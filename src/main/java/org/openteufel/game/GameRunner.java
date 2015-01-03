package org.openteufel.game;


import java.io.File;
import java.io.IOException;

import org.openteufel.file.GamedataLoader;
import org.openteufel.ui.DefaultRenderer;
import org.openteufel.ui.Renderer;
import org.openteufel.ui.Window;

public class GameRunner
{
    private final GamedataLoader dataLoader;
    private final Renderer<?>    renderer;

    public GameRunner() throws IOException
    {
        this.dataLoader = new GamedataLoader(new File("."));
        this.renderer = new DefaultRenderer();
    }

    public void runGame() throws Exception
    {
        final Window window = new Window();
        window.setSize(1024, 768);
        this.renderer.initGame(window);

        final LevelState level = new LevelStateTown(this.dataLoader);
        final LevelRenderer levelrenderer = new LevelRenderer(this.dataLoader, level, this.renderer);

        final int gametime = 0;
        while (true)
        {

            levelrenderer.renderFrame();
            Thread.sleep(30L);
        }
    }

}
