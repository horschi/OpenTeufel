package org.openteufel.game.entities.townnpcs;

import java.io.IOException;

import org.openteufel.game.entities.NPCEntity;
import org.openteufel.ui.ImageLoader;
import org.openteufel.ui.Renderer;

public class NPCDrunkEntity extends NPCEntity
{
    public NPCDrunkEntity(final int posX, final int posY)
    {
        super(posX, posY);
    }

    @Override
    public void preload(final ImageLoader imageLoader) throws IOException
    {
        imageLoader.preloadObjectCel("Towners\\drunk\\twndrunk.cel");
    }

    @Override
    protected int getNumFrames()
    {
        return 18;
    }

    @Override
    protected int getWaitFrame()
    {
        return -1;
    }

    @Override
    protected int getFrameDelay()
    {
        return 3;
    }

    @Override
    public void draw(final ImageLoader imageLoader, final Renderer renderer, final int screenX, final int screenY, final double brightness)
    {
        renderer.drawImageCentered(imageLoader.loadObjectImage("Towners\\drunk\\twndrunk.cel", this.frameId), screenX, screenY, 16, 0, brightness);
        //        renderer.drawMarker(screenX, screenY, "" + this.frameId);
    }

}
