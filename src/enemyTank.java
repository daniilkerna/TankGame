import jig.Entity;
import jig.*;
import java.util.Random;

public class enemyTank extends Entity {
    public int numberOfBullets = 0;

    private float velocity = 1.50f;
    public final Vector[] directionShot = {new Vector(0,-3f),new Vector(-3f,0), new Vector(0,3f), new Vector(3f,0) };
    public int gridPositionRoW;
    public int gridPositionColumn;


    private int lives;

    private int directionFacing = 0;
    private int directionPicture = 0;
    private int countCooldown = 4000;
    private int bulletCooldown = 1000;


    public enemyTank (final float x, final float y){
        super(x , y);
        this.lives = 1;

        addImageWithBoundingBox(ResourceManager.getImage(TankGame.Enemy_Tank_Up_RSC));


    }


    public void moveTankLeft(){
        if (!checkDirectionFaced(1)) {
            addImage(ResourceManager.getImage(TankGame.Enemy_Tank_Left_RSC));
        }
        super.setX(getX() - velocity);
    }

    public void moveTankRight(){
        if (!checkDirectionFaced(3)) {
            addImage(ResourceManager.getImage(TankGame.Enemy_Tank_Right_RSC));
        }
        super.setX(getX() + velocity);
    }
    public void moveTankUp(){
        if (!checkDirectionFaced(0)) {
            addImage(ResourceManager.getImage(TankGame.Enemy_Tank_Up_RSC));
        }

        super.setY(getY() - velocity);
    }

    public void moveTankDown(){
        if (!checkDirectionFaced(2)) {
            addImage(ResourceManager.getImage(TankGame.Enemy_Tank_Down_RSC));
        }
        super.setY(getY() + velocity);
    }

    private boolean checkDirectionFaced(int facing){
        if (facing == directionPicture){
            return true;
        }
        else{
            this.removeAllImages();
            directionPicture = facing;
            return false;
        }
    }

    public void updateDirection(final int delta){
        countCooldown += delta;
        if (countCooldown > 2000 ) {
            Random random = new Random();
            this.directionFacing = random.nextInt(4);

            //System.out.println(getDirectionFacing());

            countCooldown = 0;
        }

    }

    public boolean updateBulletCooldown(final int delta){
        bulletCooldown += delta;
        if (bulletCooldown > 2000){
            bulletCooldown = 0;
            return true;
        }

        else return false;
    }



    public int getDirectionFacing(){
        return this.directionFacing;
    }

    public int getDirectionPicture(){
        return this.directionPicture;
    }

    public int getLives(){
        return this.lives;
    }

    public void decrementLives(){
        this.lives--;
    }
}
