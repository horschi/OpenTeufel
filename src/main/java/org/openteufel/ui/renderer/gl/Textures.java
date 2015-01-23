package org.openteufel.ui.renderer.gl;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class Textures {

    private static final Logger LOG = Logger.getLogger(Textures.class.getName());
    private static final Textures firstTexture = new Textures();
    private static int lastBound = -1;
    private static final int MAP_TEXTURE_SIZE = 4096;
    private static final int MIN_TEXTURE_SIZE = 8;

    public static Texture getTexture(final int[] pixels, final int width, final int height) throws Exception {
        final String textureHash = String.valueOf(Arrays.hashCode(pixels));
        firstTexture.addTexture(pixels, width, height);
        return firstTexture.getTexture(textureHash);
    }

    public static void update() {
        firstTexture.triggerUpdate();
    }

    public static int getLastBound() {
        return lastBound;
    }

    public static void setLastBound(final int lastBound) {
        Textures.lastBound = lastBound;
    }

    public static int countMaps() {
        return firstTexture.count();
    }

    public static void dumpTextures() {
        Textures.firstTexture.dumpTextures(0);
    }

    private final IntBuffer intbuf = BufferUtils.createIntBuffer(1);

    private final Map<String, Texture> textureMap = new ConcurrentHashMap<String, Texture>();
    private final int[] allpixels;
    private final String[][] map;
    private final int id;
    private boolean needsUpdate = false;
    private Textures nextMap = null;

    private Textures() {
        allpixels = new int[MAP_TEXTURE_SIZE * MAP_TEXTURE_SIZE];
        map = new String[MAP_TEXTURE_SIZE / MIN_TEXTURE_SIZE][MAP_TEXTURE_SIZE / MIN_TEXTURE_SIZE];
        for (int x = 0; x < MAP_TEXTURE_SIZE / MIN_TEXTURE_SIZE; x++) {
            for (int y = 0; y < MAP_TEXTURE_SIZE / MIN_TEXTURE_SIZE; y++) {
                map[x][y] = "";
            }
        }
        GL11.glGenTextures(intbuf);
        id = intbuf.get(0);
        if (id == 0) {
            LOG.severe("got no texture id!");
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
    }

    private void updateTextureMap() {
        if (needsUpdate) {
            ByteBuffer buf = getBuffer(true);
            buf.rewind();

            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, MAP_TEXTURE_SIZE, MAP_TEXTURE_SIZE, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);

            needsUpdate = false;
        }
    }

    private void addFromPixels(int[] pixels, final int width, final int height, final String textureHash) throws Exception {
        if (pixels.length != width * height) {
            throw new Exception("Implausible Data: Pixel array has length " + pixels.length + " but should have length " + (width * height) + " (width=" + width + " * height=" + height + ")");
        }

        int texWidth = 2;
        while (texWidth < width) {
            texWidth *= 2;
        }

        int texHeight = 2;
        while (texHeight < height) {
            texHeight *= 2;
        }

        int xpos = -1, ypos = -1;
        final int wunits = (texWidth / MIN_TEXTURE_SIZE);
        final int hunits = (texHeight / MIN_TEXTURE_SIZE);

        test:
        for (int y = 0; y < (MAP_TEXTURE_SIZE / MIN_TEXTURE_SIZE) - hunits; y += hunits) {
            for (int x = 0; x < MAP_TEXTURE_SIZE / MIN_TEXTURE_SIZE - wunits; x += wunits) {
                boolean isFree = true;
                subtest:
                for (int sy = y; sy < y + hunits; sy++) {
                    for (int sx = x; sx < x + wunits; sx++) {
                        if (!map[sx][sy].isEmpty()) {
                            isFree = false;
                            break subtest;
                        }
                    }
                }
                if (isFree) {
                    for (int sy = y; sy < y + hunits; sy++) {
                        for (int sx = x; sx < x + wunits; sx++) {
                            map[sx][sy] = textureHash;
                        }
                    }
                    xpos = x * MIN_TEXTURE_SIZE;
                    ypos = y * MIN_TEXTURE_SIZE;
                    break test;
                }
            }
        }

        if (xpos < 0 || ypos < 0) {
            if (nextMap == null) {
                nextMap = new Textures();
            }
            nextMap.addFromPixels(pixels, width, height, textureHash);
        } else {
            int pos;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    pos = (xpos + x) + ((ypos + y) * MAP_TEXTURE_SIZE);
                    try {
                        allpixels[pos] = pixels[x + (y * width)];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new Exception(e);
                    }

                }
            }

            final Texture result = new Texture(GL11.GL_TEXTURE_2D, id);
            result.setTexHeight(MAP_TEXTURE_SIZE);
            result.setTexWidth(MAP_TEXTURE_SIZE);
            result.setTexXPos(xpos);
            result.setTexYPos(ypos);
            result.setImgWidth(width);
            result.setImgHeight(height);
            result.setImgHash(textureHash);

            textureMap.put(textureHash, result);

            needsUpdate = true;
        }

    }

    private Texture getTexture(final String hash) {
        final Texture texture = textureMap.get(hash);
        if (texture == null && nextMap != null) {
            return nextMap.getTexture(hash);
        }
        return texture;
    }

    private void triggerUpdate() {
        updateTextureMap();
        if (nextMap != null) {
            nextMap.triggerUpdate();
        }
    }

    private void addTexture(final int[] pixels, final int width, final int height) throws Exception {
        final String textureHash = String.valueOf(Arrays.hashCode(pixels));
        if (getTexture(textureHash) == null) {
            addFromPixels(pixels, width, height, textureHash);
        }
    }

    private int count() {
        if (nextMap == null) {
            return 1;
        } else {
            return nextMap.count() + 1;
        }
    }

    private ByteBuffer getBuffer(boolean direct) {
        final ByteBuffer buf;
        if (direct) {
            buf = ByteBuffer.allocateDirect(MAP_TEXTURE_SIZE * MAP_TEXTURE_SIZE * 4);
        } else {
            buf = ByteBuffer.allocate(MAP_TEXTURE_SIZE * MAP_TEXTURE_SIZE * 4);
        }
        
        buf.order(ByteOrder.LITTLE_ENDIAN);
        for (int y = 0; y < MAP_TEXTURE_SIZE; y++) {
            buf.position(y * MAP_TEXTURE_SIZE * 4);
            for (int x = 0; x < MAP_TEXTURE_SIZE; x++) {
                final int p = allpixels[(y * MAP_TEXTURE_SIZE) + x];
                buf.put((byte) ((p >> 16) & 0xff));
                buf.put((byte) ((p >> 8) & 0xff));
                buf.put((byte) ((p) & 0xff));
                buf.put((byte) ((p >> 24) & 0xff));
            }
        }
        return buf;
        
    }

    private void dumpTextures(int no) {
        ColorModel cm = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8, 8}, true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
        WritableRaster raster = cm.createCompatibleWritableRaster(MAP_TEXTURE_SIZE, MAP_TEXTURE_SIZE);
        ByteBuffer bb = ByteBuffer.allocate(MAP_TEXTURE_SIZE * MAP_TEXTURE_SIZE * 4);
        raster.setDataElements(0, 0, MAP_TEXTURE_SIZE, MAP_TEXTURE_SIZE, getBuffer(false).array());
        BufferedImage image = new BufferedImage(cm, raster, false, null);

        File imageFile = new File("texturemap-" + no + ".png");
        try {
            ImageIO.write(image, "png", imageFile);
        } catch (IOException ex) {
            Logger.getLogger(Textures.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (this.nextMap != null) {
            nextMap.dumpTextures(no + 1);
        }
    }


}
