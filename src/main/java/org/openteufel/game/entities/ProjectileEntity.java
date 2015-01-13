package org.openteufel.game.entities;

import java.io.IOException;

import org.openteufel.game.utils.EntityUtils;
import org.openteufel.game.utils.Position2d;
import org.openteufel.ui.ImageLoader;

public abstract class ProjectileEntity extends AnimatedEntity
{
    private double precisePosX, precisePosY;
    private double moveX, moveY;
    private int    direction;

    public ProjectileEntity(final Position2d pos, final Position2d target, final int speed, int team)
    {
        super(pos, team);
        this.updateTarget(target, speed);
    }

    protected void updateTarget(final Position2d target, final int speed)
    {
        final int difX = this.pos.calcDiffX(target);
        final int difY = this.pos.calcDiffY(target);
        final double speedMult = ((double) speed) / (double) (difX * difX + difY * difY);
        this.moveX = (double) difX * speedMult;
        this.moveY = (double) difY * speedMult;

        switch (this.getNumDirections())
        {
            case 0:
            case 1:
                this.direction = 0;
                break;
            case 8:
                this.direction = EntityUtils.calcDirection8(difX, difY, -1);
                break;
            case 16:
                this.direction = EntityUtils.calcDirection16(difX, difY, -1);
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
    protected final void preProcess(final int gametime, final int currentFrameId)
    {
        this.precisePosX += this.moveX;
        this.precisePosY += this.moveY;
        this.pos.setPos((int) this.precisePosX, (int) this.precisePosY);
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
        return 1;
    }

}
