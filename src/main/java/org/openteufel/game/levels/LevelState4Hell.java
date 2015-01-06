package org.openteufel.game.levels;

import java.io.IOException;

import org.openteufel.file.GamedataLoader;
import org.openteufel.file.dun.DUNFile;
import org.openteufel.game.EntityManager;
import org.openteufel.game.LevelState;

public class LevelState4Hell extends LevelState
{
    public LevelState4Hell(final GamedataLoader dataLoader) throws IOException
    {
        super(dataLoader);
    }

    @Override
    protected void init(final GamedataLoader dataLoader) throws IOException
    {

    }

    @Override
    protected String getPALPath()
    {
        return "levels\\l4data\\l4_1.pal";
    }

    @Override
    public String getCELPath()
    {
        return "levels\\l4data\\l4.cel";
    }

    @Override
    protected String getMINPath()
    {
        return "levels\\l4data\\l4.min";
    }

    @Override
    protected int getMINBlockSize()
    {
        return 16;
    }

    @Override
    protected String getTILPath()
    {
        return "levels\\l4data\\l4.til";
    }

    @Override
    protected String getSOLPath()
    {
        return "levels\\l4data\\l4.sol";
    }

    @Override
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

    @Override
    protected void placeEntities(final EntityManager entityManager)
    {

    }
}
