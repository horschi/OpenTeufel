package org.openteufel.game.entities.townnpcs;

import java.io.IOException;

import org.openteufel.game.entities.NPCEntity;
import org.openteufel.ui.ImageLoader;
import org.openteufel.ui.Renderer;

public class NPCGirlEntity extends NPCEntity
{
    public NPCGirlEntity(final int posX, final int posY)
    {
        super(posX, posY);
    }

    @Override
    public void preload(final ImageLoader imageLoader) throws IOException
    {
        imageLoader.preloadObjectCel("Towners\\girl\\Girls1.cel"); // with teddy
        imageLoader.preloadObjectCel("Towners\\girl\\Girlw1.cel"); // no teddy
    }

    @Override
    protected int getNumFrames()
    {
        return 20;
    }

    @Override
    protected int getWaitFrame()
    {
        return -1;
    }

    @Override
    protected int getFrameDelay()
    {
        return 5;
    }

    @Override
    public void draw(final ImageLoader imageLoader, final Renderer renderer, final int screenX, final int screenY, final double brightness)
    {
        renderer.drawImageCentered(imageLoader.loadObjectImage("Towners\\girl\\Girlw1.cel", this.frameId), screenX, screenY, 16, brightness);
        //        renderer.drawMarker(screenX, screenY, "" + this.frameId);
    }

}
