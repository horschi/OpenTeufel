package org.openteufel.game.levels;

import java.io.IOException;

import org.openteufel.file.GamedataLoader;
import org.openteufel.file.dun.DUNFile;
import org.openteufel.game.EntityManager;
import org.openteufel.game.LevelState;
import org.openteufel.game.levels.gen.DUNConstants4Hell;
import org.openteufel.game.levels.gen.DUNConstants5Crypt;
import org.openteufel.game.levels.gen.LevelGenerator;
import org.openteufel.game.utils.Position2d;

public class LevelState4Hell extends LevelState
{
    public LevelState4Hell()
    {
    }

    @Override
    protected void initInternal(final GamedataLoader dataLoader) throws IOException
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
//        return new DUNFile(dataLoader.getFileByteBuffer("levels\\l4data\\diab3b.dun"));
        return new LevelGenerator(16, 16, new DUNConstants4Hell(), new DUNFile[]{}).getResult();
    }

    @Override
    protected void placeEntities(final EntityManager entityManager)
    {

    }

    @Override
    protected Position2d getStartPosition()
    {
        return Position2d.byTile(5, 5);
    }
    
    @Override
    public double getBaseBrightness()
    {
        return 1.3;
    }
    
    @Override
    public LevelState checkLevelChange(int tileX, int tileY)
    {
        return null;
    }
}
