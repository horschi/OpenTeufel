package org.openteufel.game.utils;

import org.openteufel.game.WorldCallback;

public class PathFinder
{
    private static final int ADDITIONAL_SIZE = 5;
    private int[]            buf;
    private int              bufX, bufY;
    private int              bufW, bufH;
    private WorldCallback    world;

    public PathFinder(WorldCallback world, Position2d start, Position2d dest)
    {
        this(world, start.getTileX(), start.getTileY(), dest.getTileX(), dest.getTileY());
    }

    public PathFinder(WorldCallback world, int startTileX, int startTileY, int destTileX, int destTileY)
    {
        this.world = world;

        bufW = Math.abs(destTileX - startTileX) + (ADDITIONAL_SIZE << 1);
        bufH = Math.abs(destTileY - startTileY) + (ADDITIONAL_SIZE << 1);
        bufX = Math.min(startTileX, destTileX) - ADDITIONAL_SIZE;
        bufY = Math.min(startTileY, destTileY) - ADDITIONAL_SIZE;

        int s = bufW * bufH;
        buf = new int[s];

        for (int i = 0; i < s; i++)
            buf[i] = Integer.MAX_VALUE;

        _findPath(0, startTileX, startTileY, destTileX, destTileY);

//        for (int y = bufY; y < bufY + bufH; y++)
//        {
//            for (int x = bufX; x < bufX + bufW; x++)
//            {
//                int v = get(x, y);
//                if(v >= 99)
//                    System.out.print("  99");
//                else if(v > 9)
//                    System.out.print("  "+v);
//                else
//                    System.out.print("   "+v);
//            }
//            System.out.println();
//        }
    }

    private int get(int tileX, int tileY)
    {
        tileX -= bufX;
        tileY -= bufY;
        if (tileX < 0 || tileX >= bufW || tileY < 0 || tileY >= bufH)
            throw new IllegalStateException();
        return buf[tileX + (tileY * bufW)];
    }

    private void set(int tileX, int tileY, int val)
    {
        tileX -= bufX;
        tileY -= bufY;
        if (tileX < 0 || tileX >= bufW || tileY < 0 || tileY >= bufH)
            throw new IllegalStateException();
        buf[tileX + (tileY * bufW)] = val;
    }

    private void _findPath(int i, int startTileX, int startTileY, int destTileX, int destTileY)
    {
        int old = get(destTileX, destTileY);
        if (old > i)
        {
            set(destTileX, destTileY, i);

            for (int y = destTileY - 1; y <= destTileY + 1; y++)
            {
                if (y < bufY || y >= bufY + bufH)
                    continue;
                for (int x = destTileX - 1; x <= destTileX + 1; x++)
                {
                    if (x == destTileX && y == destTileY)
                        continue; // ignore center
                    if (x < bufX || x >= bufX + bufW)
                        continue;

                    if (world.isWalkable(x, y))
                    {
                        _findPath(i + 1, startTileX, startTileY, x, y);
                    }
                }
            }
        }
    }

    public Position2d getNextTile(Position2d pos)
    {
        int oldTileX = pos.getTileX();
        int oldTileY = pos.getTileY();

        int old = get(oldTileX, oldTileY);

        int bestTileX = -1;
        int bestTileY = -1;
        int bestDist = Integer.MAX_VALUE;

        for (int y = oldTileY - 1; y <= oldTileY + 1; y++)
        {
            if (y < bufY || y >= bufY + bufH)
                continue;
            for (int x = oldTileX - 1; x <= oldTileX + 1; x++)
            {
                if (x == oldTileX && y == oldTileY)
                    continue; // ignore center
                if (x < bufX || x >= bufX + bufW)
                    continue;

                int dist = get(x, y);
                if (dist < bestDist)
                {
                    bestDist = dist;
                    bestTileX = x;
                    bestTileY = y;
                }
            }
        }

        if (bestDist == Integer.MAX_VALUE || bestDist >= old || !world.isWalkable(bestTileX, bestTileY))
            return null;

        return Position2d.byTile(bestTileX, bestTileY);
    }
}
