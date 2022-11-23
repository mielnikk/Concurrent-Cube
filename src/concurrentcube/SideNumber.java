package concurrentcube;

public enum SideNumber {
    TOP,
    LEFT,
    FRONT,
    RIGHT,
    BACK,
    BOTTOM;

    public static SideNumber getSideType(int sideNumber) {
        switch (sideNumber) {
            case 0:
                return TOP;
            case 1:
                return LEFT;
            case 2:
                return FRONT;
            case 3:
                return RIGHT;
            case 4:
                return BACK;
            case 5:
                return BOTTOM;
            default:
                throw new IllegalArgumentException();
        }
    }



}
