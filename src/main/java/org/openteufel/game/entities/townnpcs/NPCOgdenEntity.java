package org.openteufel.game.entities.townnpcs;

import java.io.IOException;

import org.openteufel.game.entities.NPCEntity;
import org.openteufel.ui.ImageLoader;
import org.openteufel.ui.Renderer;

public class NPCOgdenEntity extends NPCEntity
{
    public NPCOgdenEntity(final int posX, final int posY)
    {
        super(posX, posY);
    }

    @Override
    public void preload(final ImageLoader imageLoader) throws IOException
    {
        imageLoader.preloadObjectCel("Towners\\twnf\\twnfn.cel");
    }

    @Override
    protected int getNumFrames()
    {
        return 15;
    }

    @Override
    public void draw(final ImageLoader imageLoader, final Renderer renderer, final int screenX, final int screenY, final double brightness)
    {
        renderer.drawImageCentered(imageLoader.loadObjectImage("Towners\\twnf\\twnfn.cel", this.frameId), screenX, screenY, 16, brightness);
    }

}
