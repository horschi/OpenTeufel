package org.openteufel.game.entities;

import org.openteufel.game.Entity;
import org.openteufel.game.WorldCallback;
import org.openteufel.game.utils.Position2d;
import org.openteufel.ui.ImageLoader;
import org.openteufel.ui.Renderer;

public abstract class AnimatedEntity extends Entity
{
    private int    minFrameId, maxFrameId, currentFrameId;
    private String currentCelPath;

    public AnimatedEntity(final Position2d pos, int team)
    {
        super(pos, team);
    }

    protected void updateAnimationParams(final String currentCelPath, final int minFrameId, final int maxFrameId)
    {
        if (currentCelPath != null)
            this.currentCelPath = currentCelPath;
        this.minFrameId = minFrameId;
        this.maxFrameId = maxFrameId;

        if (this.currentFrameId > this.maxFrameId || this.currentFrameId < minFrameId)
        {
            this.currentFrameId = this.minFrameId;
        }
    }

    @Override
    public final void process(final int gametime, WorldCallback world)
    {
        if ((gametime % this.getFrameDelay()) == 0)
            this.currentFrameId++;
        this.preProcess(gametime, this.currentFrameId, world);
        if (this.currentFrameId > this.maxFrameId)
        {
            this.currentFrameId = this.minFrameId;
            this.finishAnimation(gametime, this.currentFrameId, world);
        }
    }

    protected abstract void preProcess(final int gametime, int currentFrameId, WorldCallback world);

    protected abstract void finishAnimation(final int gametime, int currentFrameId, WorldCallback world);

    protected abstract int getBottomOffset();

    protected abstract int getFrameDelay();

    @Override
    public void draw(final ImageLoader imageLoader, final Renderer renderer, final int screenX, final int screenY, final int screenZ, final double brightness)
    {
        if (this.currentCelPath == null)
            throw new NullPointerException();
        renderer.drawImageCentered(imageLoader.loadObjectImage(this.currentCelPath, this.currentFrameId), screenX, screenY, 0, this.getBottomOffset(), brightness);
    }
}
