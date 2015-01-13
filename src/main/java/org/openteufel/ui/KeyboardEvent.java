/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openteufel.ui;

import java.util.logging.Logger;

/**
 *
 * @author luxifer
 */
public class KeyboardEvent {

    private static final Logger LOG = Logger.getLogger(KeyboardEvent.class.getName());

    public final char character;
    public final int key;
    public final boolean state;
    public final long relativeNanos;

    public KeyboardEvent(final int k) {
        this(' ', k, true, 0);
    }

    public KeyboardEvent(final char c, final int k, final boolean s, final long r) {
        character = c;
        key = k;
        state = s;
        relativeNanos = r;
    }

}
