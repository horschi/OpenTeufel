package org.openteufel.file.dun;

import java.nio.ByteBuffer;
import java.util.Arrays;

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
    private final short[] squares;
    private final short[] unknown;
    private final short[] monsters;
    private final short[] objects;
    private final short[] transparencies;

    public DUNFile(final int width, final int height)
    {
        this.width = width;
        this.height = height;
        this.numTiles = width * height;
        this.numQuarterTiles = this.numTiles * 4;

        this.squares = new short[this.numTiles];
        this.unknown = new short[this.numQuarterTiles];
        this.monsters = new short[this.numQuarterTiles];
        this.objects = new short[this.numQuarterTiles];
        this.transparencies = new short[this.numQuarterTiles];
    }

    public DUNFile(final ByteBuffer in)
    {
        this.width = in.getShort();
        this.height = in.getShort();

        this.numTiles = this.width * this.height;
        this.numQuarterTiles = this.numTiles * 4;

        this.squares = new short[this.numTiles];
        for (int i = 0; i < this.numTiles; i++)
        {
            this.squares[i] = (short) ((in.getShort() & 0xffff) - 1);
        }

        if (!in.hasRemaining())
        {
            this.unknown = null;
            this.monsters = null;
            this.objects = null;
            this.transparencies = null;
        }
        else
        {
            this.unknown = new short[this.numQuarterTiles];
            for (int i = 0; i < this.numQuarterTiles; i++)
            {
                this.unknown[i] = in.getShort();
            }

            this.monsters = new short[this.numQuarterTiles];
            for (int i = 0; i < this.numQuarterTiles; i++)
            {
                this.monsters[i] = in.getShort();
            }

            this.objects = new short[this.numQuarterTiles];
            for (int i = 0; i < this.numQuarterTiles; i++)
            {
                this.objects[i] = in.getShort();
            }

            this.transparencies = new short[this.numQuarterTiles];
            if (in.remaining() > 0)
            {
                for (int i = 0; i < this.numQuarterTiles; i++)
                {
                    this.transparencies[i] = in.getShort();
                }
            }
        }

    }

    public static DUNFile townmerge(final DUNFile... pieces)
    {
        if (pieces[0].getWidth() != pieces[1].getWidth())
            throw new IllegalArgumentException();
        if (pieces[2].getWidth() != pieces[3].getWidth())
            throw new IllegalArgumentException();

        if (pieces[0].getHeight() != pieces[2].getHeight())
            throw new IllegalArgumentException();
        if (pieces[1].getHeight() != pieces[3].getHeight())
            throw new IllegalArgumentException("" + pieces[1].getHeight() + "!=" + pieces[3].getHeight());

        final DUNFile ret = new DUNFile(pieces[0].getWidth() + pieces[2].getWidth(), pieces[0].getHeight() + pieces[1].getHeight());

        final int halfWidth = pieces[2].getWidth();
        final int halfHeight = pieces[1].getHeight();

        ret.merge(halfWidth, halfHeight, pieces[0]); // bottom
        ret.merge(halfWidth, 0, pieces[1]); // right
        ret.merge(0, halfHeight, pieces[2]); // left
        ret.merge(0, 0, pieces[3]); // top

        return ret;
    }

    public void merge(final int x, final int y, final DUNFile src)
    {
        if (x + src.width > this.width || x < 0)
            throw new IllegalStateException("Invalid x/width: " + x + "+" + src.width + " > " + this.width);
        if (y + src.height > this.height || y < 0)
            throw new IllegalStateException("Invalid y/height: " + y + "+" + src.height + " > " + this.height);

        for (int iy = 0; iy < src.height; iy++)
        {
            for (int ix = 0; ix < src.width; ix++)
            {
                this.setSquare(x + ix, y + iy, src.getSquare(ix, iy));
            }
        }
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public short getSquare(final int x, final int y)
    {
        if (x >= this.width || x < 0 || y >= this.height || y < 0)
            return -1;

        return this.squares[x + (y * this.width)];
    }

    public void setSquare(final int x, final int y, final short val)
    {
        if (x >= this.width || x < 0 || y >= this.height || y < 0)
            throw new IllegalArgumentException();

        this.squares[x + (y * this.width)] = val;
    }

    public short getMonster(final int x, final int y)
    {
        if (x >= this.width || x < 0 || y >= this.height || y < 0)
            throw new IllegalArgumentException();

        return this.monsters[x + (y * this.width)];
    }

    public void setMonster(final int x, final int y, final short val)
    {
        if (x >= this.width * 2 || x < 0 || y >= this.height * 2 || y < 0)
            throw new IllegalArgumentException();

        this.monsters[x + (y * this.width * 2)] = val;
    }

    public short getObject(final int x, final int y)
    {
        if (x >= this.width * 2 || x < 0 || y >= this.height * 2 || y < 0)
            throw new IllegalArgumentException();

        return this.objects[x + (y * this.width * 2)];
    }

    public void setObject(final int x, final int y, final short val)
    {
        if (x >= this.width * 2 || x < 0 || y >= this.height * 2 || y < 0)
            throw new IllegalArgumentException();

        this.objects[x + (y * this.width * 2)] = val;
    }

    public short getTransparencies(final int x, final int y)
    {
        if (x >= this.width * 2 || x < 0 || y >= this.height * 2 || y < 0)
            throw new IllegalArgumentException();

        return this.transparencies[x + (y * this.width * 2)];
    }

    public void setTransparencies(final int x, final int y, final short val)
    {
        if (x >= this.width * 2 || x < 0 || y >= this.height * 2 || y < 0)
            throw new IllegalArgumentException();

        this.transparencies[x + (y * this.width * 2)] = val;
    }

    @Override
    public String toString()
    {
        return "DUNFile [width=" + this.width + ", height=" + this.height + ", numTiles=" + this.numTiles + ", numQuarterTiles=" + this.numQuarterTiles + ", squares=" + Arrays.toString(this.squares) + ", unknown=" + Arrays.toString(this.unknown) + ", monsters=" + Arrays.toString(this.monsters) + ", objects=" + Arrays.toString(this.objects) + ", transparencies=" + Arrays.toString(this.transparencies) + "]";
    }
}
