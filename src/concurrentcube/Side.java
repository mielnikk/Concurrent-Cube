package concurrentcube;

public class Side {

    private final int size;
    private final SideNumber sideNumber;
    private final int[][] squares;

    public Side(int size, int sideNumber) {
        this.size = size;
        this.sideNumber = SideNumber.getSideType(sideNumber);

        squares = new int[size][];
        for (int i = 0; i < size; i++) {
            squares[i] = new int[size];
            for (int j = 0; j < size; j++)
                squares[i][j] = sideNumber;
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

        for (int i = 0; i <= size / 2; i++) {
           int endIdx = size - i - 1;

            for (int j = i; j < endIdx; j++) {
                int acc = squares[j][i];
                squares[j][i] = squares[endIdx][j];

                squares[endIdx][j] = squares[endIdx - j][endIdx];
                squares[endIdx - j][endIdx] = squares[i][endIdx - j];
                squares[i][endIdx - j] = acc;
            }

        }
    }

    public void rotateCounterclockwise() {
        for (int i = 0; i <= size / 2; i++) {
            int endIdx = size - i - 1;

            for (int j = i; j < endIdx ; j++) {
                int acc = squares[i][j];

                squares[i][j] = squares[j][endIdx];
                squares[j][endIdx] = squares[endIdx][endIdx - j];
                squares[endIdx][endIdx - j] = squares[endIdx - j][i];
                squares[endIdx - j][i] = acc;
            }

        }
    }

    public void rotateColumn(int columnIndex, int[][] swapSide, boolean reverseIndex, boolean oppositeColumn) {
        int acc;
        for (int i = 0; i < size; i++) {
            int index = i;
            if (reverseIndex)
                index = size - 1 - i;

            int swappedColumnIndex = columnIndex;
            if (oppositeColumn)
                swappedColumnIndex = size - 1 - columnIndex;

            acc = squares[i][columnIndex];
            squares[i][columnIndex] = swapSide[index][swappedColumnIndex];
            swapSide[index][swappedColumnIndex] = acc;
        }
    }

    public void rotateRow(int rowNumber, int[][] swapSide) {
        int acc;
        for (int i = 0; i < size; i++) {
            acc = squares[rowNumber][i];
            squares[rowNumber][i] = swapSide[rowNumber][i];
            swapSide[rowNumber][i] = acc;
        }
    }

    public void swapColumnRow(int columnIndex, int rowIndex, int[][] swapSide, boolean reverseIndex) {
        int acc;
        for (int i = 0; i < size; i++) {
            int index = i;
            if (reverseIndex)
                index = size - 1 - i;

            acc = squares[i][columnIndex];
            squares[i][columnIndex] = swapSide[rowIndex][index];
            swapSide[rowIndex][index] = acc;
        }
    }

    public void swapRowColumn(int rowIndex, int columnIndex, int[][] swapSide, boolean reverseIndex) {
        int acc;
        for (int i = 0; i < size; i++) {
            int index = i;
            if (reverseIndex)
                index = size - 1 - index;

            acc = squares[rowIndex][i];
            squares[rowIndex][i] = swapSide[index][columnIndex];
            swapSide[index][columnIndex] = acc;
        }
    }

    int[][] getSquares() {
        return squares;
    }
}
