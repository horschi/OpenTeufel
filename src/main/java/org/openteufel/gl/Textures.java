package org.openteufel.gl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author luxifer
 */
public class Textures {

    private static final Logger LOG = Logger.getLogger(Textures.class.getName());

    private static final Map<String, Texture> map = new HashMap<String, Texture>();

    /**
     *
     * @param pixels
     * @param width
     * @param height
     * @return
     */
    public static Texture getTexture(int[] pixels, int width, int height) {
        if (!map.containsKey(String.valueOf(Arrays.hashCode(pixels)))) {
            map.put(String.valueOf(Arrays.hashCode(pixels)), Texture.createFromPixels(pixels, width, height));
        }

        return map.get(String.valueOf(Arrays.hashCode(pixels)));

    }

    private Textures() {
    }

}
