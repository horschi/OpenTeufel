package org.openteufel.game.entities.townnpcs;

import java.io.IOException;

import org.openteufel.game.entities.NPCEntity;
import org.openteufel.game.utils.Position2d;
import org.openteufel.ui.ImageLoader;
import org.openteufel.ui.Renderer;

public class NPCOgdenEntity extends NPCEntity
{
    public NPCOgdenEntity(final Position2d pos)
    {
        super(pos);
    }

    @Override
    public void preload(final ImageLoader imageLoader) throws IOException
    {
        imageLoader.preloadObjectCel("Towners\\twnf\\twnfn.cel");
    }

    @Override
    protected int getNumFrames()
    {
        return 16;
    }

    @Override
    protected int getWaitFrame()
    {
        return 0;
    }

    @Override
    protected int getFrameDelay()
    {
        return 6;
    }

    @Override
    public void draw(final ImageLoader imageLoader, final Renderer renderer, final int screenX, final int screenY, final int screenZ, final double brightness)
    {
        renderer.drawImageCentered(imageLoader.loadObjectImage("Towners\\twnf\\twnfn.cel", this.frameId), screenX, screenY, 0, 16, brightness);
    }

}
