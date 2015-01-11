package org.openteufel.game.entities;

import java.io.IOException;

import org.openteufel.ui.ImageLoader;

public abstract class WalkingEntity extends AnimatedEntity
{
    protected static final int ANIM_STANDING = 0;
    protected static final int ANIM_WALKING  = 1;

    private final int          targetX, targetY;
    private final int          direction;
    private final int          speed;
    private int                currentAnimation;

    public WalkingEntity(final int posX, final int posY, final int speed)
    {
        super(posX, posY);
        this.targetX = posX;
        this.targetY = posY;
        this.direction = 0;
        this.speed = speed;
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
    public void process(final int gametime, final int currentFrameId)
    {

    }

    @Override
    protected int getBottomOffset()
    {
        return 16;
    }
}
