package org.openteufel.game.entities.monsters;

import org.openteufel.game.Entity;
import org.openteufel.game.entities.MeleeMonsterEntity;
import org.openteufel.game.entities.MonsterEntity;
import org.openteufel.game.entities.missiles.BloodstarEntity;
import org.openteufel.game.utils.Position2d;

public class SnakeEntitiy extends MeleeMonsterEntity
{
    public SnakeEntitiy(Position2d pos)
    {
        super(pos, 5);
    }

    @Override
    protected String getCelPathStand()
    {
        return "monsters\\snake\\snaken.cl2";
    }

    @Override
    protected String getCelPathWalk()
    {
        return "monsters\\snake\\snakew.cl2";
    }

    @Override
    protected String getCelPathAttack()
    {
        return "monsters\\snake\\snakea.cl2";
    }

    @Override
    protected String getCelPathHit()
    {
        return "monsters\\snake\\snakeh.cl2";
    }

    @Override
    protected String getCelPathDeath()
    {
        return "monsters\\snake\\snaked.cl2";
    }

    @Override
    protected int getNumFramesStand()
    {
        return 12;
    }

    @Override
    protected int getNumFramesWalk()
    {
        return 11;
    }

    @Override
    protected int getNumFramesAttack()
    {
        return 13;
    }

    @Override
    protected int getNumFramesHit()
    {
        return 6;
    }

    @Override
    protected int getNumFramesDeath()
    {
        return 18;
    }

    @Override
    protected int getFrameAttack()
    {
        return 8;
    }

    @Override
    protected int getMaxLookDist()
    {
        return 32 * 50;
    }
}
