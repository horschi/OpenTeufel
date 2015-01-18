package org.openteufel.game.entities.missiles;

import org.openteufel.game.entities.ProjectileEntity;
import org.openteufel.game.utils.Position2d;

public class BloodstarEntity extends ProjectileEntity
{
    public BloodstarEntity(Position2d pos, Position2d target, int team)
    {
        super(pos, target, 8, team);
    }

    @Override
    protected String getProjectileCelPath(int dir)
    {
        return "missiles\\Scubmisd.cl2";
    }
    
    @Override
    protected int getNumProjectileFrames()
    {
        return 16;
    }

    @Override
    protected int getNumProjectileDirections()
    {
        return 0;
    }
    

    @Override
    protected String getExplosionCelPath()
    {
        return "missiles\\Scbsexpd.cl2";
    }

    @Override
    protected int getNumExplosionFrames()
    {
        return 7;
    }
}
