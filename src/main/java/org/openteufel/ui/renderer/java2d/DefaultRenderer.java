package org.openteufel.ui.renderer.java2d;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;
import org.openteufel.ui.KeyboardEvent;
import org.openteufel.ui.KeyboardHandler;
import org.openteufel.ui.MouseHandler;

import org.openteufel.ui.Renderer;

public class DefaultRenderer implements Renderer<BufferedImage>, MouseInputListener, MouseWheelListener, KeyListener {
    private Window window=null;
    private Canvas         canvas = null;
    private BufferStrategy buffer                 = null;
    private Graphics2D     currentGraphicsContext = null;
    private Point          lastClick              = null;
    private long fps;
    private long lastFrameTime;
    private int lastKey = -1;
    private int targetFps;

    private final List<KeyboardHandler> keyboardHandlers = new ArrayList<KeyboardHandler>();

    private final List<MouseHandler> mouseHandlers = new ArrayList<MouseHandler>();

    public DefaultRenderer()
    {
        System.setProperty("sun.java2d.opengl", "true");
        lastFrameTime = System.nanoTime();
    }

    @Override
    public int getTargetFps()
    {
        return targetFps;
    }

    @Override
    public void setTargetFps(int targetFps)
    {
        this.targetFps = targetFps;
        if (targetFps < 0) {
            // unlimited
        }

        if (targetFps == 0) {
            // vsync
        }
    }

    @Override
    public void registerKeyboardHandler(KeyboardHandler handler)
    {
        keyboardHandlers.add(handler);
    }

    @Override
    public void registerMouseHandler(MouseHandler handler)
    {
        mouseHandlers.add(handler);
    }

    @Override
    public void initGame()
    {
        window = new Window();
        final JPanel panel = (JPanel) window.getContentPane();
        //        panel.setPreferredSize(new Dimension(1024, 768));
        //        panel.setLayout(null);
        panel.setIgnoreRepaint(true);

        this.canvas = new Canvas();
        this.canvas.setBackground(Color.black);
        panel.add(this.canvas);
        this.canvas.setSize(panel.getWidth(), panel.getHeight());
        this.canvas.addMouseListener(this);
        this.canvas.addMouseMotionListener(this);
        this.canvas.addMouseWheelListener(this);
        this.canvas.addKeyListener(this);

        window.show();
        this.canvas.setIgnoreRepaint(true);
        this.canvas.createBufferStrategy(2);
        this.buffer = this.canvas.getBufferStrategy();
        if (this.buffer == null)
            throw new NullPointerException();
    }
    
    @Override
    public void close()
    {
        window.hide();
        window = null;
    }

    @Override
    public BufferedImage loadImage(final int[] pixels, final int w, final int h)
    {
        //        final BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        //        img.getRaster().setDataElements(0, 0, w, h, pixels);
        //        return img;

        final GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        final BufferedImage image = gc.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        image.getRaster().setDataElements(0, 0, w, h, pixels);
        return image;
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
        this.currentGraphicsContext.setColor(Color.black);
        this.currentGraphicsContext.fillRect(0, 0, this.getScreenWidth(), this.getScreenHeight());
    }

    @Override
    public void drawImage(final BufferedImage image, final int screenX, final int screenY, final int screenZ, final double brightness)    {
        this.currentGraphicsContext.drawImage(image, null, screenX, screenY);
    }

    @Override
    public void drawImageCentered(final BufferedImage image, final int screenX, final int screenY, final int screenZ, final int bottomOffset, final double brightness)    {
        this.currentGraphicsContext.drawImage(image, null, screenX - (image.getWidth() >> 1), screenY + bottomOffset - image.getHeight());
    }

    @Override
    public void drawMarker(final int screenX, final int screenY, final String text)
    {
        this.currentGraphicsContext.setColor(Color.red);
        this.currentGraphicsContext.drawRect(screenX - 32, screenY - 16, 64, 32);
        this.currentGraphicsContext.drawLine(screenX - 4, screenY - 4, screenX + 4, screenY + 4);
        this.currentGraphicsContext.drawLine(screenX + 4, screenY - 4, screenX - 4, screenY + 4);
        if (text != null)
        {
            this.currentGraphicsContext.setColor(Color.yellow);
            this.currentGraphicsContext.drawString(text, screenX - 31, screenY + 15);
        }
    }

    @Override
    public void drawLine(final int screenX1, final int screenY1, final int screenX2, final int screenY2)
    {
        this.currentGraphicsContext.setColor(Color.red);
        this.currentGraphicsContext.drawLine(screenX1, screenY1, screenX2, screenY2);
    }

    @Override
    public void finishFrame()
    {
        this.currentGraphicsContext.dispose();
        this.buffer.show();

        if(targetFps > 0) {
            long now = System.nanoTime();
            long tdif = now - lastFrameTime;
            if (tdif < 1000000000 / targetFps) {
                try {
                    Thread.sleep(((1000000000 / targetFps) - tdif) / 1000000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DefaultRenderer.class.getName()).log(Level.WARNING, "Someone interrupted my slumber: {0}\n{1}", new Object[]{ex.toString(), ex.getStackTrace().toString()});
                }
            }
            fps = 1000000000 / tdif;
            lastFrameTime = now;
        }
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

    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        for (MouseHandler h : mouseHandlers) {
            h.handleMouseEvent(new org.openteufel.ui.MouseEvent(e.getWheelRotation()));
        }
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
    }

    @Override
    public void mouseEntered(final MouseEvent arg0)
    {
    }

    @Override
    public void mouseExited(final MouseEvent arg0)
    {
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        for (MouseHandler h : mouseHandlers) {
            h.handleMouseEvent(new org.openteufel.ui.MouseEvent(e.getButton() - 1, e.getX() - (getScreenWidth() / 2), e.getY() - (getScreenHeight() / 2), true));
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e)    {
        for (MouseHandler h : mouseHandlers) {
            h.handleMouseEvent(new org.openteufel.ui.MouseEvent(e.getButton() - 1, e.getX() - (getScreenWidth() / 2), e.getY() - (getScreenHeight() / 2), false));
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        for (MouseHandler h : mouseHandlers) {
            h.handleMouseEvent(new org.openteufel.ui.MouseEvent(e.getX() - (getScreenWidth() / 2), e.getY() - (getScreenHeight() / 2)));
        }
    }

    public Point getLastRelativeClickPos()
    {
        final Point ret = this.lastClick;
        this.lastClick = null;
        return ret;
    }

    public int processKeyboard()
    {
        if (lastKey >= 0) {
            for (KeyboardHandler h : keyboardHandlers) {
                h.handleKeyboardEvent(new KeyboardEvent(lastKey));
            }
        }
        int k = lastKey;
        lastKey = -1;
        return k;
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        lastKey = e.getKeyCode();
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }

}
