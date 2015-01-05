package org.openteufel.file.cel;


public class CELFrame
{
    private final int[] pixels;
    private final int   width, height;

    public CELFrame(final int[] pixels, final int width, final int height)
    {
        this.pixels = pixels;
        this.width = width;
        this.height = height;
        if (pixels.length != (width * height))
            throw new IllegalArgumentException("Invalid size");
    }

    public int[] getPixels()
    {
        return this.pixels;
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    @Override
    public String toString()
    {
        return "CELFrame [pixels=" + this.pixels.length + ", width=" + this.width + ", height=" + this.height + "]";
    }
}
