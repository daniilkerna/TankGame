public class GridBlock {
    public int row;
    public int column;

    public GridBlock (final int row, final int column){
        this.row = row;
        this.column = column;
    }

    public GridBlock (GridBlock gridPosition){
        this.row = gridPosition.row;
        this.column = gridPosition.column;
    }

    static public boolean equal(GridBlock a1, GridBlock a2){
        return (a1.row == a2.row) && (a1.column == a2.column);
    }

}
