import org.openteufel.game.GameRunner;
import org.openteufel.ui.DefaultRenderer;
import org.openteufel.ui.Renderer;
import org.openteufel.ui.Window;

public class Main
{
    public static void main(final String[] args) throws Exception
    {
        final Window window = new Window();
        final Renderer renderer = new DefaultRenderer();
        renderer.initGame(window);

        final GameRunner gamerunner = new GameRunner(renderer);
        gamerunner.runGame();
    }
}
