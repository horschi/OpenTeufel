package org.openteufel.game.entities;

import java.io.IOException;

import org.openteufel.game.Entity;
import org.openteufel.game.utils.EntityUtils;
import org.openteufel.game.utils.Position2d;
import org.openteufel.ui.ImageLoader;
import org.openteufel.ui.Renderer;

public abstract class WalkingEntity extends AnimatedEntity
{
    protected static final int ANIM_STANDING = 0;
    protected static final int ANIM_WALKING  = 1;

    private Position2d         targetPos;
    private Entity             targetEnt;
    private int                direction;
    private final int          speed;
    private int                currentAnimation;

    public WalkingEntity(final Position2d pos, final int speed)
    {
        super(pos);
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
    }

    protected abstract String getCelPath(int animType);

    protected abstract int getNumFrames(int animType);

    protected abstract int[] getAnimTypes();

    protected void updateAnimation(final int animType)
    {
        this.currentAnimation = animType;
        final int num = this.getNumFrames(animType);
        final int frame = (num * this.direction);
        this.updateAnimationParams(this.getCelPath(animType), frame, frame + num - 1);
    }

    @Override
    protected void preProcess(final int gametime, final int currentFrameId)
    {
        if (this.targetPos == null && this.targetEnt != null)
            this.targetPos = this.targetEnt.getPos(); // TODO:

        if(this.pos.decreaseOffset(this.speed))
        { // zero offset
            if (this.targetPos != null)
            {
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
        }
    }

    @Override
    protected void finishAnimation(final int gametime, final int currentFrameId)
    {
    }

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
    public void draw(final ImageLoader imageLoader, final Renderer renderer, final int screenX, final int screenY, final double brightness)
    {
        super.draw(imageLoader, renderer, screenX, screenY, brightness);
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
