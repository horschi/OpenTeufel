package org.openteufel.game.entities.townnpcs;

import java.io.IOException;

import org.openteufel.game.entities.NPCEntity;
import org.openteufel.ui.ImageLoader;
import org.openteufel.ui.Renderer;

public class NPCBlacksmithEntity extends NPCEntity
{
    public NPCBlacksmithEntity(final int posX, final int posY)
    {
        super(posX, posY);
    }

    @Override
    public void preload(final ImageLoader imageLoader) throws IOException
    {
        imageLoader.preloadObjectCel("Towners\\smith\\smithn.cel", 96, 96);
    }

    @Override
    protected int getNumFrames()
    {
        return 15;
    }

    @Override
    public void draw(final ImageLoader imageLoader, final Renderer renderer, final int screenX, final int screenY)
    {
        renderer.drawImage(imageLoader.loadObjectImage("Towners\\smith\\smithn.cel", this.frameId), screenX - 48, screenY + 16 - 96);
        //        renderer.drawMarker(screenX, screenY, "" + this.frameId);
    }

}
