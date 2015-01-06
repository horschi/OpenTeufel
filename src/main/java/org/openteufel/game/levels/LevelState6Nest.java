package org.openteufel.game.levels;

import java.io.IOException;

import org.openteufel.file.GamedataLoader;
import org.openteufel.file.dun.DUNFile;
import org.openteufel.game.EntityManager;
import org.openteufel.game.LevelState;

public class LevelState6Nest extends LevelState
{
    public LevelState6Nest(final GamedataLoader dataLoader) throws IOException
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
        return "nlevels\\l6data\\l6base1.pal";
    }

    @Override
    public String getCELPath()
    {
        return "nlevels\\l6data\\l6.CEL";
    }

    @Override
    protected String getMINPath()
    {
        return "nlevels\\l6data\\l6.min";
    }

    @Override
    protected int getMINBlockSize()
    {
        return 10;
    }

    @Override
    protected String getTILPath()
    {
        return "nlevels\\l6data\\l6.til";
    }

    @Override
    protected String getSOLPath()
    {
        return "levels\\l6data\\l6.sol";
    }

    @Override
    protected DUNFile loadDUN(final GamedataLoader dataLoader) throws IOException
    {
        return new DUNFile(dataLoader.getFileByteBuffer("levels\\l6data\\valley01.dun"));
    }

    @Override
    protected void placeEntities(final EntityManager entityManager)
    {

    }
}
