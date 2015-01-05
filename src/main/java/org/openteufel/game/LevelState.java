package org.openteufel.game;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openteufel.file.GamedataLoader;
import org.openteufel.file.cel.PALFile;
import org.openteufel.file.dun.DUNFile;
import org.openteufel.file.dun.MINFile;
import org.openteufel.file.dun.MINPillar;
import org.openteufel.file.dun.TILFile;
import org.openteufel.file.dun.TILSquare;
import org.openteufel.game.entities.DummyEntity;
import org.openteufel.game.entities.GoldEntity;
import org.openteufel.game.entities.NPCEntity;
import org.openteufel.game.entities.townnpcs.NPCBlacksmithEntity;
import org.openteufel.game.entities.townnpcs.NPCStorytellerEntity;

public abstract class LevelState
{
    private final GamedataLoader dataLoader;

    private final PALFile        pal;
    private final MINFile        min;
    private final TILFile        til;

    private final DUNFile        dun;

    private final EntityManager  entityManager;

    public LevelState(final GamedataLoader dataLoader) throws IOException
    {
        this.dataLoader = dataLoader;

        this.pal = new PALFile(dataLoader.getFileByteBuffer(this.getPALPath()));
        this.min = new MINFile(dataLoader.getFileByteBuffer(this.getMINPath()), this.getMINBlockSize());
        this.til = new TILFile(dataLoader.getFileByteBuffer(this.getTILPath()));

        this.dun = this.loadDUN(dataLoader);
        this.entityManager = new EntityManager();
        this.placeEntities(this.entityManager);

        for (int y = 0; y < this.dun.getHeight(); y++)
        {
            for (int x = 0; x < this.dun.getWidth(); x++)
            {
                final short monster = this.dun.getMonster(x, y);
                if (monster != 0)
                {
                    this.entityManager.addEntity(new DummyEntity(x * 64, y * 64, "m" + monster));
                }
                final short object = this.dun.getObject(x, y);
                if (object != 0)
                {
                    this.entityManager.addEntity(new DummyEntity(x * 64, y * 64, "o" + object));
                }
            }
        }

    }

    public EntityManager getEntityManager()
    {
        return this.entityManager;
    }

    protected abstract String getPALPath();

    public abstract String getCELPath();

    protected abstract String getMINPath();

    protected abstract int getMINBlockSize();

    protected abstract String getTILPath();

    protected abstract String getSOLPath();

    protected abstract DUNFile loadDUN(GamedataLoader dataLoader) throws IOException;

    protected abstract void placeEntities(EntityManager entityManager);

    public TILSquare getSquare(final int worldX, final int worldY)
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
        final List<Integer> frameIdsPlus1 = new ArrayList<Integer>(this.min.getPillars().length * 10);
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

    public void runFrame(final int gametime)
    {
        this.entityManager.process(gametime);
    }

    int camX, camY;
    public int getCameraX()
    {
        return this.camX;
    }

    public int getCameraY()
    {
        return this.camY;
    }

    public void updateCamPos(final int offX, final int offY)
    {
        this.camX += offX;
        this.camY += offY;
    }
}
