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

    public static int getOppositeSide(int a) {
        switch (a) {
            case 0:
                return 5;
            case 5: return 0;
            case 2 : return 4;
            case 4:return 2;
            case 3 : return 1;
            case 1: return 3;
            default : return -1;
        }
    }


}
