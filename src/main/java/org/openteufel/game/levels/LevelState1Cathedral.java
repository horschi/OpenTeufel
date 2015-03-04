package org.openteufel.game.levels;

import java.io.IOException;

import org.openteufel.file.GamedataLoader;
import org.openteufel.file.dun.DUNFile;
import org.openteufel.game.EntityManager;
import org.openteufel.game.LevelState;
import org.openteufel.game.levels.gen.DUNConstants1Cathedral;
import org.openteufel.game.levels.gen.LevelGenerator;
import org.openteufel.game.utils.Position2d;

public class LevelState1Cathedral extends LevelState
{
    public LevelState1Cathedral()
    {
    }

    @Override
    protected void initInternal(final GamedataLoader dataLoader) throws IOException
    {

    }

    @Override
    protected String getPALPath()
    {
        return "levels\\l1data\\l1.pal";
    }

    @Override
    public String getCELPath()
    {
        return "levels\\l1data\\l1.cel";
    }

    @Override
    protected String getMINPath()
    {
        return "levels\\l1data\\l1.min";
    }

    @Override
    protected int getMINBlockSize()
    {
        return 10;
    }

    @Override
    protected String getTILPath()
    {
        return "levels\\l1data\\l1.til";
    }

    @Override
    protected String getSOLPath()
    {
        return "levels\\l1data\\l1.sol";
    }

    @Override
    protected DUNFile loadDUN(final GamedataLoader dataLoader) throws IOException
    {
        // vile1 vile2 skngdo skngdc
        // hero1 hero2
        // sklkng sklkng1 sklkng2
        // banner1 banner2
        // lv1mazea lv1mazeb
        // rnd1 rnd2 rnd3 rnd4 rnd5 rnd6
//        return new DUNFile(dataLoader.getFileByteBuffer("levels\\l1data\\lv1mazea.dun"));
        
        return new LevelGenerator(16, 16, new DUNConstants1Cathedral(), new DUNFile[]{}).getResult();
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
