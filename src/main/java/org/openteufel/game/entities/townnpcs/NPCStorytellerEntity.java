package org.openteufel.game.entities.townnpcs;

import java.io.IOException;

import org.openteufel.game.entities.NPCEntity;
import org.openteufel.ui.ImageLoader;
import org.openteufel.ui.Renderer;

public class NPCStorytellerEntity extends NPCEntity
{
    public NPCStorytellerEntity(final int posX, final int posY)
    {
        super(posX, posY);
    }

    @Override
    public void preload(final ImageLoader imageLoader) throws IOException
    {
        imageLoader.preloadObjectCel("Towners\\strytell\\strytell.cel", 96, 96);
    }

    @Override
    protected int getNumFrames()
    {
        return 24;
    }

    @Override
    public void draw(final ImageLoader imageLoader, final Renderer renderer, final int screenX, final int screenY)
    {
        renderer.drawImage(imageLoader.loadObjectImage("Towners\\strytell\\strytell.cel", this.frameId), screenX - 48, screenY + 16 - 96);
        //        renderer.drawMarker(screenX, screenY, "" + this.frameId);
    }

}
