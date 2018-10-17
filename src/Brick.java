import jig.Entity;
import jig.ResourceManager;

public class Brick extends Entity {
    private boolean isDestroyed = false;

    public Brick (final float x, final float y){
        super( x , y);

        addImageWithBoundingBox(ResourceManager.getImage(TankGame.Brick_RSC));
    }

    public boolean getIsDestroyed(){
        return this.isDestroyed;
    }

    public void setIsDestroyed(boolean value){
        this.isDestroyed = value;
    }
}
