package org.openteufel.ui.renderer.gl;

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
    public Sprite(final int[] pixels, final int width, final int height) {
        texture = Textures.getTexture(pixels, width, height);
    }

    public Texture getTexture() {
        return texture;
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
     * @param z
     */
    public void draw(final int x, final int y, final int z) {
        GL11.glTexCoord2f(texture.getX1(), texture.getY1());
        GL11.glVertex3f(x, y, z);

        GL11.glTexCoord2f(texture.getX1(), texture.getY2());
        GL11.glVertex3f(x, y + getHeight(), z);

        GL11.glTexCoord2f(texture.getX2(), texture.getY2());
        GL11.glVertex3f(x + getWidth(), y + getHeight(), z);

        GL11.glTexCoord2f(texture.getX2(), texture.getY1());
        GL11.glVertex3f(x + getWidth(), y, z);
    }
}
