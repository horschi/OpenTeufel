package org.openteufel.ui.renderer.gl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.openteufel.file.GamedataLoader;
import org.openteufel.ui.KeyboardEvent;
import org.openteufel.ui.KeyboardHandler;
import org.openteufel.ui.MouseEvent;
import org.openteufel.ui.MouseHandler;
import org.openteufel.ui.Renderer;
import org.openteufel.ui.TextRenderer;

public class ClassicGLRenderer implements Renderer<Sprite> {

    private static final Logger LOG = Logger.getLogger(ClassicGLRenderer.class.getName());
    private static final int AA_SAMPLES = 4;
    private int targetFps = -1;

    private GamedataLoader dataLoader = null;
    private TextRenderer textrenderer = null;


    private final List<String> messageStack = new ArrayList<String>();

    private List<drawImageInfo> drawImageList = new ArrayList<drawImageInfo>();
    private List<drawLineInfo> drawLineList = new ArrayList<drawLineInfo>();
    private final List<KeyboardHandler> keyboardHandlers = new ArrayList<KeyboardHandler>();

    private final List<MouseHandler> mouseHandlers = new ArrayList<MouseHandler>();

    @Override
    public int getTargetFps() {
        return targetFps;
    }

    @Override
    public void setTargetFps(int targetFps) {
        this.targetFps = targetFps;
        if (targetFps < 0) {
            Display.setVSyncEnabled(false);
        }

        if (targetFps == 0) {
            Display.setVSyncEnabled(true);
        }
    }

    public void registerKeyboardEventHandler(KeyboardHandler handler) {
        keyboardHandlers.add(handler);
    }

    public void registerMouseEventHandler(MouseHandler handler) {
        mouseHandlers.add(handler);
    }

