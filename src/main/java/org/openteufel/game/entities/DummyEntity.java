package org.openteufel.game.entities;

import java.io.IOException;

import org.openteufel.game.Entity;
import org.openteufel.game.WorldCallback;
import org.openteufel.game.utils.Position2d;
import org.openteufel.ui.ImageLoader;
import org.openteufel.ui.Renderer;

public class DummyEntity extends Entity
{
    private String text;

    public DummyEntity(final Position2d pos, final String text)
    {
        super(pos, false, TEAM_NEUTRAL);
        this.text = text;
        if (text == null)
            this.text = "" + this.pos.getTileX() + "/" + this.pos.getTileY();
    }

    @Override
    public void preload(final ImageLoader imageLoader) throws IOException
    {
    }

    @Override
    public void process(final int gametime, WorldCallback world)
    {
    }

    @Override
    public void draw(final ImageLoader imageLoader, final Renderer renderer, final int screenX, final int screenY, final int screenZ, final double brightness)
    {
        renderer.drawMarker(screenX, screenY, this.text);
    }

}
