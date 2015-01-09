package org.openteufel.file.cel;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class CELFile
{
    private final boolean      isCl2;
    private final List<byte[]> frames = new ArrayList<byte[]>();
    private final int          animLength;

    public CELFile(final ByteBuffer in, final boolean isCl2)
    {
        this.isCl2 = isCl2;

        final int magic = in.getInt();
        // If the first uint16_t in the file is 32,
        // then it is a cel archive, containing 8 cels,
        // each of which is a collection of frames
        // representing an animation of an object at
        // one of the eight possible rotations.
        // This is a side effect of cel archives containing
        // a header liek the normal cel header pointing to
        // each of the cels it contains, and there always being
        // 8 cels in each cel archive, so 8*4=32, the start
        // of the first cel
        if (magic == 32)
        {
            if (isCl2)
            {
                in.position(0);
                this.animLength = this.readCl2ArchiveFrames(in);
            }
            else
            {
                in.position(32);
                this.animLength = this.readCelArchiveFrames(in);
            }
        }
        else
        {
            in.position(0);
            this.animLength = this.readNormalFrames(in, null);
        }

    }

    private int readNormalFrames(final ByteBuffer in, final Integer offset)
    {
        if (offset != null)
            in.position(offset);

        final int numFrames = in.getInt();

        final int[] frameOffsets = new int[numFrames + 1];
        for (int i = 0; i <= numFrames; i++)
            frameOffsets[i] = in.getInt();

        if (offset != null)
            in.position(offset + frameOffsets[0]);

        for (int i = 0; i < numFrames; i++)
        {
            final byte[] data = new byte[frameOffsets[i + 1] - frameOffsets[i]];
            in.get(data);
            this.frames.add(data);
        }

        return numFrames;
    }

    private int readCl2ArchiveFrames(final ByteBuffer in)
    {
        final int[] headerOffsets = new int[8];
        for (int i = 0; i < 8; i++)
            headerOffsets[i] = in.getInt();

        int numFrames = 0;
        for (int i = 0; i < 8; i++)
        {
            final int newAnimLen = this.readNormalFrames(in, headerOffsets[i]);
            if (i == 0)
                numFrames = newAnimLen;
            else if (numFrames != newAnimLen)
                throw new IllegalStateException("Found variable animLength: " + numFrames + "!=" + newAnimLen);
        }
        return numFrames;
    }

    private int readCelArchiveFrames(final ByteBuffer in)
    {
        int numFrames = 0;
        for (int i = 0; i < 8; i++)
        {
            final int newAnimLen = this.readNormalFrames(in, null);
            if (i == 0)
                numFrames = newAnimLen;
            else if (numFrames != newAnimLen)
                throw new IllegalStateException("Found variable animLength: " + numFrames + "!=" + newAnimLen);
        }
        return numFrames;
    }

    //

    public int getTotalFrames()
    {
        return this.frames.size();
    }

    public int getFramePerAnim()
    {
        return this.animLength;
    }

    private byte[] getFrameRaw(final int frame)
    {
        if (frame > this.getTotalFrames() || frame < 0)
            throw new IllegalArgumentException("Illegal frame: " + frame);
        return this.frames.get(frame);
    }

    private int[] getFramePixelsRaw(final int frame, final PALFile pal)
    {
        final byte[] raw = this.getFrameRaw(frame);
        final int num = raw.length;
        final int[] ret = new int[num];
        for (int i = 0; i < num; i++)
        {
            ret[i] = pal.getColor(raw[i]);
        }
        return ret;
    }

    public int[] getFramePixelsType0(final int frame, final PALFile pal)
    {
        final byte[] raw = this.getFrameRaw(frame);
        final int num = raw.length;
        int wh;
        switch (num)
        {
            case 32 * 32:
                wh = 32;
                break;
            default:
                throw new IllegalStateException("Cannot load type 0 CEL: size=" + num);
        }
        final int[] ret = new int[num];
        for (int y = 0; y < wh; y++)
        {
            for (int x = 0; x < wh; x++)
            {
                ret[x + ((wh - 1 - y) * wh)] = pal.getColor(raw[x + (y * wh)]);
            }
        }
        return ret;
    }

    public int[] getFramePixelsType1Sparse(final int frame, final PALFile pal, final int w, final int h)
    {
        final ByteBuffer raw = ByteBuffer.wrap(this.getFrameRaw(frame));
        final IntBuffer ret = IntBuffer.allocate(w * h);
        final int realWidth = w;
        while (raw.remaining() > 0)
        {
            byte b = raw.get();
            if (b == -128)
            {
                throw new IllegalStateException("Found byte: " + b + " / remaining bytes: " + raw.remaining());
            }
            else if (b < 0)
            {
                for (; b < 0; b++)
                    ret.put(PALFile.packColor(0, 0, 0, 0));
            }
            else if (b > 0)
            {
                for (; b > 0; b--)
                {
                    ret.put(pal.getColor(raw.get()));
                }
            }
        }
        final int[] arr = new int[w * h];
        ret.rewind();

        for (int y = h - 1; y >= 0; y--)
        {
            for (int x = 0; x < w; x++)
            {
                if (ret.remaining() <= 0)
                    break;
                arr[x + (y * w)] = ret.get();
            }
        }
        return arr;
    }

    public CELFrame getFramePixelsObject(final int frame, final PALFile pal)
    {
        final ByteBuffer raw = ByteBuffer.wrap(this.getFrameRaw(frame));
        final IntBuffer ret = IntBuffer.allocate(256 * 256); // max supported size

        final byte h0 = raw.get();
        final byte h1 = raw.get();
        final byte h2 = raw.get();
        final byte h3 = raw.get();
        final byte h4 = raw.get();
        final byte h5 = raw.get();
        final byte h6 = raw.get();
        final byte h7 = raw.get();
        final byte h8 = raw.get();
        final byte h9 = raw.get();
        final int offset32 = (h2 & 0xff) | ((h3 & 0xff) << 8);
        final int offset64 = (h4 & 0xff) | ((h5 & 0xff) << 8);
        final int offset96 = (h6 & 0xff) | ((h7 & 0xff) << 8);
        final int offset128 = (h8 & 0xff) | ((h9 & 0xff) << 8);
        //        System.out.println("frame=" + frame + " : h1=" + h1 + " / offset32=" + offset32 + " / offset64=" + offset64 + " / offset96=" + offset96 + " / offset128=" + offset128);

        if (h0 != 10)
            throw new IllegalStateException("Could not find frame header");
        if (offset32 == 0)
            return null;

        int width = 0;
        while(raw.remaining() > 0)
        {
            int b = raw.get();
            if (this.isCl2)
            {
                if (b > 0)
                {
                    for (; b > 0; b--)
                    {
                        ret.put(PALFile.packColor(0, 0, 0, 0));
                    }
                    if (raw.position() == offset32)
                        width = ret.position() / 32;
                }
                else if (b < 0)
                {
                    b = -b;
                    if (b <= 65)
                    {
                        for (; b > 0; b--)
                        {
                            ret.put(pal.getColor(raw.get()));
                            if (raw.position() == offset32)
                                width = ret.position() / 32;
                        }
                    }
                    else
                    {
                        b -= 65;
                        final int c = pal.getColor(raw.get());
                        for (; b > 0; b--)
                        {
                            ret.put(c);
                            if (raw.position() == offset32)
                                width = ret.position() / 32;
                        }
                    }
                }
            }
            else
            {
                if (b < 0)
                {
                    for (; b < 0; b++)
                    {
                        ret.put(PALFile.packColor(0, 0, 0, 0));
                    }
                    if (raw.position() == offset32)
                        width = ret.position() / 32;
                }
                else if (b > 0)
                {
                    for (; b > 0; b--)
                    {
                        ret.put(pal.getColor(raw.get()));
                        if (raw.position() == offset32)
                            width = ret.position() / 32;
                    }
                }
            }
        }

        final int height = ret.position() / width;
        final int[] arr = new int[width * height];
        ret.rewind();

        for (int y = height - 1; y >= 0; y--)
        {
            for (int x = 0; x < width; x++)
            {
                if (ret.remaining() <= 0)
                    break;
                arr[x + (y * width)] = ret.get();
            }
        }
        return new CELFrame(arr, width, height);
    }



    private static final int[] decoderRowSizesType2HalfTileLeft    = new int[] { 4, 4, 8, 8, 12, 12, 16, 16, 20, 20, 24, 24, 28, 28, 32, 32, 32, 28, 28, 24, 24, 20, 20, 16, 16, 12, 12, 8, 8, 4, 4, 0 };
    private static final int[] decoderRowSizesType3HalfTileRight   = new int[] { 4, 4, 8, 8, 12, 12, 16, 16, 20, 20, 24, 24, 28, 28, 32, 32, 32, 28, 28, 24, 24, 20, 20, 16, 16, 12, 12, 8, 8, 4, 4, 0 };
    private static final int[] decoderRowSizesType4HalfTrapezLeft  = new int[] { 4, 4, 8, 8, 12, 12, 16, 16, 20, 20, 24, 24, 28, 28, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32 };
    private static final int[] decoderRowSizesType5HalfTrapezRight = new int[] { 4, 4, 8, 8, 12, 12, 16, 16, 20, 20, 24, 24, 28, 28, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32 };

    private int[] getFramePixelsDecoded(final int frame, final PALFile pal, final int w, final int h, final boolean left, final int[] decoderRowSizes)
    {
        final int[] outbuf = new int[w*h];
        final int[] inbuf = this.getFramePixelsRaw(frame, pal);
        int inIdx = 0;
        for(int y=0;y<h;y++)
        {
            final int xsize = decoderRowSizes[y];
            if(xsize > w)
                throw new IllegalStateException();
            final int xoff = left ? (w-xsize) : 0;
            for(int x=0;x<xsize;x++)
            {
                final int v = inbuf[inIdx++];
                if ((y & 1) == 0 && (v & 0xffffff) == 0)
                {
                    if (left)
                    {
                        if (x <= 2)
                            continue;
                    }
                    else
                    {
                        if (x >= xsize - 2)
                            continue;
                    }
                }
                outbuf[xoff + x + ((h - 1 - y) * w)] = v;
            }
        }
        return outbuf;
    }

    public int[] getFramePixelsType2HalfTileLeft(final int frame, final PALFile pal)
    {
        return this.getFramePixelsDecoded(frame, pal, 32, 32, true, decoderRowSizesType2HalfTileLeft);
    }

    public int[] getFramePixelsType3HalfTileRight(final int frame, final PALFile pal)
    {
        return this.getFramePixelsDecoded(frame, pal, 32, 32, false, decoderRowSizesType3HalfTileRight);
    }


    public int[] getFramePixelsType4HalfTrapezLeft(final int frame, final PALFile pal)
    {
        return this.getFramePixelsDecoded(frame, pal, 32, 32, true, decoderRowSizesType4HalfTrapezLeft);
    }

    public int[] getFramePixelsType5HalfTrapezRight(final int frame, final PALFile pal)
    {
        return this.getFramePixelsDecoded(frame, pal, 32, 32, false, decoderRowSizesType5HalfTrapezRight);
    }

    //

    public int[] getFramePixelsTypeManual(final int frame, final PALFile pal, final int type)
    {
        switch (type)
        {
            case 0:
                return this.getFramePixelsType0(frame, pal);
            case 1:
                return this.getFramePixelsType1Sparse(frame, pal, 32, 32);
            case 2:
                return this.getFramePixelsType2HalfTileLeft(frame, pal);
            case 3:
                return this.getFramePixelsType3HalfTileRight(frame, pal);
            case 4:
                return this.getFramePixelsType4HalfTrapezLeft(frame, pal);
            case 5:
                return this.getFramePixelsType5HalfTrapezRight(frame, pal);

            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString()
    {
        return "CELFile [isCl2=" + this.isCl2 + ", frames=" + this.frames.size() + ", animLength=" + this.animLength + "]";
    }
}
