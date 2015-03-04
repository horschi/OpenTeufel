package org.openteufel.game.levels.gen;

import org.openteufel.file.dun.DUNFile;

public class LevelGenerator
{
    private final DUNConstants constants;
    private final DUNFile[]    questDUNS;
    private final DUNFile      genDUN;

    public LevelGenerator(int width, int height, final DUNConstants constants, DUNFile[] questDUNS)
    {
        this.constants = constants;
        this.questDUNS = questDUNS;
        this.genDUN = new DUNFile(width, height);

        int[] roomSizes = new int[questDUNS.length];
        for (int i = 0; i < roomSizes.length; i++)
        {
            DUNFile d = questDUNS[i];
            roomSizes[i] = Math.max(d.getWidth(), d.getHeight());
        }

        LevelBitmapGenerator bitmapGenerator = new StaticRingMapBitmapGenerator();
        bitmapGenerator.init(width , height , new int[] { 3, 4, 5, 6, 7, 8, 9, 10, 11 });
        bitmapGenerator.generate();

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                boolean topleft = bitmapGenerator.get(x - 1, y);
                boolean topright = bitmapGenerator.get(x, y - 1);
                boolean top = bitmapGenerator.get(x - 1, y - 1);
                boolean bottom = bitmapGenerator.get(x, y);

                short v = constants.getFloor();
                if (bottom)
                { // room/free space
                    if(!top)
                    {
                    if (!topright && !topleft)// Top: /\
                        v = constants.getTopCornerWall();
                    else if (!topleft)
                        v = constants.getYWall();
                    else if (!topright)
                        v = constants.getXWall();
                    else
                        v = constants.getFloor();
                    }
                }
                else
                { // wall/solid
                    if (topright && topleft) // Bottom: /\
                        v = constants.getBottomCornerBackWall();
                    else if (topleft)
                        v = constants.getYBackWall();
                    else if (topright)
                        v = constants.getXBackWall();
                    else
                        v = constants.getBlank();
                }
                genDUN.setSquare(x, y, (short) (v - 1));
            }
        }
    }

    public DUNFile getResult()
    {
        return genDUN;
    }
}
