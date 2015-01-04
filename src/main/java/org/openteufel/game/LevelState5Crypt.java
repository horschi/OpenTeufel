package org.openteufel.game;

import java.io.IOException;

import org.openteufel.file.GamedataLoader;
import org.openteufel.file.dun.DUNFile;

public class LevelState5Crypt extends LevelState
{
    public LevelState5Crypt(final GamedataLoader dataLoader) throws IOException
    {
        super(dataLoader);
    }

    protected String getPALPath()
    {
        return "levels\\l5data\\l5_1.pal";
    }

    public String getCELPath()
    {
        return "nlevels\\L5Data\\L5.CEL";
    }

    protected String getMINPath()
    {
        return "nlevels\\l5data\\l5.min";
    }

    protected int getMINBlockSize()
    {
        return 10;
    }

    protected String getTILPath()
    {
        return "nlevels\\l5data\\l5.til";
    }

    protected String getSOLPath()
    {
        return "levels\\l5data\\l5.sol";
    }

    protected DUNFile loadDUN(final GamedataLoader dataLoader) throws IOException
    {
        // Uberoom Uberoom1 Uberoom2
        //

        return new DUNFile(dataLoader.getFileByteBuffer("levels\\l5data\\Uberoom1.dun"));
    }
}
