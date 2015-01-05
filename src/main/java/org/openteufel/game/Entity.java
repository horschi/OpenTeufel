package org.openteufel.game;

import java.io.IOException;

import org.openteufel.ui.ImageLoader;
import org.openteufel.ui.Renderer;

public abstract class Entity
{
    protected int posX, posY;

    public Entity(final int posX, final int posY)
    {
        this.posX = posX;
        this.posY = posY;
    }

    public int getPosX()
    {
        return this.posX;
    }

    public int getPosY()
    {
        return this.posY;
    }

    public abstract void preload(ImageLoader imageLoader) throws IOException;

    public abstract void process(int gametime);

    public abstract void draw(final ImageLoader imageLoader, Renderer renderer, int screenX, int screenY, double brightness);
}
