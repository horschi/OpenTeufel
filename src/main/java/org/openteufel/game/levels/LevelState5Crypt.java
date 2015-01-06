package org.openteufel.game.levels;

import java.io.IOException;

import org.openteufel.file.GamedataLoader;
import org.openteufel.file.dun.DUNFile;
import org.openteufel.game.EntityManager;
import org.openteufel.game.LevelState;

public class LevelState5Crypt extends LevelState
{
    public LevelState5Crypt(final GamedataLoader dataLoader) throws IOException
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
        return "levels\\l5data\\l5_1.pal";
    }

    @Override
    public String getCELPath()
    {
        return "nlevels\\L5Data\\L5.CEL";
    }

    @Override
    protected String getMINPath()
    {
        return "nlevels\\l5data\\l5.min";
    }

    @Override
    protected int getMINBlockSize()
    {
        return 10;
    }

    @Override
    protected String getTILPath()
    {
        return "nlevels\\l5data\\l5.til";
    }

    @Override
    protected String getSOLPath()
    {
        return "levels\\l5data\\l5.sol";
    }

    @Override
    protected DUNFile loadDUN(final GamedataLoader dataLoader) throws IOException
    {
        // Uberoom Uberoom1 Uberoom2
        //

        return new DUNFile(dataLoader.getFileByteBuffer("levels\\l5data\\Uberoom1.dun"));
    }

    @Override
    protected void placeEntities(final EntityManager entityManager)
    {

    }
}
