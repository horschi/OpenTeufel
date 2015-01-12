package org.openteufel.game.entities.townnpcs;

import java.io.IOException;

import org.openteufel.game.entities.NPCEntity;
import org.openteufel.game.utils.Position2d;
import org.openteufel.ui.ImageLoader;
import org.openteufel.ui.Renderer;

public class NPCWitchEntity extends NPCEntity
{
    public NPCWitchEntity(final Position2d pos)
    {
        super(pos);
    }

    @Override
    public void preload(final ImageLoader imageLoader) throws IOException
    {
        imageLoader.preloadObjectCel("Towners\\townwmn1\\witch.cel");
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
        return 5;
    }

    @Override
    public void draw(final ImageLoader imageLoader, final Renderer renderer, final int screenX, final int screenY, final double brightness)
    {
        renderer.drawImageCentered(imageLoader.loadObjectImage("Towners\\townwmn1\\witch.cel", this.frameId), screenX, screenY, 16, 0, brightness);
        //        renderer.drawMarker(screenX, screenY, "" + this.frameId);
    }

}
