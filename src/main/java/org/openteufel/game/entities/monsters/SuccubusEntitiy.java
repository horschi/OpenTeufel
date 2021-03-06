package org.openteufel.game.entities.monsters;

import org.openteufel.game.Entity;
import org.openteufel.game.WorldCallback;
import org.openteufel.game.entities.RangedMonsterEntity;
import org.openteufel.game.entities.missiles.BloodstarEntity;
import org.openteufel.game.entities.missiles.SuccubusBloodstarEntity;
import org.openteufel.game.utils.Position2d;

public class SuccubusEntitiy extends RangedMonsterEntity
{
    public SuccubusEntitiy(Position2d pos)
    {
        super(pos, 5);
    }

    @Override
    protected Entity[] getAdditionalPreloadEntitites()
    {
        return new Entity[] { new SuccubusBloodstarEntity(getPos().clone(), getPos().clone(), getTeam()) };
    }

    @Override
    protected String getCelPathStand()
    {
        return "monsters\\succ\\scbsn.cl2";
    }

    @Override
    protected String getCelPathWalk()
    {
        return "monsters\\succ\\scbsw.cl2";
    }

    @Override
    protected String getCelPathAttack()
    {
        return "monsters\\succ\\scbsa.cl2";
    }

    @Override
    protected String getCelPathHit()
    {
        return "monsters\\succ\\scbsh.cl2";
    }

    @Override
    protected String getCelPathDeath()
    {
        return "monsters\\succ\\scbsd.cl2";
    }

    @Override
    protected int getNumFramesStand()
    {
        return 14;
    }

    @Override
    protected int getNumFramesWalk()
    {
        return 8;
    }

    @Override
    protected int getNumFramesAttack()
    {
        return 16;
    }

    @Override
    protected int getNumFramesHit()
    {
        return 7;
    }

    @Override
    protected int getNumFramesDeath()
    {
        return 24;
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
        return 32 * 6;
    }

    @Override
    protected void performMonsterAttack(int gametime, WorldCallback world, Entity targetEntity)
    {
        world.addEntity(new SuccubusBloodstarEntity(getPos().clone(), targetEntity.getPos(), getTeam()));
    }
}
