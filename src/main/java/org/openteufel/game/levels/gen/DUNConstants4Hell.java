package org.openteufel.game.levels.gen;

public class DUNConstants4Hell implements DUNConstants
{
    @Override
    public short getFloor()
    {
        return 6;
    }

    @Override
    public short getBlank()
    {
        return 20; // 30
    }

    @Override
    public short getXWall()
    {
        return 2;
    }

    @Override
    public short getXBackWall()
    {
        return 19;
    }

    @Override
    public short getYWall()
    {
        return 1;
    }

    @Override
    public short getYBackWall()
    {
        return 18;
    }

    public short getXEnd()
    {
        return 5;
    }

    public short getYEnd()
    {
        return 4;
    }

    public short getTopCornerWall()
    {
        return 9;
    }

    public short getTopCornerBackWall()
    {
        return 21; // 22
    }

    public short getBottomCornerWall()
    {
        return 3;
    }

    public short getBottomCornerBackWall()
    {
        return -1;
    }

    public short getLeftCornerWall()
    {
        return 8;
    }

    public short getLeftCornerBackWall()
    {
        return -1;
    }

    public short getRightCornerWall()
    {
        return 7;
    }

    public short getRightCornerBackWall()
    {
        return -1;
    }
}
