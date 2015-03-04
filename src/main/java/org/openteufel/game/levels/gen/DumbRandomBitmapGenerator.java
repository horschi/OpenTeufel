package org.openteufel.game.levels.gen;

public class DumbRandomBitmapGenerator extends LevelBitmapGenerator
{
    @Override
    public void generate()
    {
        int i=0;
        for (int size : getRoomSizes())
        {
            int x = (int) (Math.abs(Math.random()) * (getWidth() - size));
            int y = (int) (Math.abs(Math.random()) * (getHeight() - size));

            setRoomPosition(i++, x, y);
            set(x, y, size, size, true);
        }
    }
}
