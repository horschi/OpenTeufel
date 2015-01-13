
import org.openteufel.game.GameRunner;
import org.openteufel.ui.Renderer;
import org.openteufel.ui.renderer.gl.ClassicGLRenderer;

public class Main
{
    public static void main(final String[] args) throws Exception
    {
        final Renderer renderer = new ClassicGLRenderer();
        renderer.initGame();

        final GameRunner gamerunner = new GameRunner(renderer);
        gamerunner.runGame();
        
        renderer.close();
    }
}
