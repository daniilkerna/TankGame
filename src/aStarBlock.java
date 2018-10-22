public class aStarBlock {

    public GridBlock gridPosition;
    public int fValue;

    public aStarBlock(GridBlock gridPosition, int fValue){
        this.gridPosition = new GridBlock(gridPosition);
        this.fValue = fValue;
    }

    public aStarBlock(int row, int column, int fValue){
        this.gridPosition = new GridBlock(row, column);
        this.fValue = fValue;
    }

    public static int compare(aStarBlock a1, aStarBlock a2){
        if (a1.fValue < a2.fValue){
            return -1;
        }
        else if (a1.fValue > a2.fValue){
            return 1;
        }
        else
            return 0;
    }
}
