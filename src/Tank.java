import jig.Entity;
import jig.*;

public class Tank extends Entity {
    public int numberOfBullets = 0;

    private int lives;
    private float velocity = 2.5f;
    private int directionFacing = 0;

    public Tank (final float x, final float y){
        super(x , y);
        this.lives = 5;

        addImageWithBoundingBox(ResourceManager.getImage(TankGame.Tank_Up_RSC));


    }


    public void moveTankLeft(){
        if (!checkDirectionFaced(1)) {
            addImage(ResourceManager.getImage(TankGame.Tank_Left_RSC));
        }
        super.setX(getX() - velocity);
    }

    public void moveTankRight(){
        if (!checkDirectionFaced(3)) {
            addImage(ResourceManager.getImage(TankGame.Tank_Right_RSC));
        }
        super.setX(getX() + velocity);
    }
    public void moveTankUp(){
        if (!checkDirectionFaced(0)) {
            addImage(ResourceManager.getImage(TankGame.Tank_Up_RSC));
        }

        super.setY(getY() - velocity);
    }

    public void moveTankDown(){
        if (!checkDirectionFaced(2)) {
            addImage(ResourceManager.getImage(TankGame.Tank_Down_RSC));
        }
        super.setY(getY() + velocity);
    }

    private boolean checkDirectionFaced(int facing){
        if (facing == directionFacing){
            return true;
        }
        else{
            this.removeAllImages();
            directionFacing = facing;
            return false;
        }
    }

    public int getDirectionFacing(){
        return this.directionFacing;
    }
}
