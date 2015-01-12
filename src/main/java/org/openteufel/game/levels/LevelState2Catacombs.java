package org.openteufel.game.levels;

import java.io.IOException;

import org.openteufel.file.GamedataLoader;
import org.openteufel.file.dun.DUNFile;
import org.openteufel.game.EntityManager;
import org.openteufel.game.LevelState;
import org.openteufel.game.utils.Position2d;

public class LevelState2Catacombs extends LevelState
{
    public LevelState2Catacombs(final GamedataLoader dataLoader) throws IOException
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
        return "levels\\l2data\\l2.pal";
    }

    @Override
    public String getCELPath()
    {
        return "levels\\l2data\\l2.cel";
    }

    @Override
    protected String getMINPath()
    {
        return "levels\\l2data\\l2.min";
    }

    @Override
    protected int getMINBlockSize()
    {
        return 10;
    }

    @Override
    protected String getTILPath()
    {
        return "levels\\l2data\\l2.til";
    }

    @Override
    protected String getSOLPath()
    {
        return "levels\\l2data\\l2.sol";
    }

    @Override
    protected DUNFile loadDUN(final GamedataLoader dataLoader) throws IOException
    {
        // blind1 blind2
        // blood1 blood2 blood3
        // bonecha1 bonecha2
        // bonestr1 bonestr2
        return new DUNFile(dataLoader.getFileByteBuffer("levels\\l2data\\bonestr1.dun"));
    }

    @Override
    protected void placeEntities(final EntityManager entityManager)
    {

    }

    @Override
    protected Position2d getStartPosition()
    {
        return Position2d.byTile(1, 1);
    }
}
