package org.openteufel.game.entities.missiles;

import org.openteufel.game.entities.ProjectileEntity;
import org.openteufel.game.utils.Position2d;

public class ApocalypseExplosionEntity extends ProjectileEntity
{
    public ApocalypseExplosionEntity(Position2d pos, Position2d target, int team)
    {
        super(pos, target, 32, team);
    }

    @Override
    protected String getProjectileCelPath(int dir)
    {
        return null;
    }
    
    @Override
    protected int getNumProjectileFrames()
    {
        return 0;
    }

    @Override
    protected int getNumProjectileDirections()
    {
        return 0;
    }
    

    @Override
    protected String getExplosionCelPath()
    {
        return "missiles\\Fireplar.cl2";
    }

    @Override
    protected int getNumExplosionFrames()
    {
        return 17;
    }
}
