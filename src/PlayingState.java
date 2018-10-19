import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

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

	int printCooldown = 0;

	public int enemyTanksRemaining = 20;
	Tank playerTank;
	Collision temp;
	int[][] gamePosition = new int [16][16];
	ArrayList <Brick> brickArrayList;
	ArrayList <Bullet> bulletArrayList;
	ArrayList <Bullet> enemyBulletArrayList;
	ArrayList <enemyTank> enemyTankArrayList;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		container.setSoundOn(true);
		TankGame tg = (TankGame) game;
		playerTank = new Tank(tg.ScreenWidth / 2, tg.ScreenHeight/2);
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
		enemyBulletArrayList = new ArrayList<Bullet>(5);

		enemyTankArrayList = new ArrayList<enemyTank>(1);
		for (int i = 0; i < 5 ; i++) {
			enemyTankArrayList.add(new enemyTank((i * 100) + 40, 50));
			enemyTanksRemaining--;
		}

		for (enemyTank b : enemyTankArrayList)
			b.setScale(.45f);

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

		for (enemyTank b : enemyTankArrayList)
			b.render(g);

		for (Bullet b : enemyBulletArrayList)
			b.render(g);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {

		Input input = container.getInput();
		TankGame tg = (TankGame)game;

		boolean notTouchingWall = true;

		calculateGridPosition(tg, delta);

		for (Brick b : brickArrayList){
			temp = playerTank.collides(b);

			//check for wall collision of the player
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

		for(Bullet b : enemyBulletArrayList){
			b.update(delta);
			b.updateOnScreenStatus(tg.ScreenWidth, tg.ScreenHeight);
		}

		//clear bullets
		clearOffScreenBullets(bulletArrayList);
		clearOffScreenBullets(enemyBulletArrayList);
		clearDestroyedBricks();

		//check for collisions of bullets and bricks
		checkBulletsAndBricks(bulletArrayList);
		checkBulletsAndBricks(enemyBulletArrayList);


		//update enemy tanks
		//checkCollisionEnemyTankVsEnemyTank(enemyTankArrayList);

		//check for wall collision of the enemies
		for (enemyTank enemy : enemyTankArrayList){
			boolean canMove = true;
			for (Brick b : brickArrayList) {
				temp = enemy.collides(b);

				if (temp != null) {    //if tank touching wall, adjust tank position so there is no penetration
					Vector penetration = temp.getMinPenetration();
					enemy.setX(enemy.getX() + penetration.getX());
					enemy.setY(enemy.getY() + penetration.getY());

					canMove = false;
				}
			}
			if (canMove ){
				controlEnemyTank(enemy , tg);
			}
		}



		for (enemyTank tank : enemyTankArrayList){
			tank.updateDirection(delta);
			if(tank.updateBulletCooldown(delta))
				shootEnemyTank(tank);
		}

		//check for enemy bullets hitting the player
		checkEnemyBulletsAndPlayer(enemyBulletArrayList);
		checkEnemyTanksAndPlayerBullet(enemyTankArrayList);

		//clear destroyed tanks
		clearDestroyedTanks(enemyTankArrayList);

		//create more enemy tanks
		spawnEnemyTanks();

		calculateGridPosition(tg , delta);


		//game over check
		if (playerTank.getLives() == 0 || enemyTankArrayList.size() == 0) {
			if (enemyTankArrayList.size() == 0){
				tg.victory = true;
			}
			game.enterState(TankGame.GAMEOVERSTATE);
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

	//control enemy tank
	public void controlEnemyTank(enemyTank tank, TankGame tg){

		int direction = tank.getDirectionFacing();
		int row = tank.gridPositionRoW;
		int col = tank.gridPositionColumn;

		if (direction == 0){ 	//go up
			if (tank.getCoarseGrainedMinY() > 0) {
				if (row == 0 || gamePosition[row-1][col] != 2)
					tank.moveTankUp();
			}
		}
		else if (direction == 1){	//go left
			if (tank.getCoarseGrainedMinX() > 0) {
				if (col == 0 || gamePosition[row][col-1] != 2)
					tank.moveTankLeft();
			}
		}

		else if (direction == 2){	// go down
			if (tank.getCoarseGrainedMaxY() < tg.ScreenHeight) {
				if (row == 14 || gamePosition[row+1][col] != 2)
				tank.moveTankDown();
			}

		}

		else if (direction == 3){	// go right
			if (tank.getCoarseGrainedMaxX() < tg.ScreenWidth) {
				if ( col == 14 || gamePosition[row][col + 1] != 2)
				tank.moveTankRight();
			}

		}

		else{
			//to do
		}

	}

	//produce a new bullet upon hitting the space bar
	public void shootTank(GameContainer container, TankGame tg){
		if (bulletArrayList.size() >= 2){
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

	//shoot enemy tank
	public void shootEnemyTank(enemyTank enemy){
		int direction = enemy.getDirectionPicture();

		enemyBulletArrayList.add(new Bullet(enemy.getX() , enemy.getY(), direction));

		for (Bullet b : enemyBulletArrayList)
			b.setScale(.7f);

		enemy.numberOfBullets++;
	}

	//clear offscreen bullets
	public void clearOffScreenBullets(ArrayList list){
		Iterator itr = list.iterator();
		while (itr.hasNext())
		{
			Bullet x = (Bullet) itr.next();
			if (!x.getOnScreen()) {
				itr.remove();
				//System.out.println("Bullet removed");
			}
		}
	}

	//clear destroyed bricks
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

	//check for collisions of bullets and bricks
	public void checkBulletsAndBricks(ArrayList <Bullet> list){
		for (Bullet bullet : list){
			for (Brick brick : brickArrayList) {
				if ( bullet.collides(brick) != null){
					bullet.setOnScreen(false);
					brick.setIsDestroyed(true);
				}
			}
		}
	}

	public void checkEnemyBulletsAndPlayer(ArrayList <Bullet> list){
		for (Bullet bullet : list){
			if ( bullet.collides(playerTank) != null){
				bullet.setOnScreen(false);
				playerTank.decrementLives();
			}
		}
	}

	public void checkEnemyTanksAndPlayerBullet(ArrayList <enemyTank> list ){
		for (enemyTank tank : list){
			for(Bullet bullet : bulletArrayList){
				if ( bullet.collides(tank) != null){
					bullet.setOnScreen(false);
					tank.decrementLives();
				}
			}
		}
	}

	public void clearDestroyedTanks(ArrayList <enemyTank> list){
		Iterator itr = list.iterator();
		while (itr.hasNext())
		{
			enemyTank x = (enemyTank) itr.next();
			if (x.getLives() == 0) {
				itr.remove();
			}
		}

	}



	@Override
	public int getID() {
		return TankGame.PLAYINGSTATE;
	}

	//return random int [0,maximum]
	public int getRandomInt(int maximum){
		Random random = new Random();
		int number = random.nextInt(maximum);

		return number;
	}

	public void spawnEnemyTanks(){
		if (enemyTanksRemaining == 0 || enemyTankArrayList.size() >= 5){
			return;
		}
		else{
			int location = getRandomInt(3);
			enemyTankArrayList.add(new enemyTank((location * 260) + 40, 50));
			enemyTanksRemaining--;
			for (enemyTank b : enemyTankArrayList)
				b.setScale(.45f);
		}
	}

	public void calculateGridPosition(TankGame tg , final int delta){

		for (int i = 0; i < 15; i++){
			for(int j = 0; j < 15; j++){
				gamePosition[i][j] = 0;
			}
		}

		int col = (int)(playerTank.getX() / (tg.ScreenWidth / 15f)); 		//columns
		int row = (int) (playerTank.getY() / (tg.ScreenHeight / 15));		//rows

		gamePosition[row][col] = 2;

		for (enemyTank tank : enemyTankArrayList){
			col = (int)(tank.getX() / (tg.ScreenWidth / 15f)); 		//columns
			row = (int) (tank.getY() / (tg.ScreenHeight / 15));		//rows

			gamePosition[row][col] = 2;
			tank.gridPositionRoW = row;
			tank.gridPositionColumn = col;
		}

	}
	
}