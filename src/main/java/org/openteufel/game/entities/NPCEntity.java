package org.openteufel.game.entities;

import org.openteufel.game.Entity;
import org.openteufel.game.WorldCallback;
import org.openteufel.game.utils.Position2d;

public abstract class NPCEntity extends Entity
{
    protected int     frameId      = 0;
    protected boolean frameForward = true;
    protected int     frameWait    = 0;

    public NPCEntity(final Position2d pos)
    {
        super(pos, true, TEAM_NEUTRAL);
    }

    @Override
    public final void process(final int gametime, WorldCallback world)
    {
        if ((gametime % this.getFrameDelay()) == 0)
        {
            if (this.frameWait > 0)
                this.frameWait--;
            else
            {
                this.frameId++;
                if (this.frameId >= this.getNumFrames())
                {
                    this.frameId = 0;
                }
                if (this.frameId == this.getWaitFrame())
                {
                    this.frameWait = this.getNumFrames();
                }
            }
        }
    }

    protected abstract int getNumFrames();

    protected abstract int getWaitFrame();

    protected abstract int getFrameDelay();

}
