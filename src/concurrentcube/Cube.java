package concurrentcube;

import java.util.function.BiConsumer;

public class Cube {

    private final int size;
    private final Side[] sides;

    public Cube(int size,
                BiConsumer<Integer, Integer> beforeRotation,
                BiConsumer<Integer, Integer> afterRotation,
                Runnable beforeShowing,
                Runnable afterShowing
    ) {
        this.size = size;
        this.sides = new Side[6];
        for (int i = 0; i <= 5; i++) {
            sides[i] = new Side(size, i);
        }
    }

    public void rotate(int side, int layer) throws InterruptedException {

        switch (side) {
            case 0:
                sides[1].rotateRow(layer, sides[2].getSquares());
                sides[2].rotateRow(layer, sides[3].getSquares());
                sides[3].rotateRow(layer, sides[4].getSquares());
                break;
            case 5:
                sides[2].rotateRow(size - 1 - layer, sides[1].getSquares());
                sides[1].rotateRow(size - 1 - layer, sides[4].getSquares());
                sides[4].rotateRow(size - 1 - layer, sides[3].getSquares());
                break;
            case 2:
                sides[0].swapRowColumn(size - 1 - layer, size - 1 - layer, sides[1].getSquares(), true);
                sides[1].swapColumnRow(size - 1 - layer, layer, sides[5].getSquares(), false);
                sides[5].swapRowColumn(layer, layer, sides[3].getSquares(), true);
                break;
            case 4:
                sides[0].swapRowColumn(layer, size - 1 - layer, sides[3].getSquares(), false);
                sides[3].swapColumnRow(size - 1 - layer, size - 1 - layer, sides[5].getSquares(), true);
                sides[5].swapRowColumn(size - 1 - layer, layer, sides[1].getSquares(), false);
                break;
            case 3:
                sides[0].rotateColumn(size - 1 - layer, sides[2].getSquares(), false, false);
                sides[2].rotateColumn(size - 1 - layer, sides[5].getSquares(), false, false);
                sides[5].rotateColumn(size - 1 - layer, sides[4].getSquares(), true, true);
                break;
            case 1:
                sides[0].rotateColumn(layer, sides[4].getSquares(), true, true);
                sides[4].rotateColumn(size - 1 - layer, sides[5].getSquares(), true, true);
                sides[5].rotateColumn(layer, sides[2].getSquares(), false, false);
                break;
            default:
                throw new InterruptedException();
        }

        if (layer == 0)
            sides[side].rotateClockwise();
        else if (layer == size - 1) {
            sides[SideNumber.getOppositeSide(side)].rotateCounterclockwise();
        }
        else if (layer >= size || layer < 0){
            // wywala exception
        }
    }

    public String show() throws InterruptedException {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sides[i].show(b);
        }

        String s = b.toString();

//        for (int i = 0; i < 6; i++) {
//            for (int j = 0; j < size; j++) {
//                for (int k = 0; k < size; k++)
//                    System.out.print(s.charAt(i * size * size + j * size + k));
//                System.out.println(" ");
//            }
//
//        }
        return s;
    }


}
