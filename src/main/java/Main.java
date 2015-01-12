import org.openteufel.game.GameRunner;
import org.openteufel.ui.renderer.gl.ClassicGLRenderer;
import org.openteufel.ui.Renderer;
import org.openteufel.ui.Window;
import org.openteufel.ui.renderer.java2d.DefaultRenderer;

public class Main
{
    public static void main(final String[] args) throws Exception
    {
        final Window window = new Window();
        final Renderer renderer = new DefaultRenderer();
        // final Renderer renderer = new ClassicGLRenderer();
        renderer.initGame(window);

        final GameRunner gamerunner = new GameRunner(renderer);
        gamerunner.runGame();
    }
}
