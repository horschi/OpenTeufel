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
        imageLoader.preloadObjectCel("Towners\\healer\\healer.cel", 96, 96);
    }

    @Override
    protected int getNumFrames()
    {
        return 18;
    }

    @Override
    public void draw(final ImageLoader imageLoader, final Renderer renderer, final int screenX, final int screenY)
    {
        renderer.drawImage(imageLoader.loadObjectImage("Towners\\healer\\healer.cel", this.frameId), screenX - 48, screenY + 16 - 96);
        //        renderer.drawMarker(screenX, screenY, "" + this.frameId);
    }

}
