package org.openteufel.game.levels.gen;

public class DUNConstants1Cathedral implements DUNConstants
{
    //  xWallEnd = 17
    //  yWallEnd = 16
    //  xDoor = 26
    //  yDoor = 25

    @Override
    public short getFloor()
    {
        return 13;
    }

    @Override
    public short getBlank()
    {
        return 22;
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

    public short getTopCornerWall()
    {
        return 4;
    }

    public short getTopCornerBackWall()
    {
        return 21;
    }

    public short getBottomCornerWall()
    {
        return 3;
    }

    public short getBottomCornerBackWall()
    {
        return 20;
    }

    public short getLeftCornerWall()
    {
        return 7;
    }

    public short getLeftCornerBackWall()
    {
        return 24;
    }

    public short getRightCornerWall()
    {
        return 6;
    }

    public short getRightCornerBackWall()
    {
        return 23;
    }
}