    @Override
    public void initGame() {
        final PixelFormat pixelFormat = new PixelFormat(8, 8, 8, AA_SAMPLES);
        try {
            Display.setDisplayMode(new DisplayMode(800, 600));

            Display.setFullscreen(false);
            Display.setResizable(true);

            Display.setTitle("OpenTeufel");

            Display.create(pixelFormat);
            Mouse.setGrabbed(false);
            Mouse.create();
        } catch (LWJGLException ex) {
            Logger.getLogger(ClassicGLRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }

        setTargetFps(0);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        resize();

        try {
            Keyboard.create();
        } catch (LWJGLException ex) {
            Logger.getLogger(ClassicGLRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            this.dataLoader = new GamedataLoader(new File("."));
            this.textrenderer = new TextRenderer(this, dataLoader);
        } catch (IOException ex) {
            Logger.getLogger(ClassicGLRenderer.class.getName()).log(Level.SEVERE, null, ex);
            this.dataLoader = null;
            this.textrenderer = null;
        }
    }

    @Override
    public void close() {
    }

    @Override
    public Sprite loadImage(final int[] pixels, final int w, final int h) {
        if (pixels.length != w * h) {
            LOG.log(Level.WARNING, "Implausible Image Data: got {0}pixels for {1}*{2}px image (should be {3})", new Object[]{pixels.length, w, h, w * h});
        }
        try {
            return new Sprite(pixels, w, h);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public void unloadImage(final Sprite image) {
        //nop
    }

    @Override
    public int getScreenWidth() {
        return Display.getWidth();
    }

    @Override
    public int getScreenHeight() {
        return Display.getHeight();
    }

    @Override
    public void startFrame() {
        Textures.update();
        drawImageList = new ArrayList<drawImageInfo>();
        drawLineList = new ArrayList<drawLineInfo>();

        if (Display.wasResized()) {
            resize();
        }

        processEvents();

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
    }

    private void processEvents() {
        while (Mouse.next()) {
            for (MouseHandler h : mouseHandlers) {
                h.handleMouseEvent(new MouseEvent(Mouse.getEventButton(), Mouse.getEventX() - (getScreenWidth() / 2), (getScreenHeight() / 2) - Mouse.getEventY(), Mouse.getEventButtonState(), Mouse.getEventDX(), Mouse.getEventDY(), Mouse.getEventDWheel(), Mouse.getEventNanoseconds()));
            }
        }

        while (Keyboard.next()) {
            for (KeyboardHandler h : keyboardHandlers) {
                h.handleKeyboardEvent(new KeyboardEvent(Keyboard.getEventCharacter(), Keyboard.getEventKey(), Keyboard.getEventKeyState(), Keyboard.getEventNanoseconds()));
            }
            if (Keyboard.getEventKeyState() && Keyboard.getEventKey() == Keyboard.KEY_F11) {
                Textures.dumpTextures();
            }
        }

        if (!messageStack.isEmpty()) {
            for (String message : messageStack) {
                LOG.info(message);
            }
            messageStack.clear();
        }
    }

    @Override
    public void drawImage(final Sprite image, final int screenX, final int screenY, final int screenZ, final double brightness) {
        drawImageList.add(new drawImageInfo(image, screenX, screenY, screenZ, brightness));
    }

    @Override
    public void drawImageCentered(final Sprite image, final int screenX, final int screenY, final int screenZ, final int bottomOffset, final double brightness) {
        drawImage(image, screenX - (image.getWidth() >> 1), screenY + bottomOffset - image.getHeight(), screenZ, brightness);
    }

    @Override
    public void drawMarker(final int screenX, final int screenY, final String text) {
        drawRect(screenX - 32, screenY - 16, 64, 32);
        drawLine(screenX - 4, screenY - 4, screenX + 4, screenY + 4);
        drawLine(screenX + 4, screenY - 4, screenX - 4, screenY + 4);
        if(text != null)
            drawText(screenX, screenY, text);
    }

    private void drawText(final int x, final int y, final String t) {
        if(t != null)
            drawLineList.add(new drawLineInfo(x, y, t));
    }

    public void drawRect(final int screenX, final int screenY, final int width, final int height) {
        drawLine(screenX, screenY, screenX + width, screenY);
        drawLine(screenX + width, screenY, screenX + width, screenY + height);
        drawLine(screenX + width, screenY + height, screenX, screenY + height);
        drawLine(screenX, screenY + height, screenX, screenY);
    }

    @Override
    public void drawLine(final int screenX1, final int screenY1, final int screenX2, final int screenY2) {
        drawLineList.add(new drawLineInfo(screenX1, screenY1, screenX2, screenY2));
    }

    @Override
    public void finishFrame() {
        for (drawLineInfo drawCommand : drawLineList) {
            if (drawCommand.text != null) {
                textrenderer.writeText(drawCommand.X1, drawCommand.Y1, drawCommand.text, 16);
            }
        }
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
        GL11.glPopMatrix();
        Textures.setLastBound(-1);

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glColor4f(1, 0, 0, 0.3f);
        for (drawLineInfo drawCommand : drawLineList) {
            if (drawCommand.text == null) {
                GL11.glVertex3f(drawCommand.X1, drawCommand.Y1, 50);
                GL11.glVertex3f(drawCommand.X2, drawCommand.Y2, 50);
            }
        }
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_BLEND);

        Display.update();
        if (targetFps > 1) {
            Display.sync(targetFps);
        }
    }

    private void resize() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, getScreenWidth(), getScreenHeight(), 0, -100, 100);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glViewport(0, 0, getScreenWidth(), getScreenHeight());
    }

    @Override
    public void registerKeyboardHandler(KeyboardHandler handler) {
        keyboardHandlers.add(handler);
    }

    @Override
    public void registerMouseHandler(MouseHandler handler) {
        mouseHandlers.add(handler);
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

    private class drawLineInfo {

        private final int X1;
        private final int Y1;
        private final int X2;
        private final int Y2;
        private final String text;

        drawLineInfo(final int x1, final int y1, final String t) {
            this.X1 = x1;
            this.Y1 = y1;
            this.X2 = 0;
            this.Y2 = 0;
            text = t;
        }

        drawLineInfo(final int x1, final int y1, final int x2, final int y2) {
            this.X1 = x1;
            this.Y1 = y1;
            this.X2 = x2;
            this.Y2 = y2;
            text = null;
        }

    }


}
