package org.openteufel.game.levels;

import java.io.IOException;

import org.openteufel.file.GamedataLoader;
import org.openteufel.file.dun.DUNFile;
import org.openteufel.game.EntityManager;
import org.openteufel.game.LevelState;
import org.openteufel.game.entities.townnpcs.NPCBlacksmithEntity;
import org.openteufel.game.entities.townnpcs.NPCCowEntity;
import org.openteufel.game.entities.townnpcs.NPCDrunkEntity;
import org.openteufel.game.entities.townnpcs.NPCFarmerEntity;
import org.openteufel.game.entities.townnpcs.NPCGillianEntity;
import org.openteufel.game.entities.townnpcs.NPCGirlEntity;
import org.openteufel.game.entities.townnpcs.NPCHealerEntity;
import org.openteufel.game.entities.townnpcs.NPCOgdenEntity;
import org.openteufel.game.entities.townnpcs.NPCPegKidEntity;
import org.openteufel.game.entities.townnpcs.NPCPriestEntity;
import org.openteufel.game.entities.townnpcs.NPCStorytellerEntity;
import org.openteufel.game.entities.townnpcs.NPCWitchEntity;
import org.openteufel.game.entities.townnpcs.NPCWoundedEntity;

public class LevelStateTown extends LevelState
{
    private boolean hasHellfire;
    private boolean hasTheHell;

    public LevelStateTown(final GamedataLoader dataLoader) throws IOException
    {
        super(dataLoader);
    }

    @Override
    protected void init(final GamedataLoader dataLoader) throws IOException
    {
        this.hasHellfire = dataLoader.getFileByteBuffer("nlevels\\towndata\\town.cel") != null;
        this.hasTheHell = dataLoader.getFileByteBuffer("levels\\towndata\\sector2s.dun") != null;
    }

    @Override
    protected String getPALPath()
    {
        return "levels\\towndata\\town.pal";
    }

    @Override
    public String getCELPath()
    {
        if (this.hasHellfire || this.hasTheHell)
            return "nlevels\\towndata\\town.cel";
        else
            return "levels\\towndata\\town.cel";
    }

    @Override
    protected String getMINPath()
    {
        if (this.hasHellfire || this.hasTheHell)
            return "nlevels\\towndata\\town.min";
        else
            return "levels\\towndata\\town.min";
    }

    @Override
    protected int getMINBlockSize()
    {
        return 16;
    }

    @Override
    protected String getTILPath()
    {
        if (this.hasHellfire || this.hasTheHell)
            return "nlevels\\towndata\\town.til";
        else
            return "levels\\towndata\\town.til";
    }

    @Override
    protected String getSOLPath()
    {
        if (this.hasHellfire || this.hasTheHell)
            return "nlevels\\towndata\\town.sol";
        else
            return "levels\\towndata\\town.sol";
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
        entityManager.addEntity(new NPCDrunkEntity(71 * 32, 84 * 32));
        entityManager.addEntity(new NPCOgdenEntity(55 * 32, 62 * 32));
        entityManager.addEntity(new NPCPegKidEntity(11 * 32, 53 * 32));
        entityManager.addEntity(new NPCPriestEntity(31 * 32, 50 * 32));
        entityManager.addEntity(new NPCWoundedEntity(23 * 32, 32 * 32));

        entityManager.addEntity(new NPCCowEntity(56 * 32, 14 * 32, 3));
        entityManager.addEntity(new NPCCowEntity(58 * 32, 16 * 32, 1));
        entityManager.addEntity(new NPCCowEntity(59 * 32, 20 * 32, 4));

        if (this.hasTheHell || this.hasHellfire)
        {
            entityManager.addEntity(new NPCFarmerEntity(61 * 32, 22 * 32));
            entityManager.addEntity(new NPCGirlEntity(77 * 32, 43 * 32));
        }
        if (this.hasTheHell)
        {
            entityManager.addEntity(new NPCWitchEntity(44 * 32, 68 * 32));
            entityManager.addEntity(new NPCGillianEntity(55 * 32, 44 * 32));
        }
        else
        {
            entityManager.addEntity(new NPCWitchEntity(80 * 32, 20 * 32));
            entityManager.addEntity(new NPCGillianEntity(43 * 32, 66 * 32));
        }

        //        entityManager.addEntity(new GoldEntity(58 * 32, 71 * 32, 1000));
        //        this.entityManager.addEntity(new GoldEntity(1 * 32, 0 * 32, 1000));
        //        this.entityManager.addEntity(new GoldEntity(0 * 32, 1 * 32, 1000));
        //        this.entityManager.addEntity(new GoldEntity(1 * 32, 1 * 32, 1000));
    }
}
