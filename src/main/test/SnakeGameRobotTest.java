import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import javax.swing.SwingUtilities;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;

public class SnakeGameRobotTest {

    private SnakeGame game;

    @Before
    public void setUp() throws Exception {
        // create the game directly via reflection so we have a reference for assertions
        Constructor<SnakeGame> ctor = SnakeGame.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        game = ctor.newInstance();

        // Run the game loop in a new thread to avoid freezing
        Method startGame = SnakeGame.class.getDeclaredMethod("startGame");
        startGame.setAccessible(true);
        Thread gameThread = new Thread(() -> {
            try { startGame.invoke(game); } catch (Exception ignored) {}
        });
        gameThread.setDaemon(true);
        gameThread.start();

        // wait for startGame() to finish initializing fields (snake, directions, isNewGame, ...)
        long deadline = System.currentTimeMillis() + 2000;
        while (!game.isNewGame() && System.currentTimeMillis() < deadline) {
            Thread.sleep(20);
        }
        assertTrue("game did not finish initializing", game.isNewGame());

        // start the game (dispatching the key directly so we don't depend on OS focus)
        press(KeyEvent.VK_ENTER);

        // wait until the snake actually exists on the board
        deadline = System.currentTimeMillis() + 2000;
        while (snakeHead() == null && System.currentTimeMillis() < deadline) {
            Thread.sleep(20);
        }
        assertNotNull("snake was not initialized after ENTER", snakeHead());
    }

    @After
    public void tearDown() {
        if (game != null) {
            game.dispose();
        }
    }

    private void press(int keyCode) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            long t = System.currentTimeMillis();
            game.dispatchEvent(new KeyEvent(game, KeyEvent.KEY_PRESSED, t, 0, keyCode, KeyEvent.CHAR_UNDEFINED));
            game.dispatchEvent(new KeyEvent(game, KeyEvent.KEY_RELEASED, t, 0, keyCode, KeyEvent.CHAR_UNDEFINED));
        });
    }

    @SuppressWarnings("unchecked")
    private Point snakeHead() throws Exception {
        Field f = SnakeGame.class.getDeclaredField("snake");
        f.setAccessible(true);
        LinkedList<Point> snake = (LinkedList<Point>) f.get(game);
        Point head = snake == null ? null : snake.peekFirst();
        return head == null ? null : new Point(head);
    }

    @Test
    public void testArrowRight() throws Exception {
        Point before = snakeHead();
        press(KeyEvent.VK_RIGHT);
        Thread.sleep(250);
        Point after = snakeHead();
        assertEquals(Direction.East, game.getDirection());
        assertTrue("snake should have moved right", after.x > before.x);
    }

    @Test
    public void testArrowDown() throws Exception {
        press(KeyEvent.VK_RIGHT);
        Thread.sleep(150);
        Point before = snakeHead();
        press(KeyEvent.VK_DOWN);
        Thread.sleep(250);
        Point after = snakeHead();
        assertEquals(Direction.South, game.getDirection());
        assertTrue("snake should have moved down", after.y > before.y);
    }

    @Test
    public void testArrowLeft() throws Exception {
        Point before = snakeHead();
        press(KeyEvent.VK_LEFT);
        Thread.sleep(250);
        Point after = snakeHead();
        assertEquals(Direction.West, game.getDirection());
        assertTrue("snake should have moved left", after.x < before.x);
    }

    @Test
    public void testArrowUp() throws Exception {
        press(KeyEvent.VK_RIGHT);
        Thread.sleep(150);
        press(KeyEvent.VK_UP);
        Thread.sleep(150);
        Point before = snakeHead();
        Thread.sleep(200);
        Point after = snakeHead();
        assertEquals(Direction.North, game.getDirection());
        assertTrue("snake should have moved up", after.y < before.y);
    }

    @Test
    public void testWasdRight() throws Exception {
        Point before = snakeHead();
        press(KeyEvent.VK_D);
        Thread.sleep(250);
        Point after = snakeHead();
        assertEquals(Direction.East, game.getDirection());
        assertTrue(after.x > before.x);
    }

    @Test
    public void testWasdDown() throws Exception {
        press(KeyEvent.VK_D);
        Thread.sleep(150);
        Point before = snakeHead();
        press(KeyEvent.VK_S);
        Thread.sleep(250);
        Point after = snakeHead();
        assertEquals(Direction.South, game.getDirection());
        assertTrue(after.y > before.y);
    }

    @Test
    public void testWasdLeft() throws Exception {
        Point before = snakeHead();
        press(KeyEvent.VK_A);
        Thread.sleep(250);
        Point after = snakeHead();
        assertEquals(Direction.West, game.getDirection());
        assertTrue(after.x < before.x);
    }

    @Test
    public void testWasdUp() throws Exception {
        press(KeyEvent.VK_D);
        Thread.sleep(150);
        press(KeyEvent.VK_W);
        Thread.sleep(150);
        Point before = snakeHead();
        Thread.sleep(200);
        Point after = snakeHead();
        assertEquals(Direction.North, game.getDirection());
        assertTrue(after.y < before.y);
    }

    @Test
    public void testPauseUnpause() throws Exception {
        press(KeyEvent.VK_P);
        Thread.sleep(100);
        assertTrue(game.isPaused());

        Point during = snakeHead();
        Thread.sleep(300);
        assertEquals("snake should not move while paused", during, snakeHead());

        press(KeyEvent.VK_P);
        Thread.sleep(100);
        assertFalse(game.isPaused());
    }

    @Test
    public void testRestart() throws Exception {
        // ENTER only resets when the game is over so need to force game over
        Field gameOver = SnakeGame.class.getDeclaredField("isGameOver");
        gameOver.setAccessible(true);
        gameOver.setBoolean(game, true);
        press(KeyEvent.VK_ENTER);

        Point head = snakeHead();
        assertFalse("game should be reset", game.isGameOver());
        assertEquals("score should reset to 0", 0, game.getScore());
        assertEquals(BoardPanel.COL_COUNT / 2, head.x);
        assertEquals(BoardPanel.ROW_COUNT / 2, head.y);
    }

    @Test
    public void testGameOverOnWallHit() throws Exception {
        Thread.sleep(2000);
        assertTrue("snake should have crashed into the wall", game.isGameOver());
        assertFalse(game.isNewGame());
    }

    @Test
    public void testEatFruit() throws Exception {
        // snake starts at center heading North after setUp()
        Point head = snakeHead();

        // place a fruit directly in the snake's path
        Field boardField = SnakeGame.class.getDeclaredField("board");
        boardField.setAccessible(true);
        BoardPanel board = (BoardPanel) boardField.get(game);
        board.setTile(head.x, head.y - 1, TileType.Fruit);

        // wait for the snake to move onto it
        Thread.sleep(300);

        // check that the game registered the fruit
        assertEquals("should have eaten 1 fruit", 1, game.getFruitsEaten());
        assertTrue("score should have increased", game.getScore() > 0);
    }
}
