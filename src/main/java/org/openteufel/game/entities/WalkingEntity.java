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
    private int                tmpX;
    private int                tmpY;
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
        this.tmpX = -1;
        this.tmpY = -1;
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
            this.targetPos = this.targetEnt.getPos();

        if (this.targetPos == null || this.pos.equals(this.targetPos))
        {
            this.targetPos = null;
            this.updateAnimation(ANIM_STANDING);
        }
        else
        {
            int posX = this.pos.getPosX();
            int posY = this.pos.getPosY();
            if (this.tmpX < 0 || this.tmpY < 0 || (this.tmpX == posX && this.tmpY == posY))
            {
                final int difX = this.targetPos.getPosX() - posX;
                final int difY = this.targetPos.getPosY() - posY;

                final double difLen = Math.sqrt(difX * difX + difY * difY);

                this.tmpX = ((int) (posX + ((difX * 32) / difLen))) & 0xffffffE0;
                this.tmpY = ((int) (posY + ((difY * 32) / difLen))) & 0xffffffE0;

                this.direction = EntityUtils.calcDirection8(this.tmpX - posX, this.tmpY - posY, this.direction);
                this.updateAnimation(ANIM_WALKING);
            }

            if (this.tmpX > posX)
            {
                posX += this.speed;
                if (posX > this.tmpX)
                    posX = this.tmpX;
            }
            else if (this.tmpX < posX)
            {
                posX -= this.speed;
                if (posX < this.tmpX)
                    posX = this.tmpX;
            }

            if (this.tmpY > posY)
            {
                posY += this.speed;
                if (posY > this.tmpY)
                    posY = this.tmpY;
            }
            else if (this.tmpY < posY)
            {
                posY -= this.speed;
                if (posY < this.tmpY)
                    posY = this.tmpY;
            }
            this.pos.setPos(posX, posY);
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

        if (this.targetPos != null)
        {
            final int cartX = this.targetPos.getPosX() - this.pos.getPosX();
            final int cartY = this.targetPos.getPosY() - this.pos.getPosY();
            renderer.drawMarker(screenX + cartesianToIsometricX(cartX, cartY), screenY + cartesianToIsometricY(cartX, cartY), "T");
        }

        if (true)
        {
            final int cartX = this.tmpX - this.pos.getPosX();
            final int cartY = this.tmpY - this.pos.getPosY();
            renderer.drawMarker(screenX + cartesianToIsometricX(cartX, cartY), screenY + cartesianToIsometricY(cartX, cartY), "M");
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
