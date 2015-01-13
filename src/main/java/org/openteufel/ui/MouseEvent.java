package org.openteufel.ui;

/**
 *
 * @author luxifer
 */
public class MouseEvent {

    public enum eventType {

        COMBINED,
        SCROLL,
        CLICK,
        PRESS,
        RELEASE,
        WHEEL
    }

    public final int button;
    public final boolean eventState;
    public final int dx;
    public final int dy;
    public final int dz;
    public final long relativeNanos;
    public final int x;
    public final int y;
    public final eventType type;

    public MouseEvent(final int dz) {
        this(-1, 0, 0, true, 0, 0, dz, 0, eventType.WHEEL);
    }

    public MouseEvent(final int b, final int x, final int y) {
        this(b, x, y, true, 0, 0, 0, 0, eventType.CLICK);
    }

    public MouseEvent(final int b, final int x, final int y, final boolean s) {
        this(b, x, y, s, 0, 0, 0, 0, (s ? eventType.PRESS : eventType.RELEASE));
    }

    public MouseEvent(final int b, final int x, final int y, final boolean s, final int dx, final int dy, final int dz, final long n) {
        this(b, x, y, s, dx, dy, dz, n, eventType.COMBINED);
    }

    private MouseEvent(final int b, final int ax, final int ay, final boolean s, final int edx, final int edy, final int edz, final long n, eventType t) {
        button = b;
        x = ax;
        y = ay;
        eventState = s;
        dx = edx;
        dy = edy;
        dz = edz;
        relativeNanos = n;
        type = t;
    }
}
