package org.openteufel.game;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;

import org.openteufel.file.GamedataLoader;
import org.openteufel.game.levels.LevelStateTown;
import org.openteufel.ui.KeyboardEvent;
import org.openteufel.ui.KeyboardHandler;
import org.openteufel.ui.renderer.gl.ClassicGLRenderer;
import org.openteufel.ui.Renderer;
import org.openteufel.ui.TextRenderer;

public class GameRunner implements KeyboardHandler
{
    private final GamedataLoader dataLoader;
    private final Renderer<?>    renderer;

    private boolean              runGame   = true;

    private static final int targetFps = 60;

    public GameRunner(final Renderer<?> renderer) throws IOException
    {
        this.dataLoader = new GamedataLoader(new File("."));
        this.renderer = renderer;
        this.renderer.setTargetFps(targetFps);
        renderer.registerKeyboardHandler(this);
    }

    public void runGame() throws Exception
    {
        final LevelState level = new LevelStateTown(this.dataLoader);
        final LevelRenderer levelrenderer = new LevelRenderer(this.dataLoader, level, this.renderer);
        final TextRenderer textrenderer = new TextRenderer(renderer, dataLoader);

        int gametime = 0;
        while (runGame)
        {
            gametime++;
            levelrenderer.applyUserInput();

            long processStart = System.nanoTime();
            level.runFrame(gametime);
            long processTime = (System.nanoTime() - processStart) / 1000000;

            long renderStart = System.nanoTime();
            this.renderer.startFrame();
            levelrenderer.renderFrame();
            long renderTime = (System.nanoTime() - renderStart) / 1000000;

            textrenderer.writeText(1, 1, "proc=" + processTime + "ms / render=" + renderTime + "ms / mem=" + (Runtime.getRuntime().totalMemory() >> 20) + "M", 24);
            textrenderer.writeText(2, 26, "pos=" + (level.getCameraX() / 32)+","+(level.getCameraY() / 32)+" / num=" + level.getEntityManager().getNumEntities()+" / t=" + gametime, 24);

            this.renderer.finishFrame();
        }
    }

    @Override
    public void handleKeyboardEvent(KeyboardEvent e)
    {
        switch (e.key)
        {
            case 1:
            case 27:
                runGame = false;
                break;
            default:
                break;
        }
    }

}
