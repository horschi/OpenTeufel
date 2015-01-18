package org.openteufel.game.entities;

import org.openteufel.game.utils.Position2d;

public abstract class RangedMonsterEntity extends MonsterEntity
{
    public RangedMonsterEntity(Position2d pos, int speed)
    {
        super(pos, speed);
    }
}
