/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openteufel.gl;

import java.util.logging.Logger;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author luxifer
 */
public class Sprite {

    private static final Logger LOG = Logger.getLogger(Sprite.class.getName());

    private final Texture texture;

    /**
     *
     * @param pixels
     * @param width
     * @param height
     */
    public Sprite(int[] pixels, int width, int height) {
        texture = Textures.getTexture(pixels, width, height);
    }

    /**
     *
     * @return
     */
    public int getWidth() {
        return texture.getImgWidth();
    }

    /**
     *
     * @return
     */
    public int getHeight() {
        return texture.getImgHeight();
    }

    /**
     *
     * @param x
     * @param y
     */
    public void draw(int x, int y) {
        texture.bind();
        GL11.glPushMatrix();

        GL11.glTranslatef(x, y, 0);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1, 1, 1, 1);

        GL11.glBegin(GL11.GL_QUADS);

        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(0, 0);

        GL11.glTexCoord2f(0, texture.getHeight());
        GL11.glVertex2f(0, getHeight());

        GL11.glTexCoord2f(texture.getWidth(), texture.getHeight());
        GL11.glVertex2f(getWidth(), getHeight());

        GL11.glTexCoord2f(texture.getWidth(), 0);
        GL11.glVertex2f(getWidth(), 0);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
