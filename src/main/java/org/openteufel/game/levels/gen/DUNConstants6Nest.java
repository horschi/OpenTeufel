package org.openteufel.game.levels.gen;

public class DUNConstants6Nest implements DUNConstants
{
    @Override
    public short getFloor()
    {
        return 7;
    }

    @Override
    public short getBlank()
    {
        return 8;
    }

    @Override
    public short getXWall()
    {
        return 10;
    }

    @Override
    public short getXBackWall()
    {
        return 2;
    }

    @Override
    public short getYWall()
    {
        return 9;
    }

    @Override
    public short getYBackWall()
    {
        return 4;
    }

    public short getXEnd()
    {
        return -1;
    }

    public short getYEnd()
    {
        return -1;
    }

    public short getTopCornerWall()
    {
        return 11;
    }

    public short getTopCornerBackWall()
    {
        return 5;
    }

    public short getBottomCornerWall()
    {
        return 12;
    }

    public short getBottomCornerBackWall()
    {
        return 6;
    }

    public short getLeftCornerWall()
    {
        return 13;
    }

    public short getLeftCornerBackWall()
    {
        return 1;
    }

    public short getRightCornerWall()
    {
        return 14;
    }

    public short getRightCornerBackWall()
    {
        return 3;
    }
}
