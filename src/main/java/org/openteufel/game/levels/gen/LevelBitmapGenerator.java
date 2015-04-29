package org.openteufel.game.levels.gen;

public abstract class LevelBitmapGenerator
{
    protected int w;
    protected int h;
    protected boolean[] data;
    private int[]     roomSizes;
    private int[][]   roomPositions;

    public LevelBitmapGenerator()
    {
    }
    
    public void init(int w, int h, int[] roomSizes)
    {
        this.w = w;
        this.h = h;
        this.data = new boolean[w * h];
        this.roomSizes = roomSizes;
        this.roomPositions = new int[roomSizes.length][];
    }
    
    public abstract void generate();
    
    public void set(int x, int y, boolean value)
    {
        data[x + (y*w)] = value;
    }

    public void set(int x, int y, int w, int h, boolean value)
    {
        if (x + w >= this.w || y + h >= this.h)
            throw new IllegalArgumentException();
        int off = x + (y * this.w);
        for (int iy = 0; iy < h; iy++)
        {
            for (int ix = 0; ix < w; ix++)
            {
                data[off++] = value;
            }
            off += this.w - w;
        }
    }
    
    public boolean get(int x, int y)
    {
        if(x < 0 || y < 0 || x >= w  || y >= h)
            return false;
        return data[x + (y*w)];
    }

    public int getWidth()
    {
        return w;
    }

    public int getHeight()
    {
        return h;
    }

    public int[] getRoomSizes()
    {
        return roomSizes;
    }

    public int[][] getRoomPositions()
    {
        return roomPositions;
    }
 
    public void setRoomPosition(int i, int x, int y)
    {
        roomPositions[i] = new int[]{x,y};
    }
}
