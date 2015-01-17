package org.openteufel.ui.renderer.gl;

import java.util.logging.Logger;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author luxifer
 */
public class Texture {

    private static final Logger LOG = Logger.getLogger(Texture.class.getName());

    private final int target;
    private final int textureID;
    private int imgHeight;
    private int imgWidth;
    private int texWidth;
    private int texHeight;
    private int texXPos;
    private int texYPos;

    private float widthRatio;
    private float heightRatio;

    private String imgHash;

    /**
     *
     * @param target
     * @param textureID
     */
    public Texture(final int target, final int textureID) {
        this.target = target;
        this.textureID = textureID;
    }

    public int getId() {
        return textureID;
    }

    public String getImgHash() {
        return imgHash;
    }

    public void setImgHash(final String imgHash) {
        this.imgHash = imgHash;
    }

    public int getTexXPos() {
        return texXPos;
    }

    public void setTexXPos(final int xpos) {
        this.texXPos = xpos;
    }

    public int getTexYPos() {
        return texYPos;
    }

    public void setTexYPos(final int ypos) {
        this.texYPos = ypos;
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
    public float getX2() {
        return getX1() + (((float) this.imgWidth) / this.texWidth);
    }

    public float getX1() {
        return ((float) this.texXPos) / this.texWidth;
    }

    /**
     *
     * @return
     */
    public float getY2() {
        return getY1() + (((float) this.imgHeight) / this.texHeight);
    }

    public float getY1() {
        return ((float) this.texYPos) / this.texWidth;
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
    public void setImgHeight(final int imgHeight) {
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
    public void setImgWidth(final int imgWidth) {
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
    public void setTexWidth(final int texWidth) {
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
    public void setTexHeight(final int texHeight) {
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
    public void setWidthRatio(final float widthRatio) {
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
    public void setHeightRatio(final float heightRatio) {
        this.heightRatio = heightRatio;
    }

}
