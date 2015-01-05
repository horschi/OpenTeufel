package org.openteufel.game.entities;

import org.openteufel.game.Entity;

public abstract class NPCEntity extends Entity
{
    protected int frameId = 0;
    protected boolean frameForward = true;
    protected int frameWait = 0;

    public NPCEntity(final int posX, final int posY)
    {
        super(posX, posY);
    }

    @Override
    public final void process(final int gametime)
    {
        if ((gametime & 3) == 0)
        {
            if (this.frameId >= this.getNumFrames())
                this.frameId = 0;
            else
                this.frameId++;

            //            if (this.frameForward)
            //            {
            //                if (this.frameId >= this.getNumFrames() - 1)
            //                    this.frameForward = false;
            //                else
            //                    this.frameId++;
            //            }
            //            else
            //            {
            //                if (this.frameId <= 0)
            //                    this.frameForward = true;
            //                else
            //                    this.frameId--;
            //            }
        }
    }

    protected abstract int getNumFrames();

    //    protected abstract int getWaitFrame();
}
