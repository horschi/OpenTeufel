package org.openteufel.game.entities.monsters;

import org.openteufel.game.entities.MonsterEntity;
import org.openteufel.game.entities.WalkingEntity;
import org.openteufel.game.utils.Position2d;

public class SuccubusEntitiy extends MonsterEntity
{
    public SuccubusEntitiy(Position2d pos)
    {
        super(pos, 5);
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
        return 0;
    }

    @Override
    protected int getNumFramesHit()
    {
        return 0;
    }

    @Override
    protected int getNumFramesDeath()
    {
        return 0;
    }
}
