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
    protected String getCelPath(int dir)
    {
        return "missiles\\Scubmisd.cl2"; // missiles\Scbsexpd.cl2
    }

    @Override
    protected int getNumFrames()
    {
        return 16;
    }

    @Override
    protected int getNumDirections()
    {
        return 0;
    }
}
