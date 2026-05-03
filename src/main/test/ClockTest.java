    import org.junit.Test;
    import static org.junit.Assert.*;

    public class ClockTest {

        @Test
        public void testIsPaused() {
            Clock clock = new Clock(10f);
            assertFalse(clock.isPaused());

            clock.setPaused(true);
            assertTrue(clock.isPaused());

            clock.setPaused(false);
            assertFalse(clock.isPaused());
        }

        @Test
        public void testPeekElapsedCycle() throws Exception {
            // Set the clock to 1000 cycles per second
            Clock clock = new Clock(1000f);
            assertFalse("no cycles should have elapsed yet", clock.peekElapsedCycle());

            Thread.sleep(10);
            clock.update();

            assertTrue(clock.peekElapsedCycle());
            // peek shouldn't decrement, so calling it again should still be true
            assertTrue("peek should not decrement", clock.peekElapsedCycle());

            // use hasElapsedCycle to delete all the cycles, then peek should flip back to false
            while (clock.hasElapsedCycle()) {}
            assertFalse(clock.peekElapsedCycle());
        }
    }
