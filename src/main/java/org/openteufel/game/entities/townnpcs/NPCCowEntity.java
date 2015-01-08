package org.openteufel.game.entities.townnpcs;

import java.io.IOException;

import org.openteufel.game.entities.NPCEntity;
import org.openteufel.ui.ImageLoader;
import org.openteufel.ui.Renderer;

public class NPCCowEntity extends NPCEntity
{
    private final int dir;

    public NPCCowEntity(final int posX, final int posY, final int dir)
    {
        super(posX, posY);
        this.dir = dir % 8;
    }

    @Override
    public void preload(final ImageLoader imageLoader) throws IOException
    {
        imageLoader.preloadObjectCel("Towners\\animals\\cow.cel");
    }

    @Override
    protected int getNumFrames()
    {
        return 12;
    }

    @Override
    protected int getWaitFrame()
    {
        return ((int) (Math.random() * 10.0));
    }

    @Override
    protected int getFrameDelay()
    {
        return 8;
    }

    @Override
    public void draw(final ImageLoader imageLoader, final Renderer renderer, final int screenX, final int screenY, final double brightness)
    {
        renderer.drawImageCentered(imageLoader.loadObjectImage("Towners\\animals\\cow.cel", this.frameId + (this.dir * this.getNumFrames())), screenX, screenY, 16, brightness);
        //        renderer.drawMarker(screenX, screenY, "" + this.frameId);
    }

}
