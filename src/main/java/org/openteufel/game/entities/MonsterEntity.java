package org.openteufel.game.entities;

import org.openteufel.game.utils.Position2d;

public abstract class MonsterEntity extends WalkingEntity
{
    public MonsterEntity(Position2d pos, int speed)
    {
        super(pos, speed);
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
        return new int[] { ANIM_STANDING, ANIM_WALKING };
    }
    
    @Override
    protected void preWalk(final int gametime, final int currentFrameId)
    {
        
    }

    @Override
    protected void finishWalk(final int gametime, final int currentFrameId)
    {
        
    }
}
