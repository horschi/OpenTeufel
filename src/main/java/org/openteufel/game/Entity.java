package org.openteufel.game;

import java.io.IOException;

import org.openteufel.game.utils.Position2d;
import org.openteufel.ui.ImageLoader;
import org.openteufel.ui.Renderer;

public abstract class Entity
{
    protected Position2d pos;

    public Entity(final Position2d pos)
    {
        this.pos = pos;
    }

    public Position2d getPos()
    {
        return this.pos;
    }

    public abstract void preload(ImageLoader imageLoader) throws IOException;

    public abstract void process(int gametime);

    public abstract void draw(final ImageLoader imageLoader, Renderer renderer, int screenX, int screenY, double brightness);
}
