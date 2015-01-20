package org.openteufel.game.entities.monsters;

import org.openteufel.game.Entity;
import org.openteufel.game.WorldCallback;
import org.openteufel.game.entities.RangedMonsterEntity;
import org.openteufel.game.entities.missiles.ApocalypseExplosionEntity;
import org.openteufel.game.entities.missiles.BloodstarEntity;
import org.openteufel.game.entities.missiles.SuccubusBloodstarEntity;
import org.openteufel.game.utils.Position2d;

public class DiabloEntitiy extends RangedMonsterEntity
{
    public DiabloEntitiy(Position2d pos)
    {
        super(pos, 5);
    }

    @Override
    protected Entity[] getAdditionalPreloadEntitites()
    {
        return new Entity[] { new ApocalypseExplosionEntity(getPos().clone(), getPos().clone(), getTeam()) };
    }

    @Override
    protected String getCelPathStand()
    {
        return "monsters\\diablo\\diablon.cl2";
    }

    @Override
    protected String getCelPathWalk()
    {
        return "monsters\\diablo\\diablow.cl2";
    }

    @Override
    protected String getCelPathAttack()
    {
        return "monsters\\diablo\\diabloa.cl2";
    }

    @Override
    protected String getCelPathHit()
    {
        return "monsters\\diablo\\diabloh.cl2";
    }

    @Override
    protected String getCelPathDeath()
    {
        return "monsters\\diablo\\diablod.cl2";
    }

    @Override
    protected int getNumFramesStand()
    {
        return 16;
    }

    @Override
    protected int getNumFramesWalk()
    {
        return 6;
    }

    @Override
    protected int getNumFramesAttack()
    {
        return 16;
    }

    @Override
    protected int getNumFramesHit()
    {
        return 6;
    }

    @Override
    protected int getNumFramesDeath()
    {
        return 16;
    }

    @Override
    protected int getFrameAttack()
    {
        return 10;
    }

    @Override
    protected int getMaxLookDist()
    {
        return 32 * 50;
    }

    @Override
    protected int getMaxAttackDist()
    {
        return 32 * 15;
    }

    @Override
    protected int getMinAttackDist()
    {
        return 32 * 0;
    }

    @Override
    protected void performMonsterAttack(int gametime, WorldCallback world, Entity targetEntity)
    {
        world.addEntity(new ApocalypseExplosionEntity(this.getPos().clone(), targetEntity.getPos().clone(), getTeam()));
    }
}
