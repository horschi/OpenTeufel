/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openteufel.ui.renderer.gl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author luxifer
 */
public class Textures {

    private static final Logger LOG = Logger.getLogger(Textures.class.getName());
    private static final Textures firstTexture = new Textures();
    private static int lastBound = -1;

    public static Texture getTexture(final int[] pixels, final int width, final int height) {
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

    private final IntBuffer intbuf = BufferUtils.createIntBuffer(1);

    private static final int MAP_TEXTURE_SIZE = 4096;
    private static final int MIN_TEXTURE_SIZE = 8;

    private final Map<String, Texture> textureMap = new ConcurrentHashMap<String, Texture>();
    private final int[] allpixels;
    private final String[][] map;
    private final int id;
    private boolean needsUpdate = false;
    private Textures nextMap = null;

    public Textures() {
        allpixels = new int[MAP_TEXTURE_SIZE * MAP_TEXTURE_SIZE];
        map = new String[MAP_TEXTURE_SIZE / MIN_TEXTURE_SIZE][MAP_TEXTURE_SIZE / MIN_TEXTURE_SIZE];
        for (int x = 0; x < MAP_TEXTURE_SIZE / MIN_TEXTURE_SIZE; x++) {
            for (int y = 0; y < MAP_TEXTURE_SIZE / MIN_TEXTURE_SIZE; y++) {
                map[x][y] = "";
            }
        }
        GL11.glGenTextures(intbuf);
        id = intbuf.get(0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
    }

    private void updateTextureMap() {
        if (needsUpdate) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            final ByteBuffer buf = ByteBuffer.allocateDirect(MAP_TEXTURE_SIZE * MAP_TEXTURE_SIZE * 4);
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
            buf.rewind();
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, MAP_TEXTURE_SIZE, MAP_TEXTURE_SIZE, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
            needsUpdate = false;
        }
    }

    private void addFromPixels(final int[] pixels, final int width, final int height, final String textureHash) throws IllegalStateException {
        int texWidth = 2;
        int texHeight = 2;
        while (texWidth < width) {
            texWidth *= 2;
        }
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
        }

        int pos;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pos = (xpos + x) + ((ypos + y) * MAP_TEXTURE_SIZE);
                allpixels[pos] = pixels[x + (y * height)];
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

    public Texture getTexture(final String hash) {
        /*
         if (needsUpdate) {
         updateTextureMap();
         }
         */
        final Texture texture = textureMap.get(hash);
        if (texture == null && nextMap != null) {
            return nextMap.getTexture(hash);
        }
        return texture;
    }

    public void triggerUpdate() {
        updateTextureMap();
        if (nextMap != null) {
            nextMap.triggerUpdate();
        }
    }

    public void addTexture(final int[] pixels, final int width, final int height) {
        final String textureHash = String.valueOf(Arrays.hashCode(pixels));
        if (getTexture(textureHash) == null) {
            addFromPixels(pixels, width, height, textureHash);
        }
    }

    public int count() {
        if (nextMap == null) {
            return 1;
        } else {
            return nextMap.count() + 1;
        }
    }

}
