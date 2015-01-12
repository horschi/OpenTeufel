package org.openteufel.game;


import java.io.File;
import java.io.IOException;

import org.openteufel.file.GamedataLoader;
import org.openteufel.game.levels.LevelStateTown;
import org.openteufel.ui.Renderer;

public class GameRunner
{
    private final GamedataLoader dataLoader;
    private final Renderer<?>    renderer;

    public GameRunner(final Renderer<?> renderer) throws IOException
    {
        this.dataLoader = new GamedataLoader(new File("."));
        this.renderer = renderer;
    }

    public void runGame() throws Exception
    {
        final LevelState level = new LevelStateTown(this.dataLoader);
        final LevelRenderer levelrenderer = new LevelRenderer(this.dataLoader, level, this.renderer);

        int gametime = 0;
        while (true)
        {
            gametime++;
            levelrenderer.applyUserInput();
            level.runFrame(gametime);

            levelrenderer.renderFrame();
        }
    }

}
