import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.awt.*;
import java.awt.event.KeyEvent;

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
        Thread.sleep(500);

        // create robot
        Robot robot = new Robot();
        robot.setAutoDelay(100);

        // simulate keys
        // enter -> to start game
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        // right
        robot.keyPress(KeyEvent.VK_RIGHT);
        robot.keyRelease(KeyEvent.VK_RIGHT);
        // up
        robot.keyPress(KeyEvent.VK_UP);
        robot.keyRelease(KeyEvent.VK_UP);
        // pause
        robot.keyPress(KeyEvent.VK_P);   // pause
        robot.keyRelease(KeyEvent.VK_P);
        // unpause
        robot.keyPress(KeyEvent.VK_P);   // unpause
        robot.keyRelease(KeyEvent.VK_P);

        Thread.sleep(1000);
    }
}