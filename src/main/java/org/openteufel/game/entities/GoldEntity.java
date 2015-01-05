package org.openteufel.game.entities;

import java.io.IOException;

import org.openteufel.game.Entity;
import org.openteufel.ui.ImageLoader;
import org.openteufel.ui.Renderer;

public class GoldEntity extends Entity
{
    private final int amount;
    private int       frameId = 0;

    public GoldEntity(final int posX, final int posY, final int amount)
    {
        super(posX, posY);
        this.amount = amount;
    }

    @Override
    public void preload(final ImageLoader imageLoader) throws IOException
    {
        imageLoader.preloadObjectCel("items\\goldflip.cel", 96, 160);
    }

    @Override
    public void process(final int gametime)
    {
        if (this.frameId < 9)
            this.frameId++;
    }

    @Override
    public void draw(final ImageLoader imageLoader, final Renderer renderer, final int screenX, final int screenY)
    {
        renderer.drawImage(imageLoader.loadObjectImage("items\\goldflip.cel", this.frameId), screenX - 48, screenY + 14 - 160);
    }

}
