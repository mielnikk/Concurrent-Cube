package concurrentcube;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class CubeTest {

    private static final BiConsumer<Integer, Integer> emptyBiConsumer = (a, b) -> {};

    private String getInitialCubeState(int size) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < size; j++) {
                builder.append(i);
            }
        }
        return builder.toString();
    }

    private class rotateRunnable implements Runnable {

        private final Cube cube;
        private final int side;
        private final int layer;

        public rotateRunnable(Cube cube, int side, int layer) {
            this.cube = cube;
            this.side = side;
            this.layer = layer;
        }

        @Override
        public void run() {
            try {
                cube.rotate(side, layer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class showRunnable implements Runnable {
        private final Cube c;
        private final int repetitions;

        public showRunnable(Cube c, int repetitions) {
            this.c = c;
            this.repetitions = repetitions;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < repetitions; i++)
                    c.show();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    void counterRunnableTest() {
        var runCounter1 = new Object() {
            final AtomicInteger value = new AtomicInteger(0);
        };
        var runCounter2 = new Object() {
            final AtomicInteger value = new AtomicInteger(0);
        };

        Cube cube = new Cube(4, emptyBiConsumer, emptyBiConsumer,
                runCounter1.value::getAndIncrement,
                runCounter2.value::getAndIncrement);

        ExecutorService executorService = Executors.newFixedThreadPool(12);

        int expectedResult = 0;
        for (int i = 1; i < 100; i++) {
            executorService.submit(new showRunnable(cube, i));
            expectedResult += i;
        }
        executorService.shutdown();
        Assertions.assertDoesNotThrow(() -> {executorService.awaitTermination(1, TimeUnit.SECONDS);});
        Assertions.assertTrue(runCounter1.value.get() == expectedResult &&
                runCounter2.value.get() == expectedResult);
    }


    @Test
    @DisplayName("Parallel rotations of different layers on one side")
    void concurrentLayerRotations() {
        BiConsumer<Integer, Integer> rotationSleeper = (a, b) -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Cube cube = new Cube(4, rotationSleeper, emptyBiConsumer, () -> {}, () -> {});
        Thread[] threads = new Thread[4];

        for (int i = 0; i < 4; i++)
            threads[i] = new Thread(new rotateRunnable(cube, 0, i));

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 4; i++)
            threads[i].start();

        Assertions.assertDoesNotThrow(() -> {
            for (int i = 0; i < 4; i++)
                threads[i].join();
        });
        long endTime = System.currentTimeMillis();
        Assertions.assertTrue(endTime - startTime < 2 * 300);
    }


    @Test
    @DisplayName("Parallel multiple show() calls")
    void concurrentShows() {
        Runnable showSleeper = () -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Cube cube = new Cube(4, emptyBiConsumer, emptyBiConsumer, showSleeper, () -> {});
        Thread[] threads = new Thread[4];

        for (int i = 0; i < 4; i++)
            threads[i] = new Thread(new showRunnable(cube, 1));

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 4; i++)
            threads[i].start();

        Assertions.assertDoesNotThrow(() -> {
            for (int i = 0; i < 4; i++)
                threads[i].join();
        });
        long endTime = System.currentTimeMillis();
        Assertions.assertTrue(endTime - startTime < 2 * 300);
    }


}
