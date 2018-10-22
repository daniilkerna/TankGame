import jig.Entity;
import jig.ResourceManager;
import org.newdawn.slick.Graphics;


public class Base extends Entity {
    private boolean isDestroyed = false;

    public int gridPositionRoW;
    public int gridPositionColumn;

    public Base (final float x, final float y){
        super( x , y);

        addImageWithBoundingBox(ResourceManager.getImage(TankGame.Base_RSC));
        scale(.5f);
    }


    public boolean getIsDestroyed(){
        return this.isDestroyed;
    }

    public void setIsDestroyed(boolean value){
        this.isDestroyed = value;
    }
}
