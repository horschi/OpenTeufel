package org.openteufel.game.utils;

import org.openteufel.game.WorldCallback;

public class PathFinder
{
    private static final int ADDITIONAL_SIZE = 5;
    private int[]            buf;
    private int              bufX, bufY;
    private int              bufW, bufH;
    private WorldCallback    world;
    private int              destX, destY;

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

        destX = destTileX;
        destY = destTileY;

        int s = bufW * bufH;
        buf = new int[s];

        for (int i = 0; i < s; i++)
            buf[i] = Integer.MAX_VALUE;

        _findPath(0, startTileX, startTileY, destTileX, destTileY);
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

                    if(x != destTileX && y != destTileY) // diagonal
                    {
                        if (!world.isWalkable(destTileX, y))
                            continue;
                        if (!world.isWalkable(x, destTileY))
                            continue;
                    }
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

        int oldSteps = get(oldTileX, oldTileY);

        int bestTileX = -1;
        int bestTileY = -1;
        int bestDistSteps = Integer.MAX_VALUE-1;
        long bestDistBeeline = Long.MAX_VALUE;

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
                if (dist <= bestDistSteps)
                {
                    int difX = x - destX;
                    int difY = y - destY;
                    long distBeeline = difX*difX + difY*difY;
                    if (dist < bestDistSteps || distBeeline < bestDistBeeline)
                    {
                        bestDistBeeline = distBeeline;
                        bestDistSteps = dist;
                        bestTileX = x;
                        bestTileY = y;
                    }
                }
            }
        }

        if (bestDistSteps == Integer.MAX_VALUE || bestDistSteps >= oldSteps || !world.isWalkable(bestTileX, bestTileY))
            return null;

        return Position2d.byTile(bestTileX, bestTileY);
    }

    @Override
    public String toString()
    {
        return "PathFinder [bufX=" + bufX + ", bufY=" + bufY + ", bufW=" + bufW + ", bufH=" + bufH + "]";
        //      for (int y = bufY; y < bufY + bufH; y++)
        //      {
        //          for (int x = bufX; x < bufX + bufW; x++)
        //          {
        //              int v = get(x, y);
        //              if(v >= 99)
        //                  System.out.print("  99");
        //              else if(v > 9)
        //                  System.out.print("  "+v);
        //              else
        //                  System.out.print("   "+v);
        //          }
        //          System.out.println();
        //      }
    }

}
