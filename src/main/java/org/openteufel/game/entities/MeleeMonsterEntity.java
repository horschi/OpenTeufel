package org.openteufel.game.entities;

import org.openteufel.game.utils.Position2d;

public abstract class MeleeMonsterEntity extends MonsterEntity
{
    public MeleeMonsterEntity(Position2d pos, int speed)
    {
        super(pos, speed);
    }

}
