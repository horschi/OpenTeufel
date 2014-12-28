package org.openteufel.file.cel;

import java.nio.ByteBuffer;
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

    
    public byte[] getFrameRaw(int frame)
    {
        return this.frames.get(frame);
    }

    public byte[] getFrameRaw(int anim, int frameInAnimation)
    {
        if(frameInAnimation > animLength)
            throw new IllegalArgumentException();
        return this.frames.get((anim*animLength) + frameInAnimation);
    }
    
    public int[] getFramePixels(int anim, int frameInAnimation, PALFile pal)
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
    
    @Override
    public String toString()
    {
        return "CELFile [isCl2=" + isCl2 + ", frames=" + frames.size() + ", animLength=" + animLength + "]";
    }
}
