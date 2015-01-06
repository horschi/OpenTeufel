package org.openteufel.game.entities.townnpcs;

import java.io.IOException;

import org.openteufel.game.entities.NPCEntity;
import org.openteufel.ui.ImageLoader;
import org.openteufel.ui.Renderer;

public class NPCFarmerEntity extends NPCEntity
{
    private final int type = 0;

    public NPCFarmerEntity(final int posX, final int posY)
    {
        super(posX, posY);
    }

    @Override
    public void preload(final ImageLoader imageLoader) throws IOException
    {
        imageLoader.preloadObjectCel("Towners\\Farmer\\Farmrn2.cel");
        imageLoader.preloadObjectCel("Towners\\Farmer\\cfrmrn2.cel");
        imageLoader.preloadObjectCel("Towners\\Farmer\\mfrmrn2.cel");
    }

    @Override
    protected int getNumFrames()
    {
        return 15;
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
        switch (this.type)
        {
            case 1: // cow
                renderer.drawImageCentered(imageLoader.loadObjectImage("Towners\\Farmer\\cfrmrn2.cel", this.frameId), screenX, screenY, 16, brightness);
                break;
            case 2: // elk
                renderer.drawImageCentered(imageLoader.loadObjectImage("Towners\\Farmer\\mfrmrn2.cel", this.frameId), screenX, screenY, 16, brightness);
                break;

            case 0: // farmer
            default:
                renderer.drawImageCentered(imageLoader.loadObjectImage("Towners\\Farmer\\Farmrn2.cel", this.frameId), screenX, screenY, 16, brightness);
        }
    }

}
