package org.openteufel.game;

import java.io.IOException;

import org.openteufel.file.GamedataLoader;
import org.openteufel.file.dun.DUNFile;

public class LevelState4Hell extends LevelState
{
    public LevelState4Hell(final GamedataLoader dataLoader) throws IOException
    {
        super(dataLoader);
    }

    protected String getPALPath()
    {
        return "levels\\l4data\\l4_1.pal";
    }

    public String getCELPath()
    {
        return "levels\\l4data\\l4.cel";
    }

    protected String getMINPath()
    {
        return "levels\\l4data\\l4.min";
    }

    protected int getMINBlockSize()
    {
        return 16;
    }

    protected String getTILPath()
    {
        return "levels\\l4data\\l4.til";
    }

    protected String getSOLPath()
    {
        return "levels\\l4data\\l4.sol";
    }

    protected DUNFile loadDUN(final GamedataLoader dataLoader) throws IOException
    {
        // warlord warlord2
        // diab1
        // diab2a diab2b
        // diab3a diab3b
        // diab4a diab4b - downwards stairs
        // vile1 vile2
        return new DUNFile(dataLoader.getFileByteBuffer("levels\\l4data\\vile3.dun"));
    }
}