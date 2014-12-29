package org.openteufel.file.cel;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class CELFile
{
    private final boolean      isCl2;
    private final List<byte[]> frames = new ArrayList<byte[]>();
    private final int          animLength;

    public CELFile(ByteBuffer in, boolean isCl2)
    {
        this.isCl2 = isCl2;

        int magic = in.getInt();
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
                animLength = readCl2ArchiveFrames(in);
            }
            else
            {
                in.position(32);
                animLength = readCelArchiveFrames(in);
            }
        }
        else
        {
            in.position(0);
            animLength = readNormalFrames(in, null);
        }

    }

    private int readNormalFrames(ByteBuffer in, Integer offset)
    {
        if (offset != null)
            in.position(offset);

        int numFrames = in.getInt();

        int[] frameOffsets = new int[numFrames + 1];
        for (int i = 0; i <= numFrames; i++)
            frameOffsets[i] = in.getInt();

        if (offset != null)
            in.position(offset + frameOffsets[0]);

        for (int i = 0; i < numFrames; i++)
        {
            byte[] data = new byte[frameOffsets[i + 1] - frameOffsets[i]];
            in.get(data);
            frames.add(data);
        }

        return numFrames;
    }

    private int readCl2ArchiveFrames(ByteBuffer in)
    {
        int[] headerOffsets = new int[8];
        for (int i = 0; i < 8; i++)
            headerOffsets[i] = in.getInt();

        int numFrames = 0;
        for (int i = 0; i < 8; i++)
        {
            int newAnimLen = readNormalFrames(in, headerOffsets[i]);
            if (i == 0)
                numFrames = newAnimLen;
            else if (numFrames != newAnimLen)
                throw new IllegalStateException("Found variable animLength: " + numFrames + "!=" + newAnimLen);
        }
        return numFrames;
    }

    private int readCelArchiveFrames(ByteBuffer in)
    {
        int numFrames = 0;
        for (int i = 0; i < 8; i++)
        {
            int newAnimLen = readNormalFrames(in, null);
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
        return animLength;
    }
    
    private byte[] getFrameRaw(int frame)
    {
        return this.frames.get(frame);
    }

    private byte[] getFrameRaw(int anim, int frameInAnimation)
    {
        if(frameInAnimation > animLength)
            throw new IllegalArgumentException();
        return this.frames.get((anim*animLength) + frameInAnimation);
    }

    public int[] getFramePixelsType0(int anim, int frameInAnimation, PALFile pal)
    {
        byte[] raw = getFrameRaw(anim, frameInAnimation);
        int num = raw.length;
        int[] ret = new int[num];
        for(int i=0;i<num;i++)
        {
            ret[i] = pal.getColor(raw[i]);
        }
        return ret;
    }
    
    public int[] getFramePixelsType1Sparse(int anim, int frameInAnimation, PALFile pal)
    {
        ByteBuffer raw = ByteBuffer.wrap(getFrameRaw(anim, frameInAnimation));
        IntBuffer ret = IntBuffer.allocate(raw.remaining()*2+(32*32));
        while(raw.remaining() > 0)
        {
            byte b = raw.get();
            // TODO: resize required:  if(ret.remaining() < Math.abs(b)) { IntBuffer newret = IntBuffer.allocate(ret.capacity()*2); newret.put(ret.get) }
            if(b < 0)
            {
                for(;b<0;b++)
                    ret.put(PALFile.packColor(0, 0, 0, 0));
            }
            else if(b > 0)
            {
                for(;b>0;b--)
                    ret.put(pal.getColor(raw.get()));
            }
            // else
            //    throw new IllegalStateException("Found byte: "+b+" / remaining bytes: "+raw.remaining());
        }
        int[] arr = new int[ret.position()];
        ret.rewind();
        for(int i=arr.length-1;i>=0;i--)
        {
            arr[i] = ret.get(); // TODO: THIS mirrors left/right incorrectly, the pixels should only be reversed on the y axis!
        }
        return arr;
    }
    


    private static final int[] decoderRowSizesType2HalfTileLeft    = new int[]{ 0, 4, 4, 8, 8, 12, 12, 16, 16, 20, 20, 24, 24, 28, 28, 32, 32, 32, 28, 28, 24, 24, 20, 20, 16, 16, 12, 12, 8, 8, 4, 4};
    private static final int[] decoderRowSizesType3HalfTileRight   = new int[]{ 0, 4, 4, 8, 8, 12, 12, 16, 16, 20, 20, 24, 24, 28, 28, 32, 32, 32, 28, 28, 24, 24, 20, 20, 16, 16, 12, 12, 8, 8, 4, 4};
    private static final int[] decoderRowSizesType4HalfTrapezLeft  = new int[]{ 4, 4, 8, 8, 12, 12, 16, 16, 20, 20, 24, 24, 28, 28, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32};
    private static final int[] decoderRowSizesType5HalfTrapezRight = new int[]{ 4, 4, 8, 8, 12, 12, 16, 16, 20, 20, 24, 24, 28, 28, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32};

    private int[] getFramePixelsDecoded(int anim, int frameInAnimation, PALFile pal, int w, int h, boolean left, int[] decoderRowSizes)
    {
        int[] outbuf = new int[w*h];
        int[] inbuf = getFramePixelsType0(anim, frameInAnimation, pal);
        int inIdx = 0;
        for(int y=0;y<h;y++)
        {
            int xsize = decoderRowSizes[y];
            if(xsize > w)
                throw new IllegalStateException();
            int xoff = left ? (w-xsize) : 0;
            for(int x=0;x<xsize;x++)
            {
                outbuf[xoff+x+((h-1-y)*w)] = inbuf[inIdx++];
            }
        }
        return outbuf;
    }
    
    public int[] getFramePixelsType2HalfTileLeft(int anim, int frameInAnimation, PALFile pal)
    {
        return getFramePixelsDecoded(anim, frameInAnimation, pal, 32, 32, true, decoderRowSizesType2HalfTileLeft);
    }
    
    public int[] getFramePixelsType3HalfTileRight(int anim, int frameInAnimation, PALFile pal)
    {
        return getFramePixelsDecoded(anim, frameInAnimation, pal, 32, 32, false, decoderRowSizesType3HalfTileRight);
    }
    
    
    public int[] getFramePixelsType4HalfTrapezLeft(int anim, int frameInAnimation, PALFile pal)
    {
        return getFramePixelsDecoded(anim, frameInAnimation, pal, 32, 32, true, decoderRowSizesType4HalfTrapezLeft);
    }
    
    public int[] getFramePixelsType5HalfTrapezRight(int anim, int frameInAnimation, PALFile pal)
    {
        return getFramePixelsDecoded(anim, frameInAnimation, pal, 32, 32, false, decoderRowSizesType5HalfTrapezRight);
    }
    
    @Override
    public String toString()
    {
        return "CELFile [isCl2=" + isCl2 + ", frames=" + frames.size() + ", animLength=" + animLength + "]";
    }
}
