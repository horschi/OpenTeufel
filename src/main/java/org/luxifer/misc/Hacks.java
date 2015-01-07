/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.luxifer.misc;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 *
 * @author luxifer
 */
public class Hacks {

    public static void addToLibraryPath(String pathToAdd) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
        usrPathsField.setAccessible(true);
        final String[] paths = (String[]) usrPathsField.get(null);
        for (String path : paths) {
            if (path.equals(pathToAdd)) {
                return;
            }
        }
        final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
        newPaths[newPaths.length - 1] = pathToAdd;
        usrPathsField.set(null, newPaths);
    }
}
