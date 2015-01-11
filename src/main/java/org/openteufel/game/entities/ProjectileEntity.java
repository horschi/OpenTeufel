package org.openteufel.game.entities;

import java.io.IOException;

import org.openteufel.game.utils.EntityUtils;
import org.openteufel.ui.ImageLoader;

public abstract class ProjectileEntity extends AnimatedEntity
{
    private double precisePosX, precisePosY;
    private double moveX, moveY;
    private int    direction;

    public ProjectileEntity(final int posX, final int posY, final int targetX, final int targetY, final int speed)
    {
        super(posX, posY);
        this.updateTarget(targetX, targetY, speed);
    }

    protected void updateTarget(final int targetX, final int targetY, final int speed)
    {
        final double difX = targetX - this.posX;
        final double difY = targetY - this.posY;
        final double speedMult = ((double) speed) / (difX * difX + difY * difY);
        this.moveX = difX * speedMult;
        this.moveY = difY * speedMult;

        switch (this.getNumDirections())
        {
            case 0:
            case 1:
                this.direction = 0;
                break;
            case 8:
                this.direction = EntityUtils.calcDirection8(this.posX, this.posY, targetX, targetY, -1);
                break;
            case 16:
                this.direction = EntityUtils.calcDirection16(this.posX, this.posY, targetX, targetY, -1);
                break;

            default:
                throw new IllegalStateException();
        }
        if (this.direction >= 0)
            this.updateAnimationParams(this.getCelPath(this.direction), 0, this.getNumFrames() - 1);
    }

    @Override
    public final void preload(final ImageLoader imageLoader) throws IOException
    {
        final int numdirs = this.getNumDirections();
        for (int i = 0; i < numdirs; i++)
            imageLoader.preloadObjectCel(this.getCelPath(i));
    }

    protected abstract String getCelPath(int dir);

    protected abstract int getNumFrames();

    protected abstract int getNumDirections();

    @Override
    public final void process(final int gametime, final int currentFrameId)
    {
        this.precisePosX += this.moveX;
        this.precisePosY += this.moveY;
        this.posX = (int) (this.precisePosX);
        this.posY = (int) (this.precisePosY);
    }

    @Override
    protected int getBottomOffset()
    {
        return 16;
    }
}
