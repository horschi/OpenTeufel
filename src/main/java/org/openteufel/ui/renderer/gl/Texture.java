package org.openteufel.ui.renderer.gl;

import org.lwjgl.opengl.GL11;

public class Texture {

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

    public void bind() {
        GL11.glBindTexture(target, textureID);
    }

    public float getX2() {
        return getX1() + (((float) this.imgWidth) / this.texWidth);
    }

    public float getX1() {
        return ((float) this.texXPos) / this.texWidth;
    }

    public float getY2() {
        return getY1() + (((float) this.imgHeight) / this.texHeight);
    }

    public float getY1() {
        return ((float) this.texYPos) / this.texWidth;
    }

    public int getImgHeight() {
        return imgHeight;
    }

    public void setImgHeight(final int imgHeight) {
        this.imgHeight = imgHeight;
    }

    public int getImgWidth() {
        return imgWidth;
    }

    public void setImgWidth(final int imgWidth) {
        this.imgWidth = imgWidth;
    }

    public int getTexWidth() {
        return texWidth;
    }

    public void setTexWidth(final int texWidth) {
        this.texWidth = texWidth;
    }

    public int getTexHeight() {
        return texHeight;
    }

    public void setTexHeight(final int texHeight) {
        this.texHeight = texHeight;
    }

    public float getWidthRatio() {
        return widthRatio;
    }

    public void setWidthRatio(final float widthRatio) {
        this.widthRatio = widthRatio;
    }

    public float getHeightRatio() {
        return heightRatio;
    }

    public void setHeightRatio(final float heightRatio) {
        this.heightRatio = heightRatio;
    }

}
