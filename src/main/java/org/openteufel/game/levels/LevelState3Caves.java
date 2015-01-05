package org.openteufel.game.levels;

import java.io.IOException;

import org.openteufel.file.GamedataLoader;
import org.openteufel.file.dun.DUNFile;
import org.openteufel.game.EntityManager;
import org.openteufel.game.LevelState;

public class LevelState3Caves extends LevelState
{
    public LevelState3Caves(final GamedataLoader dataLoader) throws IOException
    {
        super(dataLoader);
    }

    protected String getPALPath()
    {
        return "levels\\l3data\\l3pwater.pal";
    }

    public String getCELPath()
    {
        return "levels\\l3data\\l3.cel";
    }

    protected String getMINPath()
    {
        return "levels\\l3data\\l3.min";
    }

    protected int getMINBlockSize()
    {
        return 10;
    }

    protected String getTILPath()
    {
        return "levels\\l3data\\l3.til";
    }

    protected String getSOLPath()
    {
        return "levels\\l3data\\l3.sol";
    }

    protected DUNFile loadDUN(final GamedataLoader dataLoader) throws IOException
    {
        // anvil
        // foulwatr
        // lair
        return new DUNFile(dataLoader.getFileByteBuffer("levels\\l3data\\foulwatr.dun"));
    }

    @Override
    protected void placeEntities(final EntityManager entityManager)
    {

    }
}