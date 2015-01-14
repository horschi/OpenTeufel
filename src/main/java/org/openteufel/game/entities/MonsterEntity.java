package org.openteufel.game.entities;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
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
    protected String getCelPath(int animType)
    {
        switch (animType)
        {
            case ANIM_STANDING:
                return getCelPathStand();

            case ANIM_WALKING:
                return getCelPathWalk();

            case ANIM_ATTACKING:
                return getCelPathAttack();
                
            case ANIM_HIT:
                return getCelPathHit();
                
            case ANIM_DEATH:
                return getCelPathDeath();
            default:
                throw new IllegalArgumentException();
        }
    }
    
    protected abstract String getCelPathStand();

    protected abstract String getCelPathWalk();

    protected abstract String getCelPathAttack();

    protected abstract String getCelPathHit();

    protected abstract String getCelPathDeath();

    @Override
    protected int getNumFrames(int animType)
    {
        switch (animType)
        {
            case ANIM_STANDING:
                return getNumFramesStand();

            case ANIM_WALKING:
                return getNumFramesWalk();

            case ANIM_ATTACKING:
                return getNumFramesAttack();

            case ANIM_HIT:
                return getNumFramesHit();
                
            case ANIM_DEATH:
                return getNumFramesDeath();
                
            default:
                throw new IllegalArgumentException();
        }
    }

    protected abstract int getNumFramesStand();

    protected abstract int getNumFramesWalk();

    protected abstract int getNumFramesAttack();

    protected abstract int getNumFramesHit();

    protected abstract int getNumFramesDeath();

    @Override
    protected int[] getAnimTypes()
    {
        return new int[] { ANIM_STANDING, ANIM_WALKING, ANIM_ATTACKING, ANIM_HIT, ANIM_DEATH };
    }

    @Override
    protected void preWalk(final int gametime, final int currentFrameId, WorldCallback world)
    {

    }

    protected int getMaxLookDist()
    {
        return 32 * 50;
    }
    
    protected int getMaxAttackDist()
    {
        return 32 * 15;
    }

    protected int getMinAttackDist()
    {
        return 32 * 6;
    }

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
                updateTarget(getPos().calcMidPoint(attackTarget.getPos(), -0.3));
            }
            else if (dist > getMaxAttackDist())
            { // walk to enemy
                updateTarget(getPos().calcMidPoint(attackTarget.getPos(), 0.3));
            }
            else
            { // attack
                updateDirection(attackTarget.getPos());
                updateAnimation(ANIM_ATTACKING);
            }
        }
    }

    @Override
    protected Entity[] getAdditionalPreloadEntitites()
    {
        return new Entity[] { new BloodstarEntity(getPos().clone(), getPos().clone(), getTeam()) };
    }
    
    @Override
    protected void finishAnimation(final int gametime, final int currentFrameId, WorldCallback world)
    {
        if(this.getCurrentAnimation() == ANIM_ATTACKING)
        {
            world.addEntity(new BloodstarEntity(getPos().clone(), attackTarget.getPos(), getTeam()));
            updateAnimation(ANIM_STANDING);
        }
    }
}
