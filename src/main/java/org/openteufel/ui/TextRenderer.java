package org.openteufel.ui;

public class TextRenderer
{
    private final Renderer<Object> renderer;
    private final ImageLoader      imageLoader;

    public TextRenderer(final Renderer<Object> renderer, final ImageLoader imageLoader)
    {
        super();
        this.renderer = renderer;
        this.imageLoader = imageLoader;
    }

}
