package org.openteufel.ui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class DefaultRenderer implements Renderer<BufferedImage>
{
    private Canvas         canvas = null;
    private BufferStrategy buffer                 = null;
    private Graphics2D     currentGraphicsContext = null;

    @Override
    public void initGame(final JFrame window)
    {
        final JPanel panel = (JPanel) window.getContentPane();
        //        panel.setPreferredSize(new Dimension(1024, 768));
        //        panel.setLayout(null);
        panel.setIgnoreRepaint(true);

        this.canvas = new Canvas();
        this.canvas.setBackground(Color.black);
        panel.add(this.canvas);
        this.canvas.setSize(panel.getWidth(), panel.getHeight());
        window.show();
        this.canvas.setIgnoreRepaint(true);
        this.canvas.createBufferStrategy(2);
        this.buffer = this.canvas.getBufferStrategy();
        if (this.buffer == null)
            throw new NullPointerException();
    }

    @Override
    public BufferedImage loadImage(final int[] pixels, final int w, final int h)
    {
        final BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        img.getRaster().setDataElements(0, 0, w, h, pixels);
        return img;

        //        final GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        //        final BufferedImage image = gc.createCompatibleImage(img.getWidth(), img.getHeight(), Transparency.BITMASK);
        //        return image;
    }

    @Override
    public void unloadImage(final BufferedImage image)
    {
        image.flush();
    }

    @Override
    public void startFrame()
    {
        this.currentGraphicsContext = (Graphics2D) this.buffer.getDrawGraphics();
    }

    @Override
    public void drawImage(final BufferedImage image, final int screenX, final int screenY)
    {
        //        this.currentGraphicsContext.setColor(Color.GRAY);
        //        this.currentGraphicsContext.fillRect(screenX, screenY, image.getWidth(), image.getHeight());

        //        this.currentGraphicsContext.drawImage(image, screenX, screenY, null);
        this.currentGraphicsContext.drawImage(image, null, screenX, screenY);
    }

    @Override
    public void finishFrame()
    {
        this.currentGraphicsContext.dispose();
        this.buffer.show();
    }

    @Override
    public int getScreenWidth()
    {
        return this.canvas.getWidth();
    }

    @Override
    public int getScreenHeight()
    {
        return this.canvas.getHeight();
    }

}
