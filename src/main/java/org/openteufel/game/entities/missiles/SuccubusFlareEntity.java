package org.openteufel.game.entities.missiles;

import org.openteufel.game.entities.ProjectileEntity;
import org.openteufel.game.utils.Position2d;

public class SuccubusFlareEntity extends ProjectileEntity
{
    public SuccubusFlareEntity(Position2d pos, Position2d target)
    {
        super(pos, target, 8);
    }

    @Override
    protected String getCelPath(int dir)
    {
        return "monsters\\succ\\flare.cel";
        //        flarexp.cel
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
