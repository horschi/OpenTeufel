package org.openteufel.game.levels;

import java.io.IOException;

import org.openteufel.file.GamedataLoader;
import org.openteufel.file.dun.DUNFile;
import org.openteufel.game.EntityManager;
import org.openteufel.game.LevelState;
import org.openteufel.game.utils.Position2d;

public class LevelState5Crypt extends LevelState
{
    public LevelState5Crypt(final GamedataLoader dataLoader)
    {
    }

    @Override
    protected void initInternal(final GamedataLoader dataLoader) throws IOException
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

    @Override
    protected Position2d getStartPosition()
    {
        return Position2d.byTile(1, 1);
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
