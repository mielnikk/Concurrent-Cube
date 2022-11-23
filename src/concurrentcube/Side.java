package concurrentcube;

public class Side {

    private final int size;
    private final int sideNumber;
    private final int[][] squares;

    public Side(int size, int sideNumber) {
        this.size = size;
        this.sideNumber = sideNumber;

        squares = new int[size][];
        for (int i = 0; i < size; i++) {
            squares[i] = new int[size];
            for (int j = 0; j < size; j++)
                squares[i][j] = sideNumber;
        }
    }

    public static int getOppositeSide(int a) {
        switch (a) {
            case 0:
                return 5;
            case 5:
                return 0;
            case 2:
                return 4;
            case 4:
                return 2;
            case 3:
                return 1;
            case 1:
                return 3;
            default:
                return -1;
        }
    }

    public static int getLayer(int side, int layer, int sideSize) {
        switch (side) {
            case 0:
            case 1:
            case 4:
                return layer;
            case 2:
            case 3:
            case 5:
                return sideSize - layer - 1;
            default:
                return -1;
        }
    }

    public int getSize() {
        return size;
    }

    public void show(StringBuilder builder) {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                builder.append(squares[i][j]);
    }

    public void rotateClockwise() {
        for (int i = 0; i < size / 2; i++) {
            for (int j = i; j < size - i - 1; j++) {
                int acc = squares[i][j];
                squares[i][j] = squares[size - 1 - j][i];
                squares[size - 1 - j][i] = squares[size - 1 - i][size - 1 - j];
                squares[size - 1 - i][size - 1 - j] = squares[j][size - 1 - i];
                squares[j][size - 1 - i] = acc;
            }
        }
    }


    public void rotateCounterclockwise() {
        for (int i = 0; i < size / 2; i++) {
            for (int j = i; j < size - i - 1; j++) {
                int acc = squares[i][j];
                squares[i][j] = squares[j][size - 1 - i];
                squares[j][size - 1 - i] = squares[size - 1 - i][size - 1 - j];
                squares[size - 1 - i][size - 1 - j] = squares[size - 1 - j][i];
                squares[size - 1 - j][i] = acc;
            }
        }
    }

    public void swapColumns(int columnIndex, Side otherSide, boolean reverseIndex, boolean oppositeColumn) {
        int acc;
        for (int i = 0; i < size; i++) {
            int index = i;
            if (reverseIndex)
                index = size - 1 - i;

            int swappedColumnIndex = columnIndex;
            if (oppositeColumn)
                swappedColumnIndex = size - 1 - columnIndex;

            acc = squares[i][columnIndex];
            squares[i][columnIndex] = otherSide.squares[index][swappedColumnIndex];
            otherSide.squares[index][swappedColumnIndex] = acc;
        }
    }

    public void swapRows(int rowNumber, Side otherSide) {
        int acc;
        for (int i = 0; i < size; i++) {
            acc = squares[rowNumber][i];
            squares[rowNumber][i] = otherSide.squares[rowNumber][i];
            otherSide.squares[rowNumber][i] = acc;
        }
    }

    public void swapColumnRow(int columnIndex, int rowIndex, Side otherSide, boolean reverseIndex) {
        int acc;
        for (int i = 0; i < size; i++) {
            int index = i;
            if (reverseIndex)
                index = size - 1 - index;

            acc = squares[i][columnIndex];
            squares[i][columnIndex] = otherSide.squares[rowIndex][index];
            otherSide.squares[rowIndex][index] = acc;
        }
    }

    public void swapRowColumn(int rowIndex, int columnIndex, Side otherSide, boolean reverseIndex) {
        int acc;
        for (int i = 0; i < size; i++) {
            int index = i;
            if (reverseIndex)
                index = size - 1 - index;

            acc = squares[rowIndex][i];
            squares[rowIndex][i] = otherSide.squares[index][columnIndex];
            otherSide.squares[index][columnIndex] = acc;
        }
    }

    int getPlaneNumber() {
        switch (sideNumber) {
            case 0:
            case 5:
                return 0;
            case 1:
            case 3:
                return 1;
            case 2:
            case 4:
                return 2;
        }
        return -1;
    }

}
