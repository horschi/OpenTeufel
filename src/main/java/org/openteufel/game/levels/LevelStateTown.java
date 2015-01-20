package org.openteufel.game.levels;

import java.io.IOException;

import org.openteufel.file.GamedataLoader;
import org.openteufel.file.dun.DUNFile;
import org.openteufel.game.EntityManager;
import org.openteufel.game.LevelState;
import org.openteufel.game.entities.monsters.BlackKnightEntitiy;
import org.openteufel.game.entities.monsters.DiabloEntitiy;
import org.openteufel.game.entities.monsters.SnakeEntitiy;
import org.openteufel.game.entities.monsters.SuccubusEntitiy;
import org.openteufel.game.entities.player.PlayerEntity;
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
import org.openteufel.game.utils.Position2d;

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

        entityManager.addEntity(new NPCBlacksmithEntity(Position2d.byTile(62, 63)));
        entityManager.addEntity(new NPCStorytellerEntity(Position2d.byTile(62, 71)));
        entityManager.addEntity(new NPCHealerEntity(Position2d.byTile(55, 79)));
        entityManager.addEntity(new NPCDrunkEntity(Position2d.byTile(71, 84)));
        entityManager.addEntity(new NPCOgdenEntity(Position2d.byTile(55, 62)));
        entityManager.addEntity(new NPCPegKidEntity(Position2d.byTile(11, 53)));
        entityManager.addEntity(new NPCPriestEntity(Position2d.byTile(31, 50)));
        entityManager.addEntity(new NPCWoundedEntity(Position2d.byTile(23, 32)));

        entityManager.addEntity(new NPCCowEntity(Position2d.byTile(56, 14), 3));
        entityManager.addEntity(new NPCCowEntity(Position2d.byTile(58, 16), 1));
        entityManager.addEntity(new NPCCowEntity(Position2d.byTile(59, 20), 4));

        if (this.hasTheHell || this.hasHellfire)
        {
            entityManager.addEntity(new NPCFarmerEntity(Position2d.byTile(61, 22)));
            entityManager.addEntity(new NPCGirlEntity(Position2d.byTile(77, 43)));
        }
        if (this.hasTheHell)
        {
            entityManager.addEntity(new NPCWitchEntity(Position2d.byTile(44, 68)));
            entityManager.addEntity(new NPCGillianEntity(Position2d.byTile(55, 44)));
        }
        else
        {
            entityManager.addEntity(new NPCWitchEntity(Position2d.byTile(80, 20)));
            entityManager.addEntity(new NPCGillianEntity(Position2d.byTile(43, 66)));
        }

        //        entityManager.addEntity(new GoldEntity(58, 71, 1000));
        //        this.entityManager.addEntity(new GoldEntity(1, 0, 1000));
        //        this.entityManager.addEntity(new GoldEntity(0, 1, 1000));
        //        this.entityManager.addEntity(new GoldEntity(1, 1, 1000));

        entityManager.addEntity(new SuccubusEntitiy(Position2d.byTile(65, 70)));
        entityManager.addEntity(new BlackKnightEntitiy(Position2d.byTile(66, 70)));
        entityManager.addEntity(new DiabloEntitiy(Position2d.byTile(67, 70)));
        entityManager.addEntity(new SnakeEntitiy(Position2d.byTile(65, 69)));
    }

    @Override
    protected Position2d getStartPosition()
    {
        return Position2d.byTile(75, 75);
    }
}
