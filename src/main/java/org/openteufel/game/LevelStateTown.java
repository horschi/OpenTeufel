package org.openteufel.game;

import java.io.IOException;

import org.openteufel.file.GamedataLoader;
import org.openteufel.file.dun.DUNFile;

public class LevelStateTown extends LevelState
{
    public LevelStateTown(final GamedataLoader dataLoader) throws IOException
    {
        super(dataLoader);
    }

    protected String getPALPath()
    {
        return "levels\\towndata\\town.pal";
    }

    public String getCELPath()
    {
        return "nlevels\\towndata\\town.cel";
    }

    protected String getMINPath()
    {
        return "nlevels\\towndata\\town.min";
    }

    protected int getMINBlockSize()
    {
        return 16;
    }

    protected String getTILPath()
    {
        return "nlevels\\towndata\\town.til";
    }

    protected String getSOLPath()
    {
        return "nlevels\\towndata\\town.sol";
    }

    protected DUNFile loadDUN(final GamedataLoader dataLoader) throws IOException
    {
        final DUNFile[] townPieces = new DUNFile[4];
        townPieces[0] = new DUNFile(dataLoader.getFileByteBuffer("levels\\towndata\\sector1s.dun"));
        townPieces[1] = new DUNFile(dataLoader.getFileByteBuffer("levels\\towndata\\sector2s_openCleft.dun"));
        townPieces[2] = new DUNFile(dataLoader.getFileByteBuffer("levels\\towndata\\sector3s.dun"));
        townPieces[3] = new DUNFile(dataLoader.getFileByteBuffer("levels\\towndata\\sector4s.dun"));
        final DUNFile town = DUNFile.townmerge(townPieces);
        return town;
    }
}
