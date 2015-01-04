package org.openteufel.game;

import java.io.IOException;

import org.openteufel.file.GamedataLoader;
import org.openteufel.file.dun.DUNFile;

public class LevelState2Catacombs extends LevelState
{
    public LevelState2Catacombs(final GamedataLoader dataLoader) throws IOException
    {
        super(dataLoader);
    }

    protected String getPALPath()
    {
        return "levels\\l2data\\l2.pal";
    }

    public String getCELPath()
    {
        return "levels\\l2data\\l2.cel";
    }

    protected String getMINPath()
    {
        return "levels\\l2data\\l2.min";
    }

    protected int getMINBlockSize()
    {
        return 10;
    }

    protected String getTILPath()
    {
        return "levels\\l2data\\l2.til";
    }

    protected String getSOLPath()
    {
        return "levels\\l2data\\l2.sol";
    }

    protected DUNFile loadDUN(final GamedataLoader dataLoader) throws IOException
    {
        // blind1 blind2
        // blood1 blood2 blood3
        // bonecha1 bonecha2
        // bonestr1 bonestr2
        return new DUNFile(dataLoader.getFileByteBuffer("levels\\l2data\\bonestr1.dun"));
    }
}
