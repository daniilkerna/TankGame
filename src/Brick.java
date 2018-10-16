import jig.Entity;
import jig.ResourceManager;

public class Brick extends Entity {

    public Brick (final float x, final float y){
        super( x , y);

        addImageWithBoundingBox(ResourceManager.getImage(TankGame.Brick_RSC));
    }
}
