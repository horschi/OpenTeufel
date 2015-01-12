package org.openteufel.game.entities;

import java.io.IOException;

import org.openteufel.game.Entity;
import org.openteufel.ui.ImageLoader;
import org.openteufel.ui.Renderer;

public abstract class AnimatedEntity extends Entity
{
    private int minFrameId, maxFrameId, currentFrameId;
    private String currentCelPath;

    public AnimatedEntity(final int posX, final int posY)
    {
        super(posX, posY);
    }

    protected void updateAnimationParams(final String currentCelPath, final int minFrameId, final int maxFrameId)
    {
        if(currentCelPath != null)
            this.currentCelPath = currentCelPath;
        this.minFrameId = minFrameId;
        this.maxFrameId = maxFrameId;
    }

    @Override
    public final void process(final int gametime)
    {
        this.process(gametime, this.currentFrameId);
        this.currentFrameId++;
        if (this.currentFrameId > this.maxFrameId)
            this.currentFrameId = this.minFrameId;
    }

    public abstract void process(final int gametime, int currentFrameId);

    protected abstract int getBottomOffset();

    @Override
    public final void draw(final ImageLoader imageLoader, final Renderer renderer, final int screenX, final int screenY, final double brightness)
    {
        if (this.currentCelPath == null)
            throw new NullPointerException();
        renderer.drawImageCentered(imageLoader.loadObjectImage(this.currentCelPath, this.currentFrameId), screenX, screenY, 0, this.getBottomOffset(), brightness);
    }
}
