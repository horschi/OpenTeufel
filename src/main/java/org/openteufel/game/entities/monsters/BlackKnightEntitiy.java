package org.openteufel.game.entities.monsters;

import org.openteufel.game.Entity;
import org.openteufel.game.entities.MeleeMonsterEntity;
import org.openteufel.game.entities.MonsterEntity;
import org.openteufel.game.entities.missiles.BloodstarEntity;
import org.openteufel.game.utils.Position2d;

public class BlackKnightEntitiy extends MeleeMonsterEntity
{
    public BlackKnightEntitiy(Position2d pos)
    {
        super(pos, 5);
    }

    @Override
    protected String getCelPathStand()
    {
        return "monsters\\black\\blackn.cl2";
    }

    @Override
    protected String getCelPathWalk()
    {
        return "monsters\\black\\blackw.cl2";
    }

    @Override
    protected String getCelPathAttack()
    {
        return "monsters\\black\\blacka.cl2";
    }

    @Override
    protected String getCelPathHit()
    {
        return "monsters\\black\\blackh.cl2";
    }

    @Override
    protected String getCelPathDeath()
    {
        return "monsters\\black\\blackd.cl2";
    }

    @Override
    protected int getNumFramesStand()
    {
        return 8;
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
}
