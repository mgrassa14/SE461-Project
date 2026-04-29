import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.LinkedList;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SnakeGameRobotTest {

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    @Test
    public void snakeGameRobotTest() throws Exception {
        // launch game
        // put into new thread to avoid freezing since it has infinite while loop
        Thread gameThread = new Thread(() -> SnakeGame.main(new String[0]));
        gameThread.start();

        // give time for window to load
        Thread.sleep(1000);

        // create robot
        Robot robot = new Robot();
        robot.setAutoDelay(100);

        // Simulate arrow keys
        // enter -> to start game
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        Thread.sleep(1000);
        // right
        robot.keyPress(KeyEvent.VK_RIGHT);
        robot.keyRelease(KeyEvent.VK_RIGHT);
        Thread.sleep(1000);
        // down
        robot.keyPress(KeyEvent.VK_DOWN);
        robot.keyRelease(KeyEvent.VK_DOWN);
        // left
        robot.keyPress(KeyEvent.VK_LEFT);
        robot.keyRelease(KeyEvent.VK_LEFT);
        // up
        robot.keyPress(KeyEvent.VK_UP);
        robot.keyRelease(KeyEvent.VK_UP);
        Thread.sleep(1000);

        // Simulate wasd keys
        // right
        robot.keyPress(KeyEvent.VK_D);
        robot.keyRelease(KeyEvent.VK_D);
        Thread.sleep(1000);
        // down
        robot.keyPress(KeyEvent.VK_S);
        robot.keyRelease(KeyEvent.VK_S);
        // left
        robot.keyPress(KeyEvent.VK_A);
        robot.keyRelease(KeyEvent.VK_A);
        // up
        robot.keyPress(KeyEvent.VK_W);
        robot.keyRelease(KeyEvent.VK_W);
        Thread.sleep(1000);

        // pause
        robot.keyPress(KeyEvent.VK_P);   // pause
        robot.keyRelease(KeyEvent.VK_P);
        Thread.sleep(2000);
        // unpause
        robot.keyPress(KeyEvent.VK_P);   // unpause
        robot.keyRelease(KeyEvent.VK_P);
        Thread.sleep(3000);
        // restart
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        // pause
        robot.keyPress(KeyEvent.VK_P);
        robot.keyRelease(KeyEvent.VK_P);
        Thread.sleep(2000);
        // unpause
        robot.keyPress(KeyEvent.VK_P);
        robot.keyRelease(KeyEvent.VK_P);
        Thread.sleep(1000);
    }

    @Test
    public void snakeEatsAppleTest() throws Exception {
        // Launch game on a daemon thread so it doesn't block JVM shutdown.
        Thread gameThread = new Thread(() -> SnakeGame.main(new String[0]));
        gameThread.setDaemon(true);
        gameThread.start();

        // Wait for the JFrame to appear, then grab the running instance via
        // the public AWT Frame registry (no production-code change needed).
        SnakeGame game = waitForSnakeGame(3000);
        assertNotNull("SnakeGame window did not appear in time", game);

        // Press ENTER to start the game. resetGame() runs, spawns a random
        // fruit somewhere, and unpauses the logic clock.
        Robot robot = new Robot();
        robot.setAutoDelay(50);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

        // Give resetGame() time to finish before we touch the board.
        Thread.sleep(150);

        // Reflect the private board and snake fields off the SnakeGame.
        BoardPanel board = (BoardPanel) readPrivateField(game, "board");
        @SuppressWarnings("unchecked")
        LinkedList<Point> snake =
                (LinkedList<Point>) readPrivateField(game, "snake");

        // Plant a fruit three cells north of the current head. The snake
        // starts moving North, so it will walk straight into it. setTile is
        // already public on BoardPanel, so no reflection needed there.
        Point head = snake.peekFirst();
        int fruitX = head.x;
        int fruitY = head.y - 3;
        board.setTile(fruitX, fruitY, TileType.Fruit);

        // Poll fruitsEaten until the snake eats something or we time out.
        // Logic clock runs at 9 Hz (~111 ms/tick), so 3 cells ≈ 333 ms.
        // 2 seconds is plenty of margin.
        int initialEaten = (int) readPrivateField(game, "fruitsEaten");
        int eaten = initialEaten;
        long deadline = System.currentTimeMillis() + 2000;
        while (System.currentTimeMillis() < deadline) {
            eaten = (int) readPrivateField(game, "fruitsEaten");
            if (eaten > initialEaten) break;
            Thread.sleep(50);
        }

        assertTrue(
                "Snake did not eat the planted fruit (initial=" + initialEaten
                        + ", final=" + eaten + ")",
                eaten > initialEaten);
    }

    /**
     * Polls AWT's frame registry until a visible SnakeGame JFrame appears,
     * or returns null if the timeout elapses.
     */
    private static SnakeGame waitForSnakeGame(long timeoutMs)
            throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            for (Frame f : Frame.getFrames()) {
                if (f instanceof SnakeGame && f.isVisible()) {
                    return (SnakeGame) f;
                }
            }
            Thread.sleep(50);
        }
        return null;
    }

    /**
     * Reads a private field from the given object by name.
     */
    private static Object readPrivateField(Object target, String name)
            throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.get(target);
    }
}

// test wasd keys and any other keys