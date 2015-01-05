package org.openteufel.game.entities;

import java.io.IOException;

import org.openteufel.game.Entity;
import org.openteufel.ui.ImageLoader;
import org.openteufel.ui.Renderer;

public class DummyEntity extends Entity
{
    private String text;

    public DummyEntity(final int posX, final int posY, final String text)
    {
        super(posX, posY);
        this.text = text;
        this.text = "" + this.posX / 64 + "/" + this.posY / 64;
    }

    @Override
    public void preload(final ImageLoader imageLoader) throws IOException
    {
    }

    @Override
    public void process(final int gametime)
    {
    }

    @Override
    public void draw(final ImageLoader imageLoader, final Renderer renderer, final int screenX, final int screenY)
    {
        renderer.drawMarker(screenX, screenY, this.text);
    }

}
