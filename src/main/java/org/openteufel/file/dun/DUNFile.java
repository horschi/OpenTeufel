package org.openteufel.file.dun;

import java.nio.ByteBuffer;

// DUN files contain information about how to arrange the squares, which are
// constructed based on the TIL format, in order to form a dungeon. Below is a
// description of the DUN format:
//
// DUN format:
// dunQWidth   uint16
// dunQHeight  uint16
// squareNumsPlus1 [dunQWidth][dunQHeight]uint16
// // dunWidth = 2*dunQWidth
// // dunHeight = 2*dunQHeight
// unknown         [dunWidth][dunHeight]uint16
// dunMonsterIDs   [dunWidth][dunHeight]uint16
// dunObjectIDs    [dunWidth][dunHeight]uint16
// transparencies  [dunWidth][dunHeight]uint16
public class DUNFile
{
    private final int     width;
    private final int     height;
    private final int     numTiles;
    private final int     numQuarterTiles;
    private final short[] pillars;
    private final short[] unknown;
    private final short[] dunMonsterIDs;
    private final short[] dunObjectIDs;
    private final short[] transparencies;

    public DUNFile(int width, int height)
    {
        this.width = width;
        this.height = height;
        numTiles = width * height;
        numQuarterTiles = numTiles * 4;

        pillars = new short[numTiles];
        unknown = new short[numQuarterTiles];
        dunMonsterIDs = new short[numQuarterTiles];
        dunObjectIDs = new short[numQuarterTiles];
        transparencies = new short[numQuarterTiles];
    }

    public DUNFile(ByteBuffer in)
    {
        width = in.getShort();
        height = in.getShort();

        numTiles = width * height;
        numQuarterTiles = numTiles * 4;

        
        pillars = new short[numTiles];
        for (int i = 0; i < numTiles; i++)
        {
            pillars[i] = in.getShort();
        }

        unknown = new short[numQuarterTiles];
        for (int i = 0; i < numQuarterTiles; i++)
        {
            unknown[i] = in.getShort();
        }

        dunMonsterIDs = new short[numQuarterTiles];
        for (int i = 0; i < numQuarterTiles; i++)
        {
            dunMonsterIDs[i] = in.getShort();
        }

        dunObjectIDs = new short[numQuarterTiles];
        for (int i = 0; i < numQuarterTiles; i++)
        {
            dunObjectIDs[i] = in.getShort();
        }

        transparencies = new short[numQuarterTiles];
        if(in.remaining() > 0)
        {
            for (int i = 0; i < numQuarterTiles; i++)
            {
                transparencies[i] = in.getShort();
            }
        }
    }

    public static DUNFile townmerge(DUNFile... pieces)
    {
        if (pieces[0].getWidth() != pieces[1].getWidth())
            throw new IllegalArgumentException();
        if (pieces[2].getWidth() != pieces[3].getWidth())
            throw new IllegalArgumentException();

        if (pieces[0].getHeight() != pieces[2].getHeight())
            throw new IllegalArgumentException();
        if (pieces[1].getHeight() != pieces[3].getHeight())
            throw new IllegalArgumentException();

        DUNFile ret = new DUNFile(pieces[0].getWidth() + pieces[2].getWidth(), pieces[0].getHeight() + pieces[1].getHeight());

        int halfWidth = pieces[2].getWidth();
        int halfHeight = pieces[1].getHeight();

        ret.merge(halfWidth, halfHeight, pieces[0]);
        ret.merge(halfWidth, 0, pieces[1]);
        ret.merge(0, halfHeight, pieces[2]);
        ret.merge(0, 0, pieces[3]);

        return ret;
    }

    public void merge(final int x, final int y, final DUNFile src)
    {
        if (x + src.width > width || x < 0)
            throw new IllegalStateException("Invalid x/width: " + x + "+" + src.width + " > " + width);
        if (y + src.height > height || y < 0)
            throw new IllegalStateException("Invalid y/height: " + y + "+" + src.height + " > " + height);

        for (int iy = 0; iy < src.height; iy++)
        {
            for (int ix = 0; ix < src.width; ix++)
            {
                set(x + ix, y + iy, src.get(ix, iy));
            }
        }
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public short get(int x, int y)
    {
        if (x >= width || x < 0 || y >= height || y < 0)
            throw new IllegalArgumentException();

        return pillars[x + (y * width)];
    }

    private void set(int x, int y, short val)
    {
        if (x >= width || x < 0 || y >= height || y < 0)
            throw new IllegalArgumentException();

        pillars[x + (y * width)] = val;
    }

}
