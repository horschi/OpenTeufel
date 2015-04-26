package org.openteufel.game.levels.gen;

public class StaticMapBitmapGenerator extends LevelBitmapGenerator
{
    private boolean[][] map;
    
    public StaticMapBitmapGenerator(boolean[][] map)
    {
        this.map = map;
    }
    
    @Override
    public void generate()
    {
        init(map.length, map[0].length, new int[]{});
        for(int y=0;y<map[0].length;y++)
        {
            for(int x=0;x<map.length;x++)
            {
                set(x, y, map[x][y]);
            }
        }
    }
}
