package org.openteufel.game.entities;

public abstract class WalkingEntity extends AnimatedEntity
{
    private final int targetX, targetY;
    private final int direction;
    private final int speed;

    public WalkingEntity(final int posX, final int posY, final int speed)
    {
        super(posX, posY);
        this.targetX = posX;
        this.targetY = posY;
        this.direction = 0;
        this.speed = speed;
    }

    @Override
    protected final String[] getCelPaths()
    {
        final String[] ret = new String[8];
        for (int i = 0; i < 8; i++)
            ret[i] = this.getCelPath(i);
        return ret;
    }

    protected abstract String getCelPath(int dir);

    protected abstract int getNumFrames();

    @Override
    public void process(final int gametime, final int currentFrameId)
    {
        this.updateAnimationParams(this.getCelPath(this.direction), 0, this.getNumFrames() - 1);

    }

    @Override
    protected int getBottomOffset()
    {
        return 16;
    }
}
