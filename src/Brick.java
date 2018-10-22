import jig.Entity;
import jig.ResourceManager;

public class Brick extends Entity {
    private boolean isDestroyed = false;

    public GridBlock gridPosition;

    public Brick (final float x, final float y, int row, int column ){
        super( x , y);

        gridPosition = new GridBlock(row ,column);


        addImageWithBoundingBox(ResourceManager.getImage(TankGame.Brick_RSC));
    }

    public boolean getIsDestroyed(){
        return this.isDestroyed;
    }

    public void setIsDestroyed(boolean value){
        this.isDestroyed = value;
    }
}
