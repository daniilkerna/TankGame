import jig.Entity;
import jig.*;

public class Tank extends Entity {
    public int numberOfBullets = 0;

    private int lives;
    private float velocity = 1.75f;
    private int directionFacing = 0;
    public GridBlock gridPosition = new GridBlock(0 ,0 );

    public Tank (final float x, final float y){
        super(x , y);
        this.lives = 3;

        addImageWithBoundingBox(ResourceManager.getImage(TankGame.Tank_Up_RSC));
        scale(.45f);


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

    public int getLives(){
        return this.lives;
    }

    public void decrementLives(){
        this.lives--;
    }




}


