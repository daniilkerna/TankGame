import jig.Entity;
import jig.ResourceManager;

public class Bullet extends Entity {

    public Bullet (final float x, final float y){
        super( x , y);

        addImageWithBoundingBox(ResourceManager.getImage(TankGame.Bullet_RSC));
    }
}
