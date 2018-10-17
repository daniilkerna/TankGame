import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

import jig.Collision;
import jig.Vector;

import org.lwjgl.Sys;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


/**
 *
 */

class PlayingState extends BasicGameState {

	Tank playerTank;
	int[][] gamePosition = new int [15][15];
	ArrayList <Brick> brickArrayList;
	ArrayList <Bullet> bulletArrayList;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		container.setSoundOn(true);
		TankGame tg = (TankGame) game;
		playerTank = new Tank(tg.ScreenWidth/2, tg.ScreenHeight/2);
		playerTank.setScale(.45f);

		//initialize gameMap
		for (int i = 0; i < 15; i++) {
			gamePosition[i][0] = 1;
			gamePosition[i][14] = 1;
			gamePosition[0][i] = 1;
			gamePosition[14][i] = 1;
		}
		for (int i = 2; i <= 13 ; i += 2 ) {
			for(int j = 2 ; j < 13; j++)
				gamePosition[j][i] = 1;
		}


		//initialize bricks
		brickArrayList = new ArrayList<Brick>(20);
		for (int i = 0; i < 15; i++){
			for ( int j = 0 ; j < 15; j++){
				if (gamePosition[i][j] == 1){
					brickArrayList.add(new Brick(j * 40 + 20, i * 40 + 20  ));
				}
			}
		}

		for (Brick b : brickArrayList)
			b.setScale(.5f);

		bulletArrayList = new ArrayList<Bullet>(5);

	}
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		TankGame bg = (TankGame)game;

		playerTank.render(g);
		for (Brick b : brickArrayList)
			b.render(g);

		for (Bullet b : bulletArrayList)
			b.render(g);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {

		Input input = container.getInput();
		TankGame tg = (TankGame)game;

		boolean notTouchingWall = true;

		for (Brick b : brickArrayList){
			Collision temp = playerTank.collides(b);

			if (temp != null){	//if tank touching wall, adjust tank position so there is no penetration
				notTouchingWall = false;
				//System.out.println(temp.getMinPenetration());
				Vector penetration = temp.getMinPenetration();
				playerTank.setX(playerTank.getX() + penetration.getX());
				playerTank.setY(playerTank.getY() + penetration.getY());
			}
			else{
				//TO DO
			}

		}

		if (notTouchingWall){
			controlTank(container, tg);
		}

		shootTank(container , tg);
		//move the bullets
		for (Bullet b : bulletArrayList) {
			b.update(delta);
			b.updateOnScreenStatus(tg.ScreenWidth, tg.ScreenHeight);
		}

		//clear bullets
		clearOffScreenBullets();
		clearDestroyedBricks();

		//check for collisions of bullets and bricks
		checkBulletsAndBricks();
	}

	//control paddle
	public void controlTank(GameContainer container , TankGame tg){

		Input input = container.getInput();
		if (input.isKeyDown(Input.KEY_RIGHT)) {
			if (playerTank.getCoarseGrainedMaxX() < tg.ScreenWidth) {
				playerTank.moveTankRight();
			}
		}
		else if (input.isKeyDown(Input.KEY_LEFT)) {
			if (playerTank.getCoarseGrainedMinX() > 0) {
				playerTank.moveTankLeft();
			}
		}
		else if (input.isKeyDown(Input.KEY_UP)) {
			if (playerTank.getCoarseGrainedMinY() > 0) {
				playerTank.moveTankUp();
			}
		}
		else if (input.isKeyDown(Input.KEY_DOWN)) {
			if (playerTank.getCoarseGrainedMaxY() < tg.ScreenHeight) {
				playerTank.moveTankDown();
			}
		}

		else {
			//do nothing
		}

	}

	public void shootTank(GameContainer container, TankGame tg){
		if (playerTank.numberOfBullets >= 2){
			return;
		}

		Input input = container.getInput();
		int direction = playerTank.getDirectionFacing();

		if (input.isKeyPressed(Input.KEY_SPACE)) {
			bulletArrayList.add(new Bullet(playerTank.getX() , playerTank.getY(), direction));
			for (Bullet b : bulletArrayList)
				b.setScale(.7f);

			playerTank.numberOfBullets++;
		}

	}

	public void clearOffScreenBullets(){
		Iterator itr = bulletArrayList.iterator();
		while (itr.hasNext())
		{
			Bullet x = (Bullet) itr.next();
			if (!x.getOnScreen()) {
				itr.remove();
				playerTank.numberOfBullets--;
				System.out.println("Bullet removed");
			}
		}
	}

	public void clearDestroyedBricks(){
		Iterator itr = brickArrayList.iterator();
		while (itr.hasNext())
		{
			Brick x = (Brick) itr.next();
			if (x.getIsDestroyed()) {
				itr.remove();
			}
		}
	}

	public void checkBulletsAndBricks(){
		for (Bullet bullet : bulletArrayList){
			for (Brick brick : brickArrayList) {
				if ( bullet.collides(brick) != null){
					bullet.setOnScreen(false);
					brick.setIsDestroyed(true);
				}
			}
		}
	}

	@Override
	public int getID() {
		return TankGame.PLAYINGSTATE;
	}
	
}