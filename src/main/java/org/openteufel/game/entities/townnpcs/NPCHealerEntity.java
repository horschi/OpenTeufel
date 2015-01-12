package org.openteufel.game.entities.townnpcs;

import java.io.IOException;

import org.openteufel.game.entities.NPCEntity;
import org.openteufel.ui.ImageLoader;
import org.openteufel.ui.Renderer;

public class NPCHealerEntity extends NPCEntity
{
    public NPCHealerEntity(final int posX, final int posY)
    {
        super(posX, posY);
    }

    @Override
    public void preload(final ImageLoader imageLoader) throws IOException
    {
        imageLoader.preloadObjectCel("Towners\\healer\\healer.cel");
    }

    @Override
    protected int getNumFrames()
    {
        return 19;
    }

    @Override
    protected int getWaitFrame()
    {
        return 0;
    }

    @Override
    protected int getFrameDelay()
    {
        return 3;
    }

    @Override
    public void draw(final ImageLoader imageLoader, final Renderer renderer, final int screenX, final int screenY, final double brightness)
    {
        renderer.drawImageCentered(imageLoader.loadObjectImage("Towners\\healer\\healer.cel", this.frameId), screenX, screenY, 16, 0, brightness);
        //        renderer.drawMarker(screenX, screenY, "" + this.frameId);
    }

}
