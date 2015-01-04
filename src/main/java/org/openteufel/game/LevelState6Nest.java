package org.openteufel.game;

import java.io.IOException;

import org.openteufel.file.GamedataLoader;
import org.openteufel.file.dun.DUNFile;

public class LevelState6Nest extends LevelState
{
    public LevelState6Nest(final GamedataLoader dataLoader) throws IOException
    {
        super(dataLoader);
    }

    protected String getPALPath()
    {
        return "nlevels\\l6data\\l6base1.pal";
    }

    public String getCELPath()
    {
        return "nlevels\\l6data\\l6.CEL";
    }

    protected String getMINPath()
    {
        return "nlevels\\l6data\\l6.min";
    }

    protected int getMINBlockSize()
    {
        return 10;
    }

    protected String getTILPath()
    {
        return "nlevels\\l6data\\l6.til";
    }

    protected String getSOLPath()
    {
        return "levels\\l6data\\l6.sol";
    }

    protected DUNFile loadDUN(final GamedataLoader dataLoader) throws IOException
    {
        return new DUNFile(dataLoader.getFileByteBuffer("levels\\l6data\\valley01.dun"));
    }
}
