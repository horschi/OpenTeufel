package org.openteufel.game.entities;

import java.io.IOException;

import org.openteufel.game.Entity;
import org.openteufel.game.WorldCallback;
import org.openteufel.game.utils.EntityUtils;
import org.openteufel.game.utils.Position2d;
import org.openteufel.ui.ImageLoader;
import org.openteufel.ui.Renderer;

public abstract class WalkingEntity extends AnimatedEntity
{
    protected static final int ANIM_STANDING = 0;
    protected static final int ANIM_WALKING  = 1;
    protected static final int ANIM_ATTACKING  = 2;
    protected static final int ANIM_HIT  = 3;
    protected static final int ANIM_DEATH  = 4;
    
    private Position2d         targetPos;
    private Entity             targetEnt;
    private int                direction;
    private final int          speed;
    private int                currentAnimation;

    public WalkingEntity(final Position2d pos, final int speed, int team)
    {
        super(pos, team);
        this.targetPos = null;
        this.targetEnt = null;
        this.direction = 0;
        this.speed = speed;
        this.currentAnimation = ANIM_STANDING;
    }

    public void updateTarget(final Position2d targetPos)
    {
        this.targetPos = targetPos;
        this.targetEnt = null;
        targetPos.setOffsetX(0);
        targetPos.setOffsetY(0);
    }

    public void updateTarget(final Entity targetEnt)
    {
        this.targetPos = null;
        this.targetEnt = targetEnt;
    }

    @Override
    public final void preload(final ImageLoader imageLoader) throws IOException
    {
        for (final int animType : this.getAnimTypes())
            imageLoader.preloadObjectCel(this.getCelPath(animType));
        
        Entity[] addPreloadEntitties = getAdditionalPreloadEntitites();
        if (addPreloadEntitties != null)
            for (final Entity ent : addPreloadEntitties)
                ent.preload(imageLoader);
    }

    protected abstract Entity[] getAdditionalPreloadEntitites();

    protected abstract String getCelPath(int animType);

    protected abstract int getNumFrames(int animType);

    protected abstract int[] getAnimTypes();

    protected void updateDirection(final int direction)
    {
        this.direction = direction;
    }
    
    protected void updateDirection(final Position2d target)
    {
        this.direction = EntityUtils.calcDirection8(pos.calcDiffX(target), pos.calcDiffY(target), this.direction);
    }
    
    protected void updateAnimation(final int animType)
    {
        this.currentAnimation = animType;
        final int num = this.getNumFrames(animType);
        final int frame = (num * this.direction);
        this.updateAnimationParams(this.getCelPath(animType), frame, frame + num - 1);
    }

    public int getCurrentAnimation()
    {
        return currentAnimation;
    }

    @Override
    protected void preProcess(final int gametime, final int currentFrameId, WorldCallback world)
    {
        switch (currentAnimation)
        {
            case ANIM_STANDING:
            case ANIM_WALKING:
                preProcessWalk(gametime, currentFrameId, world);
                break;
                
            case ANIM_ATTACKING:
                break;

            case ANIM_DEATH:
                break;

            case ANIM_HIT:
                break;

            default:
                throw new RuntimeException();
        }
    }
    
    private void preProcessWalk(final int gametime, final int currentFrameId, WorldCallback world)
    {
        preWalk(gametime, currentFrameId, world);
        
        if (this.targetPos == null && this.targetEnt != null)
            this.targetPos = this.targetEnt.getPos(); // TODO:

        if(this.pos.decreaseOffset(this.speed))
        { // zero offset
            if (this.targetPos != null)
            {
                if (this.targetPos.getTileX() < 0)
                    this.targetPos.setTileX(0);
                if (this.targetPos.getTileY() < 0)
                    this.targetPos.setTileY(0);

                final int difX = this.targetPos.getTileX() - this.pos.getTileX();
                final int difY = this.targetPos.getTileY() - this.pos.getTileY();

                if (difX > 0)
                {
                    this.pos.setTileX(this.pos.getTileX() + 1);
                    this.pos.setOffsetX(-32);
                }
                else if (difX < 0)
                {
                    this.pos.setTileX(this.pos.getTileX() - 1);
                    this.pos.setOffsetX(32);
                }

                if (difY > 0)
                {
                    this.pos.setTileY(this.pos.getTileY() + 1);
                    this.pos.setOffsetY(-32);
                }
                else if (difY < 0)
                {
                    this.pos.setTileY(this.pos.getTileY() - 1);
                    this.pos.setOffsetY(32);
                }
            }
        }

        if(this.pos.hasOffset())
        {
            this.direction = EntityUtils.calcDirection8(-this.pos.getOffsetX(), -this.pos.getOffsetY(), this.direction);
            this.updateAnimation(ANIM_WALKING);
        }
        else
        {
            this.updateAnimation(ANIM_STANDING);
            finishWalk(gametime, currentFrameId, world);
        }
    }

    protected abstract void preWalk(final int gametime, final int currentFrameId, WorldCallback world);
    protected abstract void finishWalk(final int gametime, final int currentFrameId, WorldCallback world);


    @Override
    protected int getBottomOffset()
    {
        return 16;
    }

    @Override
    protected int getFrameDelay()
    {
        return 2;
    }

    @Override
    public void draw(final ImageLoader imageLoader, final Renderer renderer, final int screenX, final int screenY, final int screenZ, final double brightness)
    {
        super.draw(imageLoader, renderer, screenX, screenY, screenZ, brightness);
    }
}
