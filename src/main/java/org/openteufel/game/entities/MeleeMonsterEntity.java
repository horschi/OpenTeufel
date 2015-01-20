package org.openteufel.game.entities;

import org.openteufel.game.Entity;
import org.openteufel.game.WorldCallback;
import org.openteufel.game.utils.Position2d;

public abstract class MeleeMonsterEntity extends MonsterEntity
{
    public MeleeMonsterEntity(Position2d pos, int speed)
    {
        super(pos, speed);
    }

    @Override
    protected Entity[] getAdditionalPreloadEntitites()
    {
        return new Entity[] {};
    }
    
    @Override
    protected void performMonsterAttack(final int gametime, WorldCallback world, Entity targetEntity)
    {
    }
    
    @Override
    protected int getMaxAttackDist()
    {
        return 50;
    }

    @Override
    protected int getMinAttackDist()
    {
        return 32 * 0;
    }
}
