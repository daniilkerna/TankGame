import jig.Entity;
import jig.ResourceManager;
import jig.Vector;

public class Bullet extends Entity {
    final Vector[] directionShot = {new Vector(0,-.25f),new Vector(-.25f,0), new Vector(0,.25f), new Vector(.25f,0) };
    Vector velocity;
    private boolean onScreen;

    public Bullet (final float x, final float y , final int direction){
        super( x , y);

        this.velocity = directionShot[direction];
        this.onScreen = true;

        addImageWithBoundingBox(ResourceManager.getImage(TankGame.Bullet_RSC));
    }



    /**
     * Update the Bullet based on how much time has passed...
     * @param delta
     *            the number of milliseconds since the last update
     */
    public void update(final int delta) {
        translate(velocity.scale(delta));

    }

    public void updateOnScreenStatus(final int width, final int height){
        if ( this.getX() < 0 || this.getY() < 0 || this.getX() > width || this.getY() > height){
            setOnScreen(false);
        }
    }

    public boolean getOnScreen(){
        return this.onScreen;
    }

    public void setOnScreen(boolean value){
        this.onScreen = value;
    }

}
