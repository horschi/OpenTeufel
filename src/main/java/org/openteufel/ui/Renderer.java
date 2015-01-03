package org.openteufel.ui;

import javax.swing.JFrame;

public interface Renderer<ImageType>
{
    public void initGame(final JFrame window);

    public ImageType loadImage(final int[] pixels, final int w, int h);

    public void unloadImage(ImageType image);

    public int getScreenWidth();
    public int getScreenHeight();
    
    public void startFrame();

    public void drawImage(final ImageType image, final int screenX, final int screenY);

    public void finishFrame();
}
