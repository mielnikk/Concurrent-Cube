package concurrentcube;

import java.util.concurrent.Semaphore;
import java.util.function.BiConsumer;

public class Cube {

    private final BiConsumer<Integer, Integer> beforeRotation;
    private final BiConsumer<Integer, Integer> afterRotation;
    private final Runnable beforeShowing;
    private final Runnable afterShowing;


    private final int size;
    private final Side[] sides;
    private static final int SHOW_PLANE_TYPE = 3;

    private final Semaphore cubeAccess; //< mutex
    private final int[] workingProcesses; //< number of processes working on each plane
    private final int[] waitingProcesses; //< number of processes waiting to access each plane

    private final Semaphore[] planeAccess;
    private final Semaphore[] layerAccess;

    public Cube(int size,
                BiConsumer<Integer, Integer> beforeRotation,
                BiConsumer<Integer, Integer> afterRotation,
                Runnable beforeShowing,
                Runnable afterShowing
    ) {

        this.beforeRotation = beforeRotation;
        this.afterRotation = afterRotation;
        this.beforeShowing = beforeShowing;
        this.afterShowing = afterShowing;
        this.size = size;
        this.sides = new Side[6];
        for (int i = 0; i <= 5; i++) {
            sides[i] = new Side(size, i);
        }

        cubeAccess = new Semaphore(1, true);
        waitingProcesses = new int[4];
        workingProcesses = new int[4];
        planeAccess = new Semaphore[4];
        for (int i = 0; i < 4; i++) {
            planeAccess[i] = new Semaphore(0, true);
            workingProcesses[i] = 0;
            waitingProcesses[i] = 0;
        }
        layerAccess = new Semaphore[size];
        for (int i = 0; i < size; i++) {
            layerAccess[i] = new Semaphore(1, true);
        }
    }

    private void rotateCube(int side, int layer) {
        switch (side) {
            case 0:
                sides[1].swapRows(layer, sides[2]);
                sides[2].swapRows(layer, sides[3]);
                sides[3].swapRows(layer, sides[4]);
                break;
            case 5:
                sides[2].swapRows(size - 1 - layer, sides[1]);
                sides[1].swapRows(size - 1 - layer, sides[4]);
                sides[4].swapRows(size - 1 - layer, sides[3]);
                break;
            case 2:
                sides[0].swapRowColumn(size - 1 - layer, size - 1 - layer, sides[1], true);
                sides[1].swapColumnRow(size - 1 - layer, layer, sides[5], false);
                sides[5].swapRowColumn(layer, layer, sides[3], true);
                break;
            case 4:
                sides[0].swapRowColumn(layer, size - 1 - layer, sides[3], false);
                sides[3].swapColumnRow(size - 1 - layer, size - 1 - layer, sides[5], true);
                sides[5].swapRowColumn(size - 1 - layer, layer, sides[1], false);
                break;
            case 3:
                sides[0].swapColumns(size - 1 - layer, sides[2], false, false);
                sides[2].swapColumns(size - 1 - layer, sides[5], false, false);
                sides[5].swapColumns(size - 1 - layer, sides[4], true, true);
                break;
            case 1:
                sides[0].swapColumns(layer, sides[4], true, true);
                sides[4].swapColumns(size - 1 - layer, sides[5], true, true);
                sides[5].swapColumns(layer, sides[2], false, false);
                break;
        }

        if (layer == 0)
            sides[side].rotateClockwise();
        else if (layer == size - 1) {
            sides[Side.getOppositeSide(side)].rotateCounterclockwise();
        }
    }

    private boolean anyOtherPlanesWaiting(int plane) {
        return waitingProcesses[(plane + 1) % 4] > 0
                || waitingProcesses[(plane + 2) % 4] > 0
                || waitingProcesses[(plane + 3) % 4] > 0;
    }

    private boolean anyOtherPlanesWorking(int plane) {
        return workingProcesses[(plane + 1) % 4] > 0
                || workingProcesses[(plane + 2) % 4] > 0
                || workingProcesses[(plane + 3) % 4] > 0;
    }

    private void entryProcedure(int plane) throws InterruptedException {
        cubeAccess.acquire();
        if (anyOtherPlanesWaiting(plane) || anyOtherPlanesWorking(plane)) {
            waitingProcesses[plane]++;
            cubeAccess.release();
            planeAccess[plane].acquireUninterruptibly();
            waitingProcesses[plane]--;
        }

        workingProcesses[plane]++;

        if (waitingProcesses[plane] > 0)
            planeAccess[plane].release();
        else
            cubeAccess.release();
    }

    private void afterFoo(int plane) throws InterruptedException {
        cubeAccess.acquireUninterruptibly();
        workingProcesses[plane]--;

        if (workingProcesses[plane] == 0) {
            if (waitingProcesses[(plane + 1) % 4] > 0)
                planeAccess[(plane + 1) % 4].release();
            else if (waitingProcesses[(plane + 2) % 4] > 0)
                planeAccess[(plane + 2) % 4].release();
            else if (waitingProcesses[(plane + 3) % 4] > 0)
                planeAccess[(plane + 3) % 4].release();
            else if (waitingProcesses[(plane + 4) % 4] > 0)
                planeAccess[(plane + 4) % 4].release();
            else
                cubeAccess.release();
        }
        else
            cubeAccess.release();

        if (Thread.currentThread().isInterrupted())
            throw new InterruptedException();
    }


    public void rotate(int side, int layer) throws InterruptedException {
        int plane = sides[side].getPlaneNumber();
        int planeLayer = Side.getLayer(side, layer, size);

        entryProcedure(plane);

        try {
            if (!Thread.currentThread().isInterrupted()) {
                layerAccess[planeLayer].acquire();

                beforeRotation.accept(side, layer);
                rotateCube(side, layer);
                afterRotation.accept(side, layer);

                layerAccess[planeLayer].release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            afterFoo(plane);
        }
        if (Thread.currentThread().isInterrupted())
            throw new InterruptedException();
    }

    public String show() throws InterruptedException {
        entryProcedure(SHOW_PLANE_TYPE);

        String s = "";
        StringBuilder b = new StringBuilder();
        if (!Thread.currentThread().isInterrupted()) {
            beforeShowing.run();
            for (int i = 0; i < 6; i++) {
                sides[i].show(b);
            }
            s = b.toString();
            afterShowing.run();
        }


        afterFoo(SHOW_PLANE_TYPE);
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
        else
            return s;
    }
}
