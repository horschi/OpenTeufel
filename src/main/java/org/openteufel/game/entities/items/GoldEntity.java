package org.openteufel.game.entities.items;

import java.io.IOException;

import org.openteufel.game.Entity;
import org.openteufel.game.utils.Position2d;
import org.openteufel.ui.ImageLoader;
import org.openteufel.ui.Renderer;

public class GoldEntity extends Entity
{
    private final int amount;
    private int       frameId = 0;

    public GoldEntity(final Position2d pos, final int amount)
    {
        super(pos);
        this.amount = amount;
    }

    @Override
    public void preload(final ImageLoader imageLoader) throws IOException
    {
        imageLoader.preloadObjectCel("items\\goldflip.cel");
    }

    @Override
    public void process(final int gametime)
    {
        if (this.frameId < 9)
            this.frameId++;
    }

    @Override
    public void draw(final ImageLoader imageLoader, final Renderer renderer, final int screenX, final int screenY, final double brightness)
    {
        renderer.drawImageCentered(imageLoader.loadObjectImage("items\\goldflip.cel", this.frameId), screenX, screenY, 0, 14, brightness);
    }

}
