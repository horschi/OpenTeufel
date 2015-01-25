package org.openteufel.game.entities;

import java.io.IOException;

import org.openteufel.game.Entity;
import org.openteufel.game.WorldCallback;
import org.openteufel.game.entities.missiles.BloodstarEntity;
import org.openteufel.game.utils.EntityUtils;
import org.openteufel.game.utils.PathFinder;
import org.openteufel.game.utils.Position2d;
import org.openteufel.ui.ImageLoader;
import org.openteufel.ui.Renderer;

public abstract class WalkingEntity extends AnimatedEntity
{
    protected static final int ANIM_STANDING  = 1;
    protected static final int ANIM_WALKING   = 2;
    protected static final int ANIM_ATTACKING = 3;
    protected static final int ANIM_HIT       = 4;
    protected static final int ANIM_DEATH     = 5;

    private Position2d         targetPos;
    private Entity             targetEnt;
    private PathFinder         pathFinder;
    private int                direction;
    private final int          speed;
    private int                currentAnimation;

    public WalkingEntity(final Position2d pos, final int speed, int team)
    {
        super(pos, true, team);
        this.targetPos = null;
        this.targetEnt = null;
        this.pathFinder = null;
        this.direction = 0;
        this.speed = speed;
    }

    public void updateTarget(final Position2d targetPos)
    {
        pathFinder = null;
        this.targetPos = targetPos;
        this.targetEnt = null;
        targetPos.setOffsetX(0);
        targetPos.setOffsetY(0);
    }

    public void updateTarget(final Entity targetEnt)
    {
        pathFinder = null;
        this.targetEnt = targetEnt;
        this.targetPos = this.targetEnt.getPos(); // TODO:
    }
    
    public void clearTarget()
    {
        pathFinder = null;
        this.targetEnt = null;
        this.targetPos = null;
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

    protected abstract int getFrameAttack();

    protected abstract int[] getAnimTypes();

    protected void updateDirection(final int direction)
    {
        if (this.direction != direction)
        {
            this.direction = direction;
            if (currentAnimation > 0)
            {
                final int num = this.getNumFrames(currentAnimation);
                final int frame = (num * this.direction);
                this.updateAnimationParams(this.getCelPath(currentAnimation), frame, frame + num, false);
            }
        }
    }

    protected void updateDirection(final Position2d target)
    {
        updateDirection(EntityUtils.calcDirection8(pos.calcDiffX(target), pos.calcDiffY(target), this.direction));
    }

    protected void updateAnimation(final int animType)
    {
        if (currentAnimation != animType)
        {
            this.currentAnimation = animType;
            final int num = this.getNumFrames(animType);
            final int frame = (num * this.direction);
            this.updateAnimationParams(this.getCelPath(animType), frame, frame + num, true);
        }
    }

    public int getCurrentAnimation()
    {
        return currentAnimation;
    }

    @Override
    protected final void preProcess(final int gametime, final int currentFrameId, WorldCallback world)
    {
        switch (currentAnimation)
        {
            case ANIM_ATTACKING:
                if ((currentFrameId % getNumFramesAttack()) == getFrameAttack())
                {
                    performAttack(gametime, world, targetEnt);
                }
                break;

            case ANIM_DEATH:
                break;

            case ANIM_HIT:
                break;

            case ANIM_STANDING:
            case ANIM_WALKING:
            default:
                preProcessWalk(gametime, currentFrameId, world);
                break;
        }
    }

    protected abstract void performAttack(final int gametime, WorldCallback world, Entity targetEntity);

    @Override
    protected final void finishAnimation(final int gametime, final int currentFrameId, WorldCallback world)
    {
        if (this.getCurrentAnimation() == ANIM_ATTACKING)
        {
            updateAnimation(ANIM_STANDING);
            finishWalk(gametime, currentFrameId, world);
        }
    }

    private void preProcessWalk(final int gametime, final int currentFrameId, WorldCallback world)
    {
        performWalk(gametime, currentFrameId, world);

        if (this.pos.decreaseOffset(this.speed))
        { // zero offset
            if (this.targetPos != null)
            {
                if (this.targetPos.getTileX() < 0)
                    this.targetPos.setTileX(0);
                if (this.targetPos.getTileY() < 0)
                    this.targetPos.setTileY(0);

                if(pathFinder == null)
                    pathFinder = new PathFinder(world, pos, targetPos);

                Position2d nextPos = pathFinder.getNextTile(pos);
                if (nextPos == null)
                {
                    finishWalk(gametime, currentFrameId, world);
                    pathFinder = new PathFinder(world, pos, targetPos);
                    nextPos = pathFinder.getNextTile(pos);
                    if (nextPos == null)
                    {
                        clearTarget();
                    }
                    else
                        this.pos.snapToNextTile(nextPos);
                }
                else
                    this.pos.snapToNextTile(nextPos);
            }
        }

        if (this.pos.hasOffset())
        {
            updateDirection(EntityUtils.calcDirection8(-this.pos.getOffsetX(), -this.pos.getOffsetY(), this.direction));
            this.updateAnimation(ANIM_WALKING);
        }
        else
        {
            finishWalk(gametime, currentFrameId, world);
            if (targetPos == null || targetPos.equals(pos))
            {
                pathFinder = null;
                if (currentAnimation == ANIM_WALKING)
                    this.updateAnimation(ANIM_STANDING);
            }
            else
                this.updateAnimation(ANIM_WALKING);
        }
    }

    protected abstract void performWalk(final int gametime, final int currentFrameId, WorldCallback world);

    protected abstract void finishWalk(final int gametime, final int currentFrameId, WorldCallback world);

    @Override
    protected int getBottomOffset()
    {
        return 16;
    }

    @Override
    protected int getFrameDelay()
    {
        return 1;
    }

    @Override
    public void draw(final ImageLoader imageLoader, final Renderer renderer, final int screenX, final int screenY, final int screenZ, final double brightness)
    {
        super.draw(imageLoader, renderer, screenX, screenY, screenZ, brightness);
        if (targetPos != null)
        {
            int cartX = targetPos.getPosX() - pos.getPosX();
            int cartY = targetPos.getPosY() - pos.getPosY();
            renderer.drawLine(screenX, screenY, screenX + cartesianToIsometricX(cartX, cartY), screenY + cartesianToIsometricY(cartX, cartY));
        }
    }

    private static int cartesianToIsometricX(final int cartX, final int cartY)
    {
        return cartX - cartY;
    }

    private static int cartesianToIsometricY(final int cartX, final int cartY)
    {
        return (cartX + cartY) / 2;
    }
}
