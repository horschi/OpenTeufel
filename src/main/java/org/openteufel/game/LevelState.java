package org.openteufel.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openteufel.file.GamedataLoader;
import org.openteufel.file.cel.PALFile;
import org.openteufel.file.dun.DUNFile;
import org.openteufel.file.dun.MINFile;
import org.openteufel.file.dun.MINPillar;
import org.openteufel.file.dun.SOLFile;
import org.openteufel.file.dun.TILFile;
import org.openteufel.game.entities.player.PlayerEntity;
import org.openteufel.game.utils.Position2d;

public abstract class LevelState implements WorldCallback
{
    private GamedataLoader dataLoader = null;

    private PALFile        pal;
    private MINFile        min;
    private TILFile        til;
    private SOLFile        sol;

    private DUNFile        dun;

    private EntityManager  entityManager;
    private PlayerEntity   playerEntity;

    public LevelState()
    {
    }

    public EntityManager getEntityManager()
    {
        return this.entityManager;
    }

    public final void init(GamedataLoader dataLoader) throws IOException
    {
        this.dataLoader = dataLoader;

        this.initInternal(dataLoader);

        this.pal = new PALFile(dataLoader.getFileByteBuffer(this.getPALPath()));
        this.min = new MINFile(dataLoader.getFileByteBuffer(this.getMINPath()), this.getMINBlockSize());
        this.til = new TILFile(dataLoader.getFileByteBuffer(this.getTILPath()));
        this.sol = new SOLFile(dataLoader.getFileByteBuffer(this.getSOLPath()));

        this.dun = this.loadDUN(dataLoader);
        this.entityManager = new EntityManager();
        this.placeEntities(this.entityManager);

        for (int y = 0; y < this.dun.getHeight() * 2; y++)
        {
            for (int x = 0; x < this.dun.getWidth() * 2; x++)
            {
                final short monster = this.dun.getMonster(x, y);
                if (monster != 0)
                {
                    this.entityManager.addEntity(DUNMonsterFactory.createEntityFromMonsterId(monster, x, y));
                }
                final short object = this.dun.getObject(x, y);
                if (object != 0)
                {
                    this.entityManager.addEntity(DUNObjectFactory.createEntityFromObjectId(object, x, y));
                }
                final short trans = this.dun.getTransparencies(x, y);
                if (trans != 0)
                {
                    //                    this.entityManager.addEntity(new DummyEntity(x * 32, y * 32, "t" + trans));
                }
            }
        }

        this.playerEntity = new PlayerEntity(this.getStartPosition(), PlayerEntity.CLASS_ROGUE, true);
        this.entityManager.addEntity(this.playerEntity);
    }

    protected abstract void initInternal(GamedataLoader dataLoader) throws IOException;

    protected abstract String getPALPath();

    public abstract String getCELPath();

    protected abstract String getMINPath();

    protected abstract int getMINBlockSize();

    protected abstract String getTILPath();

    protected abstract String getSOLPath();

    protected abstract DUNFile loadDUN(GamedataLoader dataLoader) throws IOException;

    protected abstract void placeEntities(EntityManager entityManager);

    protected abstract Position2d getStartPosition();

    public int getSquareId(final int worldX, final int worldY)
    {
        return this.dun.getSquare(worldX, worldY);
    }

    public short[] getSquare(final int worldX, final int worldY)
    {
        final int squareId = this.dun.getSquare(worldX, worldY);
        if (squareId < 0)
            return null;

        return this.til.getSquare(squareId);
    }

    public MINPillar getPillar(final int pillarId)
    {
        return this.min.getPillar(pillarId & 0xffff);
    }

    public List<Integer> getAllFrameIdsPlus1Pillars()
    {
        final List<Integer> frameIdsPlus1 = new ArrayList<Integer>(this.min.getPillars().length * 4);
        for (final MINPillar pillar : this.min.getPillars())
        {
            for (final short frameNumPlus1 : pillar.getFrameNumsPlus1())
            {
                if (frameNumPlus1 > 0)
                    frameIdsPlus1.add(new Integer(frameNumPlus1));
            }
        }
        return frameIdsPlus1;
    }

    public PALFile getPalette()
    {
        return this.pal;
    }

    public LevelState runFrame(final int gametime)
    {
        this.entityManager.process(gametime, this);
        
        if(playerEntity.getPos().hasOffset())
            return null;
        return this.checkLevelChange(playerEntity.getPos().getTileX(), playerEntity.getPos().getTileY());
    }

    public abstract double getBaseBrightness();
    
    public double calculateBrightness(final int posX, final int posY)
    {
        Position2d cam = getCameraPos();
        int diffX = cam.getPosX() - posX;
        int diffY = cam.getPosY() - posY;
        double brightness = getBaseBrightness() - (Math.sqrt((diffX * diffX) + (diffY * diffY)) / 700);
        if (brightness > 1.0)
            brightness = 1.0;
        if (brightness < 0.0)
            brightness = 0.0;
        return brightness;
    }
    
    public Position2d getCameraPos()
    {
        return this.playerEntity.getPos();
    }

    public void updateCamPos(final int offX, final int offY)
    {
        this.playerEntity.updateTarget(this.playerEntity.getPos().addNew(offX, offY));
    }
    
    @Override
    public void addEntity(final Entity ent)
    {
        entityManager.addEntity(ent);
    }

    @Override
    public Entity getEntityClosest(final int x, final int y, final int maxradius, int team)
    {
        return entityManager.getEntityClosest(x, y, maxradius, team);
    }

    @Override
    public boolean isSolid(int tileX, int tileY, boolean isRanged)
    {
        short[] square = this.getSquare(tileX >> 1, tileY >> 1);
        if(square == null)
            return true;
        int offX = tileX & 1;
        int offY = tileY & 1;

        short pillarId = square[offX + (offY << 1)];
        if(isRanged)
            return sol.getSolidBlockRange(pillarId & 0xffff);
        else
            return sol.getSolidBlock(pillarId & 0xffff);
    }
    
    @Override
    public Entity hasEntity(int tileX, int tileY, boolean onlySolid, int selectTeam)
    {
        Entity ent = entityManager.getEntityAt(tileX, tileY);
        if(ent == null)
            return null;
        if(onlySolid && !ent.isSolid())
            return null;
        if(selectTeam >= 0 && ent.getTeam() != selectTeam)
            return null;
        return ent;
    }
    
    @Override
    public boolean isWalkable(int tileX, int tileY)
    {
        if(isSolid(tileX, tileY, false))
            return false;
        
        return hasEntity(tileX, tileY, true, -1) == null;
    }
    
    public abstract LevelState checkLevelChange(int tileX, int tileY);
}
