package org.openteufel.game.entities;

import org.openteufel.game.Entity;
import org.openteufel.game.WorldCallback;
import org.openteufel.game.entities.missiles.BloodstarEntity;
import org.openteufel.game.utils.Position2d;

public abstract class MonsterEntity extends WalkingEntity
{
    private Entity attackTarget = null;

    public MonsterEntity(Position2d pos, int speed)
    {
        super(pos, speed, TEAM_BAD);
    }

    @Override
    protected int[] getAnimTypes()
    {
        return new int[] { ANIM_STANDING, ANIM_WALKING, ANIM_ATTACKING, ANIM_HIT, ANIM_DEATH };
    }

    @Override
    protected void performWalk(final int gametime, final int currentFrameId, WorldCallback world)
    {

    }

    protected abstract int getMaxLookDist();

    protected abstract int getMaxAttackDist();

    protected abstract int getMinAttackDist();

    @Override
    protected final void performAttack(final int gametime, WorldCallback world, Entity targetEntity)
    {
        if (attackTarget != null)
            targetEntity = attackTarget;
        performMonsterAttack(gametime, world, targetEntity);
    }

    protected abstract void performMonsterAttack(final int gametime, WorldCallback world, Entity targetEntity);

    @Override
    protected void finishWalk(final int gametime, final int currentFrameId, WorldCallback world)
    {
        if (attackTarget == null)
        {
            attackTarget = world.getEntityClosest(getPos().getPosX(), getPos().getPosY(), getMaxLookDist(), getEnemyTeam());
        }

        if (attackTarget == null)
            updateAnimation(ANIM_STANDING);
        else
        {
            int dist = getPos().calcDist(attackTarget.getPos());
            if (dist < getMinAttackDist())
            { // walk away
                updateTarget(getPos().calcMidPointAbsolute(attackTarget.getPos(), -getMinAttackDist() / 2));
            }
            else if (dist > getMaxAttackDist())
            { // walk to enemy
                updateTarget(getPos().calcMidPointRelative(attackTarget.getPos(), 0.4));
            }
            else
            { // attack
                updateDirection(attackTarget.getPos());
                updateAnimation(ANIM_ATTACKING);
            }
        }
    }
}
