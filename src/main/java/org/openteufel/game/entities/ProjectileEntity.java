package org.openteufel.game.entities;

import java.io.IOException;

import org.openteufel.game.Entity;
import org.openteufel.game.WorldCallback;
import org.openteufel.game.utils.EntityUtils;
import org.openteufel.game.utils.Position2d;
import org.openteufel.ui.ImageLoader;

public abstract class ProjectileEntity extends AnimatedEntity
{
    private double  precisePosX, precisePosY;
    private double  moveX, moveY;
    private int     direction;
    private int     ttl         = 256;
    private boolean isExplosion = false;

    public ProjectileEntity(final Position2d pos, final Position2d target, final int speed, int team)
    {
        super(pos, team);
        this.updateTarget(target, speed);
    }

    protected void updateTarget(final Position2d target, final int speed)
    {
        if(isExplosion)
        {
            killEntity();
        }
        else
        {
            precisePosX = getPos().getPosX();
            precisePosY = getPos().getPosY();
            
            final int difX = this.pos.calcDiffX(target);
            final int difY = this.pos.calcDiffY(target);
            final double speedMult = ((double) speed) / Math.sqrt(difX * difX + difY * difY);
            this.moveX = (double) difX * speedMult;
            this.moveY = (double) difY * speedMult;
    
            switch (this.getNumProjectileDirections())
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
                this.updateAnimationParams(this.getProjectileCelPath(this.direction), 0, this.getNumProjectileFrames() - 1, false);
        }
    }

    @Override
    public final void preload(final ImageLoader imageLoader) throws IOException
    {
        final int numdirs = this.getNumProjectileDirections();
        for (int i = 0; i < numdirs; i++)
            imageLoader.preloadObjectCel(this.getProjectileCelPath(i));

        if(getExplosionCelPath() != null)
            imageLoader.preloadObjectCel(this.getExplosionCelPath());
    }

    protected abstract String getProjectileCelPath(int dir);

    protected abstract int getNumProjectileFrames();

    protected abstract int getNumProjectileDirections();

    protected abstract String getExplosionCelPath();
    
    protected abstract int getNumExplosionFrames();

    @Override
    protected final void preProcess(final int gametime, final int currentFrameId, WorldCallback world)
    {
        if(!isExplosion)
        {
            if(ttl < 0)
                killEntity();
            else
            {
                ttl--;
                this.precisePosX += this.moveX;
                this.precisePosY += this.moveY;
                this.pos.setPos((int) this.precisePosX, (int) this.precisePosY);
    
                Entity hitEnt = world.getEntityClosest(pos.getPosX(), pos.getPosY(), 20, getEnemyTeam());
                if(hitEnt != null)
                { // hit
                    performHit(gametime, world);
                }
            }
        }
    }

    private void performHit(final int gametime, WorldCallback world)
    {
        if(getExplosionCelPath() == null)
            killEntity();
        else
        {
            updateAnimationParams(getExplosionCelPath(), 0, getNumExplosionFrames(), true);
            isExplosion = true;
        }
    }

    @Override
    protected void finishAnimation(final int gametime, final int currentFrameId, WorldCallback world)
    {
        if(isExplosion)
            killEntity();
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
