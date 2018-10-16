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
	int[][] gamePosition = new int [20][20];
	ArrayList <Brick> brickArrayList;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		container.setSoundOn(true);
		TankGame tg = (TankGame) game;
		playerTank = new Tank(tg.ScreenWidth/2 , tg.ScreenHeight/2);
		playerTank.setScale(.45f);

		//initialize gameMap
		for (int i = 0; i < 20; i++) {
			gamePosition[i][0] = 1;
			gamePosition[i][19] = 1;
			gamePosition[0][i] = 1;
			gamePosition[19][i] = 1;
		}
		for (int i = 2; i <= 17 ; i += 3 ) {
			for(int j = 2 ; j < 18; j++)
				gamePosition[j][i] = 1;
		}


		//initialize bricks
		brickArrayList = new ArrayList<Brick>(20);
		for (int i = 0; i < 20; i++){
			for ( int j = 0 ; j < 20; j++){
				if (gamePosition[i][j] == 1){
					brickArrayList.add(new Brick(j * 40 + 20, i * 40 + 20  ));
				}
			}
		}

		for (Brick b : brickArrayList)
			b.setScale(.5f);

	}
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		TankGame bg = (TankGame)game;

		playerTank.render(g);
		for (Brick b : brickArrayList)
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

	@Override
	public int getID() {
		return TankGame.PLAYINGSTATE;
	}
	
}