package org.openteufel.game.utils;

public class EntityUtils
{
    public static int calcDirection8(final int startX, final int startY, final int destX, final int destY, final int defaultDirection)
    {
        final int diffX = destX - startX;
        final int diffY = destY - startY;
        return calcDirection8(diffX, diffY, defaultDirection);
    }

    public static int calcDirection8(final int diffX, final int diffY, final int defaultDirection)
    {
        final double revtheta = Math.atan2(diffY, diffX);
        final int baseDir = (int) ((revtheta * (4.0 / Math.PI)));
        return (baseDir + 7) & 7;
    }

    public static int calcDirection16(final int startX, final int startY, final int destX, final int destY, final int defaultDirection)
    {
        final int diffX = destX - startX;
        final int diffY = destY - startY;
        return calcDirection16(diffX, diffY, defaultDirection);
    }

    public static int calcDirection16(final int diffX, final int diffY, final int defaultDirection)
    {
        final double revtheta = Math.atan2(diffY, diffX);
        final int baseDir = (int) ((revtheta * (8.0 / Math.PI)));
        return (baseDir + 14) & 15;
    }
}
