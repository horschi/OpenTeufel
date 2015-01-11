package org.openteufel.gl;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferStrategy;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.openteufel.ui.Renderer;

/**
 *
 * @author luxifer
 */
public class ClassicGLRenderer implements Renderer<Sprite>, PropertyChangeListener {

    private static final Logger LOG = Logger.getLogger(ClassicGLRenderer.class.getName());

    private Canvas canvas;
    private boolean triggerresize;
    private long currentNano;
    private long lastFrameNano;
    private long diffNano;
    private long currentFps;
    private long lastUpdateNano;
    private final List<String> messageStack = new ArrayList<String>();
    private long speed;
    private Point lastClick;
    private BufferStrategy buffer;
    private long ms = 1;
    private long dcpf = 0;

    /**
     *
     * @param window
     */
    @Override
    public void initGame(final JFrame window) {
        final JPanel panel = (JPanel) window.getContentPane();
        //        panel.setPreferredSize(new Dimension(1024, 768));
        //        panel.setLayout(null);
        panel.setIgnoreRepaint(true);
        panel.addPropertyChangeListener("width", this);
        panel.addPropertyChangeListener("height", this);

        this.canvas = new Canvas();
        this.canvas.setBackground(Color.black);
        panel.add(this.canvas);
        this.canvas.setSize(panel.getWidth(), panel.getHeight());
        window.show();

        //this.canvas.setIgnoreRepaint(true);
        //this.canvas.createBufferStrategy(2);
        //this.buffer = this.canvas.getBufferStrategy();
        Display.setResizable(true);
        try {
            Display.setParent(canvas);
        } catch (LWJGLException ex) {
            Logger.getLogger(ClassicGLRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
        Display.sync(60);
        try {
            Display.create();
        } catch (LWJGLException ex) {
            Logger.getLogger(ClassicGLRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        resize();
    }

    /**
     *
     * @param pixels
     * @param w
     * @param h
     * @return
     */
    @Override
    public Sprite loadImage(int[] pixels, int w, int h) {
        return new Sprite(pixels, h, h);
    }

    /**
     *
     * @param image
     */
    @Override
    public void unloadImage(Sprite image) {
        //nop
    }

    /**
     *
     * @return
     */
    @Override
    public int getScreenWidth() {
        return this.canvas.getWidth();
    }

    /**
     *
     * @return
     */
    @Override
    public int getScreenHeight() {
        return this.canvas.getHeight();
    }

    /**
     *
     */
    @Override
    public void startFrame() {
        if (triggerresize) {
            resize();
        }
        // clear screen
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        processUpdates();
        processEvents();
        dcpf = 0;
        Display.sync(60);

    }

    private void processUpdates() {
        currentNano = System.nanoTime();
        diffNano = currentNano - lastFrameNano;
        currentFps = 1000000000 / (diffNano + 1);
        lastFrameNano = currentNano;
        if (currentNano - lastUpdateNano > 1000000000) {
            LOG.log(Level.INFO, "FPS: {0}", currentFps);
            //messageStack.add("Eye Position is: " + Camera.getViewPosition().toString());
            //messageStack.add("Eye Direction is: " + Camera.getViewDirection().toString());
            if (!messageStack.isEmpty()) {
                for (String message : messageStack) {
                    LOG.info(message);
                }
            }
            messageStack.clear();
            lastUpdateNano = currentNano;
        }
    }

    private void processEvents() {
        processMouse();
        processKeyboard();
    }

    private void processKeyboard() {
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                messageStack.add("Some key was pressed");
            } else {
                continue;
            }

            switch (Keyboard.getEventKey()) {
                case Keyboard.KEY_ESCAPE:
                    messageStack.add("Escape Key pressed");
                    break;
                case Keyboard.KEY_U:
                    break;
                case Keyboard.KEY_O:
                    ms *= 10;
                    messageStack.add("Draw delay set to " + ms);
                    break;
                case Keyboard.KEY_L:
                    ms /= 10;
                    if (ms < 0) {
                        ms = 1;
                    }
                    messageStack.add("Draw delay set to " + ms);
                    break;
            }
        }
        float amount = diffNano * speed / 1000;
        amount *= 0.1f;
    }

    private void processMouse() {
        while (Mouse.next()) {
            if (Mouse.getEventButtonState()) {
                messageStack.add("Key " + Mouse.getEventButton() + " was pressed (" + Mouse.getButtonName(Mouse.getEventButton()) + ")");
            } else {
                continue;
            }

            switch (Mouse.getEventButton()) {
                default:
                    final Point cursorpos = new Point(Mouse.getEventX(), Mouse.getEventY());
                    this.lastClick = new Point(cursorpos.x - (this.canvas.getWidth() / 2), (this.canvas.getHeight() / 2) - cursorpos.y);
                    break;

            }
        }
    }

    /**
     *
     * @param image
     * @param screenX
     * @param screenY
     * @param brightness
     */
    @Override
    public void drawImage(Sprite image, int screenX, int screenY, double brightness) {
        dcpf++;
        image.draw(screenX, screenY, brightness);
    }

    /**
     *
     * @param image
     * @param screenX
     * @param screenY
     * @param bottomOffset
     * @param brightness
     */
    @Override
    public void drawImageCentered(Sprite image, int screenX, int screenY, int bottomOffset, double brightness) {
        drawImage(image, screenX - (image.getWidth() >> 1), screenY + bottomOffset - image.getHeight(), brightness);
    }

    /**
     *
     * @param screenX
     * @param screenY
     * @param text
     */
    @Override
    public void drawMarker(int screenX, int screenY, String text) {
        drawRect(screenX - 32, screenY - 16, 64, 32);
        drawLine(screenX - 4, screenY - 4, screenX + 4, screenY + 4);
        drawLine(screenX + 4, screenY - 4, screenX - 4, screenY + 4);

    }

    /**
     *
     * @param screenX
     * @param screenY
     * @param width
     * @param height
     */
    public void drawRect(int screenX, int screenY, int width, int height) {
        drawLine(screenX, screenY, screenX + width, screenY);
        drawLine(screenX + width, screenY, screenX + width, screenY + height);
        drawLine(screenX + width, screenY + height, screenX, screenY + height);
        drawLine(screenX + width, screenY + height, screenX, screenY);
    }

    /**
     *
     * @param screenX1
     * @param screenY1
     * @param screenX2
     * @param screenY2
     */
    @Override
    public void drawLine(int screenX1, int screenY1, int screenX2, int screenY2) {
        GL11.glPushMatrix();
        GL11.glBegin(GL11.GL_LINE);
        //GL11.glColor3f(1f, 0, 0);
        GL11.glVertex2f(screenX1, screenY1);
        GL11.glVertex2f(screenX2, screenY2);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    /**
     *
     */
    @Override
    public void finishFrame() {
        messageStack.add("Draw calls per frame: " + dcpf);
        Display.update();
    }

    /**
     *
     * @return
     */
    @Override
    public Point getLastRelativeClickPos() {
        final Point ret = this.lastClick;
        this.lastClick = null;
        return ret;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("width".equals(evt.getPropertyName()) || "height".equals(evt.getPropertyName())) {
            this.triggerresize = true;
        }
    }

    private void resize() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, getScreenWidth(), getScreenHeight(), 0, -1, 1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glViewport(0, 0, getScreenWidth(), getScreenHeight());
        triggerresize = false;
    }

}
