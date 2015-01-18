package org.openteufel.game.utils;

public class Position2d
{
    private int tileX;
    private int tileY;
    private int offsetX;
    private int offsetY;

    private Position2d(final int tileX, final int tileY, final int offsetX, final int offsetY)
    {
        this.tileX = tileX;
        this.tileY = tileY;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public static Position2d byTile(final int tileX, final int tileY)
    {
        return new Position2d(tileX, tileY, 0, 0);
    }

    public static Position2d byPos(final int x, final int y)
    {
        final Position2d ret = new Position2d((x + 16) / 32, (y + 16) / 32, ((x + 16) % 32) - 16, ((y + 16) % 32) - 16);
        return ret;
    }

    //

    public int getTileX()
    {
        return this.tileX;
    }

    public void setTileX(final int tileX)
    {
        this.tileX = tileX;
    }

    public int getTileY()
    {
        return this.tileY;
    }

    public void setTileY(final int tileY)
    {
        this.tileY = tileY;
    }

    public int getOffsetX()
    {
        return this.offsetX;
    }

    public void setOffsetX(final int offsetX)
    {
        this.offsetX = offsetX;
    }

    public int getOffsetY()
    {
        return this.offsetY;
    }

    public boolean hasOffset()
    {
        return this.offsetX != 0 || this.offsetY != 0;
    }

    public void setOffsetY(final int offsetY)
    {
        this.offsetY = offsetY;
    }

    public int getPosX()
    {
        return (this.tileX * 32) + this.offsetX;
    }

    public int getPosY()
    {
        return (this.tileY * 32) + this.offsetY;
    }

    public void setPos(final int x, final int y)
    {
        this.tileX = (x + 16) / 32;
        this.tileY = (y + 16) / 32;
        this.offsetX = ((x + 16) % 32) - 16;
        this.offsetY = ((y + 16) % 32) - 16;
    }

    //

    private void normalizeOffsets()
    {
        if (this.offsetX > 16)
        {
            this.offsetX -= 32;
            this.tileX++;
        }
        else if (this.offsetX < -16)
        {
            this.offsetX += 32;
            this.tileX--;
        }

        if (this.offsetY > 16)
        {
            this.offsetY -= 32;
            this.tileY++;
        }
        else if (this.offsetY < -16)
        {
            this.offsetY += 32;
            this.tileY--;
        }
    }

    public boolean decreaseOffset(final int speed)
    {
        if (this.offsetX > 0)
        {
            this.offsetX -= speed;
            if (this.offsetX < 0)
                this.offsetX = 0;
        }
        else if (this.offsetX < 0)
        {
            this.offsetX += speed;
            if (this.offsetX > 0)
                this.offsetX = 0;
        }

        if (this.offsetY > 0)
        {
            this.offsetY -= speed;
            if (this.offsetY < 0)
                this.offsetY = 0;
        }
        else if (this.offsetY < 0)
        {
            this.offsetY += speed;
            if (this.offsetY > 0)
                this.offsetY = 0;
        }
        return this.offsetX == 0 && this.offsetY == 0;
    }

    public void add(final int x, final int y)
    {
        this.offsetX += x;
        this.offsetY += y;
        this.normalizeOffsets();
    }

    public void sub(final int x, final int y)
    {
        this.offsetX += x;
        this.offsetY += y;
        this.normalizeOffsets();
    }

    public Position2d clone()
    {
        return byPos(this.getPosX(), this.getPosY());
    }

    public Position2d addNew(final int x, final int y)
    {
        return byPos(this.getPosX() + x, this.getPosY() + y);
    }

    public Position2d subNew(final int x, final int y)
    {
        return byPos(this.getPosX() - x, this.getPosY() - y);
    }

    //
    public int calcDiffX(final Position2d target)
    {
        return target.getPosX() - this.getPosX();
    }

    public int calcDiffY(final Position2d target)
    {
        return target.getPosY() - this.getPosY();
    }

    public int calcDist(final Position2d target)
    {
        int dx = calcDiffX(target);
        int dy = calcDiffY(target);
        return (int) (Math.sqrt(dx * dx + dy * dy));
    }

    public Position2d calcMidPointRelative(final Position2d target, double mult)
    {
        int dx = calcDiffX(target);
        int dy = calcDiffY(target);

        return byPos((int) (getPosX() + (dx * mult)), (int) (getPosY() + (dy * mult)));
    }

    public Position2d calcMidPointAbsolute(final Position2d target, int dist)
    {
        int dx = calcDiffX(target);
        int dy = calcDiffY(target);

        double d = Math.sqrt(dx * dx + dy * dy);

        int ox = (int) ((dx * dist) / d);
        int oy = (int) ((dy * dist) / d);
        
        return byPos((getPosX() + ox), (getPosY() + oy));
    }
    
    //

    public boolean equalsTile(final Position2d other)
    {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (this.tileX != other.tileX)
            return false;
        if (this.tileY != other.tileY)
            return false;
        return true;
    }

    public boolean equals(final Position2d other)
    {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (this.getPosX() != other.getPosX())
            return false;
        if (this.getPosY() != other.getPosY())
            return false;
        return true;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.offsetX;
        result = prime * result + this.offsetY;
        result = prime * result + this.tileX;
        result = prime * result + this.tileY;
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        final Position2d other = (Position2d) obj;
        if (this.getPosX() != other.getPosX())
            return false;
        if (this.getPosY() != other.getPosY())
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "Position2d[tileX=" + this.tileX + ", tileY=" + this.tileY + ", offsetX=" + this.offsetX + ", offsetY=" + this.offsetY + "]";
    }
}
