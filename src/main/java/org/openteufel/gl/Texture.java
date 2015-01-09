/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openteufel.gl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Hashtable;
import java.util.logging.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

/**
 *
 * @author luxifer
 */
public class Texture {

    private static final Logger LOG = Logger.getLogger(Texture.class.getName());
    private static final IntBuffer intbuf = BufferUtils.createIntBuffer(1);
    private static final ColorModel glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8, 8}, true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
    private static final ColorModel glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8, 0}, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);

    /**
     *
     * @param pixels
     * @param width
     * @param height
     * @return
     */
    public static Texture createFromPixels(int[] pixels, int width, int height) {
        final GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        final BufferedImage image = gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        image.getRaster().setDataElements(0, 0, width, height, pixels);

        GL11.glGenTextures(Texture.intbuf);
        final int id = Texture.intbuf.get(0);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

        final int format = image.getColorModel().hasAlpha() ? GL11.GL_RGBA : GL11.GL_RGB;

        ByteBuffer texData;
        WritableRaster raster;
        BufferedImage texImage;

        int texWidth = 2;
        int texHeight = 2;

        while (texWidth < image.getWidth()) {
            texWidth *= 2;
        }
        while (texHeight < image.getHeight()) {
            texHeight *= 2;
        }

        if (image.getColorModel().hasAlpha()) {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 4, null);
            texImage = new BufferedImage(Texture.glAlphaColorModel, raster, false, new Hashtable<String, Object>());
        } else {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 3, null);
            texImage = new BufferedImage(Texture.glColorModel, raster, false, new Hashtable<String, Object>());
        }

        final Graphics g = texImage.getGraphics();
        g.setColor(new Color(0f, 0f, 0f, 0f));
        g.fillRect(0, 0, texWidth, texHeight);
        g.drawImage(image, 0, 0, null);

        final byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();

        texData = ByteBuffer.allocateDirect(data.length);
        texData.order(ByteOrder.nativeOrder());
        texData.put(data, 0, data.length);
        texData.flip();

        glTexParameteri(GL11.GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        glTexParameteri(GL11.GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        glTexImage2D(GL11.GL_TEXTURE_2D, 0, format, image.getWidth(), image.getHeight(), 0, format, GL_UNSIGNED_BYTE, texData);

        final Texture result = new Texture(GL11.GL_TEXTURE_2D, id);
        result.setTexHeight(texHeight);
        result.setTexWidth(texWidth);
        result.setImgWidth(width);
        result.setImgHeight(height);

        return result;
    }

    private final int target;
    private final int textureID;
    private int imgHeight;
    private int imgWidth;
    private int texWidth;
    private int texHeight;
    private float widthRatio;
    private float heightRatio;

    /**
     *
     * @param target
     * @param textureID
     */
    public Texture(int target, int textureID) {
        this.target = target;
        this.textureID = textureID;
    }

    /**
     *
     */
    public void bind() {
        GL11.glBindTexture(target, textureID);
    }

    /**
     *
     * @return
     */
    public float getWidth() {
        return ((float) this.imgWidth) / this.texWidth;
    }

    /**
     *
     * @return
     */
    public float getHeight() {
        return ((float) this.imgHeight) / this.texHeight;
    }

    /**
     *
     * @return
     */
    public int getImgHeight() {
        return imgHeight;
    }

    /**
     *
     * @param imgHeight
     */
    public void setImgHeight(int imgHeight) {
        this.imgHeight = imgHeight;
    }

    /**
     *
     * @return
     */
    public int getImgWidth() {
        return imgWidth;
    }

    /**
     *
     * @param imgWidth
     */
    public void setImgWidth(int imgWidth) {
        this.imgWidth = imgWidth;
    }

    /**
     *
     * @return
     */
    public int getTexWidth() {
        return texWidth;
    }

    /**
     *
     * @param texWidth
     */
    public void setTexWidth(int texWidth) {
        this.texWidth = texWidth;
    }

    /**
     *
     * @return
     */
    public int getTexHeight() {
        return texHeight;
    }

    /**
     *
     * @param texHeight
     */
    public void setTexHeight(int texHeight) {
        this.texHeight = texHeight;
    }

    /**
     *
     * @return
     */
    public float getWidthRatio() {
        return widthRatio;
    }

    /**
     *
     * @param widthRatio
     */
    public void setWidthRatio(float widthRatio) {
        this.widthRatio = widthRatio;
    }

    /**
     *
     * @return
     */
    public float getHeightRatio() {
        return heightRatio;
    }

    /**
     *
     * @param heightRatio
     */
    public void setHeightRatio(float heightRatio) {
        this.heightRatio = heightRatio;
    }

}
