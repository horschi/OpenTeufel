package org.openteufel.game.levels.gen;

public class StaticRingMapBitmapGenerator extends LevelBitmapGenerator
{
    @Override
    public void generate()
    {
        set(getWidth()/8, getHeight()/8, getWidth()*6/8, getHeight()*6/8, true);
        set(getWidth()/4, getHeight()/4, getWidth()/2, getHeight()/2, false);
    }
}
