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
        
//        LevelBitmapGenerator bitmapGenerator = new StaticMapBitmapGenerator(new boolean[][]//
//                        {//
//                        {false,false,false,false,false},//
//                        {false,true ,true ,true ,false},//
//                        {false,true ,false,true ,false},//
//                        {false,true ,true ,true ,false},//
//                        {false,false,false,false,false},//
//                        }//
//        );
        bitmapGenerator.generate();

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                boolean left = bitmapGenerator.get(x - 1, y);
                boolean right = bitmapGenerator.get(x, y - 1);
                boolean top = bitmapGenerator.get(x - 1, y - 1);
                boolean bottom = bitmapGenerator.get(x, y);

                short v = constants.getFloor();
                if (bottom)
                { // room/free space
                    if(!top && left && right)
                        v = constants.getBottomCornerWall();// \/ !top, others true
                    else if (!right && !left && !top)// Top: /\
                        v = constants.getTopCornerWall();
                    else if (!left && right && top)
                        v = constants.getRightCornerWall();
                    else if (!right && left && top)
                        v = constants.getLeftCornerWall();
                    else if (!left)
                        v = constants.getYWall(); // getRightCornerWall
                    else if (!right)
                        v = constants.getXWall(); // getLeftCornerWall
                    else
                        v = constants.getFloor();
                }
                else
                { // wall/solid
                    if(top && !left && !right)
                        v = constants.getBottomCornerBackWall();// \/ top only
                    else if (right && left && top) // Bottom: /\
                        v = constants.getTopCornerBackWall(); //
                    else if(left && !right && !top)
                        v = constants.getRightCornerBackWall();
                    else if(right && !left && !top)
                        v = constants.getLeftCornerBackWall();
                    else if (left)
                        v = constants.getYBackWall(); // getRightCornerBackWall
                    else if (right)
                        v = constants.getXBackWall(); // getLeftCornerBackWall
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
