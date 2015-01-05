package org.openteufel.game.levels;

import java.io.IOException;

import org.openteufel.file.GamedataLoader;
import org.openteufel.file.dun.DUNFile;
import org.openteufel.game.EntityManager;
import org.openteufel.game.LevelState;
import org.openteufel.game.entities.GoldEntity;
import org.openteufel.game.entities.townnpcs.NPCBlacksmithEntity;
import org.openteufel.game.entities.townnpcs.NPCDrunkEntity;
import org.openteufel.game.entities.townnpcs.NPCGillianEntity;
import org.openteufel.game.entities.townnpcs.NPCHealerEntity;
import org.openteufel.game.entities.townnpcs.NPCOgdenEntity;
import org.openteufel.game.entities.townnpcs.NPCStorytellerEntity;
import org.openteufel.game.entities.townnpcs.NPCWitchEntity;

public class LevelStateTown extends LevelState
{
    public LevelStateTown(final GamedataLoader dataLoader) throws IOException
    {
        super(dataLoader);
    }

    @Override
    protected String getPALPath()
    {
        return "levels\\towndata\\town.pal";
    }

    @Override
    public String getCELPath()
    {
        return "nlevels\\towndata\\town.cel";
    }

    @Override
    protected String getMINPath()
    {
        return "nlevels\\towndata\\town.min";
    }

    @Override
    protected int getMINBlockSize()
    {
        return 16;
    }

    @Override
    protected String getTILPath()
    {
        return "nlevels\\towndata\\town.til";
    }

    @Override
    protected String getSOLPath()
    {
        return "nlevels\\towndata\\town.sol";
    }

    @Override
    protected DUNFile loadDUN(final GamedataLoader dataLoader) throws IOException
    {
        final DUNFile[] townPieces = new DUNFile[4];
        townPieces[0] = new DUNFile(dataLoader.getFileByteBuffer("levels\\towndata\\sector1s.dun"));
        townPieces[1] = new DUNFile(dataLoader.getFileByteBuffer("levels\\towndata\\sector2s.dun"));
        townPieces[2] = new DUNFile(dataLoader.getFileByteBuffer("levels\\towndata\\sector3s.dun"));
        townPieces[3] = new DUNFile(dataLoader.getFileByteBuffer("levels\\towndata\\sector4s.dun"));
        final DUNFile town = DUNFile.townmerge(townPieces);
        return town;
    }

    @Override
    protected void placeEntities(final EntityManager entityManager)
    {
        entityManager.addEntity(new NPCBlacksmithEntity(62 * 32, 63 * 32));
        entityManager.addEntity(new NPCStorytellerEntity(62 * 32, 71 * 32));
        entityManager.addEntity(new NPCHealerEntity(55 * 32, 79 * 32));
        entityManager.addEntity(new NPCWitchEntity(44 * 32, 68 * 32));
        entityManager.addEntity(new NPCGillianEntity(55 * 32, 44 * 32));
        entityManager.addEntity(new NPCOgdenEntity(55 * 32, 62 * 32));
        entityManager.addEntity(new NPCDrunkEntity(71 * 32, 84 * 32));

        //        entityManager.addEntity(new GoldEntity(58 * 32, 71 * 32, 1000));
        //        this.entityManager.addEntity(new GoldEntity(1 * 32, 0 * 32, 1000));
        //        this.entityManager.addEntity(new GoldEntity(0 * 32, 1 * 32, 1000));
        //        this.entityManager.addEntity(new GoldEntity(1 * 32, 1 * 32, 1000));
    }
}
