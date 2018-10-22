import jig.Entity;
import jig.ResourceManager;

public class Stone extends Brick {
    private boolean isDestroyed = false;

    public GridBlock gridPosition;

    public Stone (final float x, final float y, int row, int column ){
        super( x, y, row, column);

        gridPosition = new GridBlock(row ,column);


        addImageWithBoundingBox(ResourceManager.getImage(TankGame.Stone_RSC));
    }

    public boolean getIsDestroyed(){
        return false;
    }   //can't be destroyed

    public void setIsDestroyed(boolean value){
        this.isDestroyed = value;
    }
}