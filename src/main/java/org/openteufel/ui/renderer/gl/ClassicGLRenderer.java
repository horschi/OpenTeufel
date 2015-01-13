package org.openteufel.ui.renderer.gl;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.openteufel.ui.Renderer;

/**
 *
 * @author luxifer
 */
public class ClassicGLRenderer implements Renderer<Sprite> {

    private static final Logger LOG = Logger.getLogger(ClassicGLRenderer.class.getName());
    private static final int AA_SAMPLES = 4;
    public static int targetFps = 0;

    private final List<String> messageStack = new ArrayList<String>();
    private Point lastClick;

    private List<drawImageInfo> drawImageList = new ArrayList<drawImageInfo>();

    /**
     *
     * @param window
     */
    @Override
    public void initGame() {
        final PixelFormat pixelFormat = new PixelFormat(8, 8, 8, AA_SAMPLES);
        try {
            Display.setDisplayMode(new DisplayMode(800, 600));

            Display.setFullscreen(false);
            Display.setResizable(true);

            if (targetFps < 0) {
                Display.setVSyncEnabled(false);
            }

            if (targetFps == 0) {
                Display.setVSyncEnabled(true);
            }


            Display.setTitle("OpenTeufel");

            Display.create(pixelFormat);
            Mouse.setGrabbed(false);
            Mouse.create();
        } catch (LWJGLException ex) {
            Logger.getLogger(ClassicGLRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        resize();
        
        try {
            Keyboard.create();
        } catch (LWJGLException ex) {
            Logger.getLogger(ClassicGLRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    @Override
    public void close() {
    }
    
    /**
     *
     * @param pixels
     * @param w
     * @param h
     * @return
     */
    @Override
    public Sprite loadImage(final int[] pixels, final int w, final int h) {
        try
        {
            return new Sprite(pixels, w, h);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param image
     */
    @Override
    public void unloadImage(final Sprite image) {
        //nop
    }

    /**
     *
     * @return
     */
    @Override
    public int getScreenWidth() {
        return Display.getWidth();
    }

    /**
     *
     * @return
     */
    @Override
    public int getScreenHeight() {
        return Display.getHeight();
    }

    /**
     *
     */
    @Override
    public void startFrame() {
        Textures.update();
        drawImageList = new ArrayList<drawImageInfo>();

        if (Display.wasResized()) {
            resize();
        }

        processEvents();

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
    }

    private void processEvents() {
        while (Mouse.next()) {
            switch (Mouse.getEventButton()) {
                case 0:
                    if (!Mouse.getEventButtonState()) {
                    final Point cursorpos = new Point(Mouse.getEventX(), Mouse.getEventY());
                        this.lastClick = new Point(cursorpos.x - (getScreenWidth() / 2), (getScreenHeight() / 2) - cursorpos.y);
                    }
                    break;
                default:
                    break;

            }
        }

        if (!messageStack.isEmpty()) {
            for (String message : messageStack) {
                LOG.info(message);
            }
            messageStack.clear();
        }
    }

    /**
     *
     * @param image
     * @param screenX
     * @param screenY
     * @param screenZ
     * @param brightness
     */
    @Override
    public void drawImage(final Sprite image, final int screenX, final int screenY, final int screenZ, final double brightness) {
        drawImageList.add(new drawImageInfo(image, screenX, screenY, screenZ, brightness));
    }

    /**
     *
     * @param image
     * @param screenX
     * @param screenY
     * @param screenZ
     * @param bottomOffset
     * @param brightness
     */
    @Override
    public void drawImageCentered(final Sprite image, final int screenX, final int screenY, final int screenZ, final int bottomOffset, final double brightness) {
        drawImage(image, screenX - (image.getWidth() >> 1), screenY + bottomOffset - image.getHeight(), screenZ, brightness);
    }

    /**
     *
     * @param screenX
     * @param screenY
     * @param text
     */
    @Override
    public void drawMarker(final int screenX, final int screenY, final String text) {
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
    public void drawRect(final int screenX, final int screenY, final int width, final int height) {
        drawLine(screenX, screenY, screenX + width, screenY);
        drawLine(screenX + width, screenY, screenX + width, screenY + height);
        drawLine(screenX + width, screenY + height, screenX, screenY + height);
        drawLine(screenX, screenY + height, screenX, screenY);
    }

    /**
     *
     * @param screenX1
     * @param screenY1
     * @param screenX2
     * @param screenY2
     */
    @Override
    public void drawLine(final int screenX1, final int screenY1, final int screenX2, final int screenY2) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glColor4f(1, 0, 0, 1);
        GL11.glVertex2f(screenX1, screenY1);
        GL11.glVertex2f(screenX2, screenY2);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    /**
     *
     */
    @Override
    public void finishFrame() {
        int lx = 0, ly = 0, lz = 0;
        GL11.glBegin(GL11.GL_QUADS);
        for (drawImageInfo drawCommand : drawImageList) {
            if (Textures.getLastBound() != drawCommand.image.getTexture().getId()) {
                GL11.glEnd();
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
                Textures.setLastBound(drawCommand.image.getTexture().getId());
                drawCommand.image.getTexture().bind();
                lx = drawCommand.x;
                ly = drawCommand.y;
                lz = drawCommand.z;

                GL11.glPushMatrix();

                GL11.glTranslatef(lx, ly, lz);

                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                GL11.glBegin(GL11.GL_QUADS);
            }
            GL11.glColor4d(drawCommand.brightness, drawCommand.brightness, drawCommand.brightness, 1.0);
            drawCommand.image.draw(drawCommand.x - lx, drawCommand.y - ly, drawCommand.z - lz);
        }
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        Textures.setLastBound(-1);

        Display.update();
        if (targetFps > 1) {
            Display.sync(targetFps);
        }
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
    public int processKeyboard() {
        if (Keyboard.next()) {
            return Keyboard.getEventKey();
        }
        return -1;
    }

    private void resize() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, getScreenWidth(), getScreenHeight(), 0, -10, 10);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glViewport(0, 0, getScreenWidth(), getScreenHeight());
    }

    private class drawImageInfo {
        private final Sprite image;
        private final int x;
        private final int y;
        private final int z;
        private final double brightness;

        drawImageInfo(final Sprite image, final int x, final int y, final int z, final double brightness) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.z = z;
            this.brightness = brightness;
        }
    }

}
