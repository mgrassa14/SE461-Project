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
}