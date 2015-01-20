package org.openteufel.game.entities.missiles;

import org.openteufel.game.entities.ProjectileEntity;
import org.openteufel.game.utils.Position2d;

public class SuccubusBloodstarEntity extends ProjectileEntity
{
    public SuccubusBloodstarEntity(Position2d pos, Position2d target, int team)
    {
        super(pos, target, 8, team);
    }

    @Override
    protected String getProjectileCelPath(int dir)
    {
        return "monsters\\succ\\flare.cel";
    }
    
    @Override
    protected int getNumProjectileFrames()
    {
        return 16;
    }

    @Override
    protected int getNumProjectileDirections()
    {
        return 1;
    }
    

    @Override
    protected String getExplosionCelPath()
    {
        return "monsters\\succ\\flarexp.cel";
    }

    @Override
    protected int getNumExplosionFrames()
    {
        return 7;
    }
}
