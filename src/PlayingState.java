import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Random;

import jig.Collision;
import jig.Entity;
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
	Base base;

	int[][] mapPosition = { {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,},
							{1,0,2,0,0,0,0,0,0,0,0,0,0,0,1,},
							{1,0,1,0,1,0,1,1,1,0,1,0,1,0,1,},
							{1,0,1,0,1,0,1,1,1,0,1,0,1,0,1,},
							{1,0,1,0,1,0,1,1,1,0,1,0,1,0,1,},
							{1,0,1,0,1,0,1,1,1,0,1,0,1,0,1,},
							{1,0,1,0,1,0,1,1,1,0,1,0,1,0,1,},
							{1,0,1,0,1,0,1,1,1,0,1,0,1,0,1,},
							{1,0,1,0,1,0,1,1,1,0,1,0,1,0,1,},
							{1,0,1,0,1,0,1,1,1,0,1,0,1,0,1,},
							{1,0,1,0,1,0,1,1,1,0,1,0,1,0,1,},
							{1,0,1,0,1,0,1,1,1,0,1,0,1,0,1,},
							{1,0,1,0,1,0,1,1,1,0,1,0,1,0,1,},
							{1,0,0,0,0,0,0,11,0,0,0,0,0,0,1,},
							{1,1,1,1,1,1,1,3,1,1,1,1,1,1,1,},
						};
	ArrayList <Entity> brickArrayList;
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
		playerTank = new Tank(tg.ScreenWidth / 2 - 40, tg.ScreenHeight - 60);

        enemyTankArrayList = new ArrayList<enemyTank>(1);
        bulletArrayList = new ArrayList<Bullet>(5);
        enemyBulletArrayList = new ArrayList<Bullet>(5);


		//initialize the map
		brickArrayList = new ArrayList <Entity>(20);
		for (int i = 0; i < 15; i++){
			for ( int j = 0 ; j < 15; j++){
				if (mapPosition[i][j] == 1){
					brickArrayList.add(new Brick(j * 40 + 20, i * 40 + 20, i , j  ));
				}
				else if (mapPosition[i][j] == 11){
					brickArrayList.add(new Stone(j * 40 + 20, i * 40 + 20, i , j  ));
				}
				else if (mapPosition[i][j] == 3){
					base = new Base(j * 40 + 20, i * 40 + 20  );
				}
                else if (mapPosition[i][j] == 2){
                    enemyTankArrayList.add(new enemyTank(j * 40 + 20, i * 40 + 20));
                    enemyTanksRemaining--;
                }
			}
		}

		for (Entity b : brickArrayList)
			b.setScale(.5f);

		//update enemy tanks
		calculateGridPosition(tg);

		for(enemyTank enemy : enemyTankArrayList){
			calculatePathTowardsBase(enemy);
		}




	}
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		TankGame tg = (TankGame)game;

		playerTank.render(g);
		for (Entity b : brickArrayList)
			b.render(g);

		for (Bullet b : bulletArrayList)
			b.render(g);

		for (enemyTank b : enemyTankArrayList)
			b.render(g);

		for (Bullet b : enemyBulletArrayList)
			b.render(g);

		if(!base.getIsDestroyed())
			base.render(g);

		g.drawString("Lives : " + playerTank.getLives() , 0 , 0);
		g.drawString("Enemies : " + (enemyTankArrayList.size() + enemyTanksRemaining) , 0 , 20);

	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {

		Input input = container.getInput();
		TankGame tg = (TankGame)game;

		boolean notTouchingWall = true;


		for (Entity b : brickArrayList){
			temp = playerTank.collides(b);

			//check for wall collision of the player
			if (temp != null){	//if tank touching wall, adjust tank position so there is no penetration
				notTouchingWall = false;
				moveEntityByMinPenetrationVector(playerTank , temp);
			}
			else{
				//TO DO
			}
		}

		//check collision with the base
		temp = playerTank.collides(base);
		if (temp != null){
			notTouchingWall = false;
			moveEntityByMinPenetrationVector(playerTank , temp);
		}

		//check for collision with enemy tanks
		for (enemyTank enemy : enemyTankArrayList){
			temp = playerTank.collides(enemy);
			if (temp != null){
				notTouchingWall = false;
				moveEntityByMinPenetrationVector(playerTank , temp);
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

		updatePathForEnemies(delta);

		//check for wall collision of the enemies
		for (enemyTank enemy : enemyTankArrayList){
			boolean canMove = true;
			for (Entity b : brickArrayList) {
				temp = enemy.collides(b);

				if (temp != null) {    //if tank touching wall, adjust tank position so there is no penetration
					moveEntityByMinPenetrationVector(enemy , temp);
					canMove = false;
				}
			}

			temp = enemy.collides(base);
			if (temp != null){		//check for enemy collision with base
				canMove = false;
				moveEntityByMinPenetrationVector(enemy , temp);
			}
			if (canMove ){
				calculateGridPosition(tg);
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

		checkBaseBullets();



		//game over check
		if (playerTank.getLives() == 0 || enemyTankArrayList.size() == 0 || base.getIsDestroyed()) {
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
		int row = tank.gridPosition.row;
		int col = tank.gridPosition.column;

		if (direction == 0){ 	//go up
			if (tank.getCoarseGrainedMinY() > 0) {
				if (row == 0 || mapPosition[row-1][col] != 2)
					tank.moveTankUp();
			}
		}
		else if (direction == 1){	//go left
			if (tank.getCoarseGrainedMinX() > 0) {
				if (col == 0 || mapPosition[row][col-1] != 2)
					tank.moveTankLeft();
			}
		}

		else if (direction == 2){	// go down
			if (tank.getCoarseGrainedMaxY() < tg.ScreenHeight) {
				if (row == 14 || mapPosition[row+1][col] != 2)
				tank.moveTankDown();
			}

		}

		else if (direction == 3){	// go right
			if (tank.getCoarseGrainedMaxX() < tg.ScreenWidth) {
				if ( col == 14 || mapPosition[row][col + 1] != 2)
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
				mapPosition[x.gridPosition.row][x.gridPosition.column] = 0;
				itr.remove();
			}
		}
	}

	//check for collisions of bullets and bricks
	public void checkBulletsAndBricks(ArrayList <Bullet> list){
		for (Bullet bullet : list){
			for (Entity brick : brickArrayList) {
				if ( bullet.collides(brick) != null){
					bullet.setOnScreen(false);
					((Brick)brick).setIsDestroyed(true);
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
			if (x.getLives() <= 0) {
				itr.remove();
			}
		}

	}

	public void moveEntityByMinPenetrationVector(Entity tank , Collision collision){
		try {
			Vector penetration = collision.getMinPenetration();
			tank.setX(tank.getX() + penetration.getX());
			tank.setY(tank.getY() + penetration.getY());
		} catch (Exception e) {
			e.printStackTrace();
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
		if (enemyTanksRemaining == 0 || enemyTankArrayList.size() >= 4){
			return;
		}
		else{
			int location = getRandomInt(3);
			enemyTankArrayList.add(new enemyTank((location * 260) + 40, 50));
			enemyTank tank = enemyTankArrayList.get(enemyTankArrayList.size() - 1);
			calculatePathTowardsBase(tank);
			enemyTanksRemaining--;

		}
	}

	public void calculateGridPosition(TankGame tg){

		for (int i = 0; i < 15; i++){
			for(int j = 0; j < 15; j++){
				if (mapPosition[i][j] == 2){		//reset all the tank positions
					mapPosition[i][j] = 0;
				}
			}
		}

		int col = (int)(playerTank.getX() / (tg.ScreenWidth / 15f)); 		//columns
		int row = (int) (playerTank.getY() / (tg.ScreenHeight / 15));		//rows

		mapPosition[row][col] = 2;
		playerTank.gridPosition.row = row;
		playerTank.gridPosition.column = col;


		for (enemyTank tank : enemyTankArrayList){
			col = (int)(tank.getX() / (tg.ScreenWidth / 15f)); 		//columns
			row = (int) (tank.getY() / (tg.ScreenHeight / 15));		//rows

			mapPosition[row][col] = 2;
			tank.gridPosition.row = row;
			tank.gridPosition.column = col;
		}

	}
	//calculate the path to base, using aStar
	public void calculatePathTowardsBase(enemyTank tank){
		tank.pathTowardsBase.clear();
		int stoneMultiplyer = 10;
		GridBlock destination;

		if (tank.target == 0) {
			destination = new GridBlock(14, 7);
		}
		else{
			destination = new GridBlock(playerTank.gridPosition);
		}

		PriorityQueue <aStarBlock> queue = new PriorityQueue<aStarBlock>(1, (a,b) -> a.fValue - b.fValue );
		tank.pathTowardsBase.add(tank.gridPosition);

		while(true){
			queue.clear();
			GridBlock source = tank.pathTowardsBase.get(tank.pathTowardsBase.size() - 1);
			//System.out.println("Source Row: " + source.row  + "Column : " + source.column);

			int fValue;
			if (source.column - 1 >= 0){

				GridBlock tmp = new GridBlock(source.row , source.column-1);
				boolean add = true;
				for (GridBlock block : tank.pathTowardsBase){
					if (GridBlock.equal(block, tmp)){
						add = false;
					}
				}
				if (add)
					queue.add(new aStarBlock(tmp , (mapPosition[tmp.row][tmp.column]*stoneMultiplyer) + manhatanDist(tmp , destination) ));
				if (GridBlock.equal(destination,tmp)){
					tank.pathTowardsBase.add(tmp);
					return;
				}
			}
			if (source.column + 1 <= 14){
				GridBlock tmp = new GridBlock(source.row , source.column+1);

				boolean add = true;
				for (GridBlock block : tank.pathTowardsBase){
					if (GridBlock.equal(block, tmp)){
						add = false;
					}
				}
				if (add)
					queue.add(new aStarBlock(tmp , (mapPosition[tmp.row][tmp.column]*stoneMultiplyer) + manhatanDist(tmp , destination)));
				if (GridBlock.equal(destination,tmp)){
					tank.pathTowardsBase.add(tmp);
					return;
				}
			}
			if (source.row - 1 >= 0){
				GridBlock tmp = new GridBlock(source.row-1 , source.column);

				boolean add = true;
				for (GridBlock block : tank.pathTowardsBase){
					if (GridBlock.equal(block, tmp)){
						add = false;
					}
				}
				if (add)
					queue.add(new aStarBlock(tmp , (mapPosition[tmp.row][tmp.column]*stoneMultiplyer) + manhatanDist(tmp , destination)));
				if (GridBlock.equal(destination,tmp)){
					tank.pathTowardsBase.add(tmp);
					return;
				}
			}
			if (source.row + 1 <= 14){
				GridBlock tmp = new GridBlock(source.row+1 , source.column);

				boolean add = true;
				for (GridBlock block : tank.pathTowardsBase){
					if (GridBlock.equal(block, tmp)){
						add = false;
					}
				}
				if (add)
					queue.add(new aStarBlock(tmp , (mapPosition[tmp.row][tmp.column]*stoneMultiplyer) + manhatanDist(tmp , destination)));
				if (GridBlock.equal(destination,tmp)){
					tank.pathTowardsBase.add(tmp);
					return;
				}
			}
			if (queue.size() != 0) {
				tank.pathTowardsBase.add(queue.poll().gridPosition);
			}
			if (queue.size() == 0){
				break;
			}
			//System.out.println("Row: " + queue.poll().gridPosition.row  + "Column : " + queue.poll().gridPosition.column);
		}
	}

	public void checkBaseBullets(){
		for (Bullet bullet : enemyBulletArrayList){
			if ( bullet.collides(base) != null){
				bullet.setOnScreen(false);
				base.setIsDestroyed(true);
			}
		}

		for (Bullet bullet : bulletArrayList){
			if ( bullet.collides(base) != null){
				bullet.setOnScreen(false);
				base.setIsDestroyed(true);
			}
		}
	}

	public int manhatanDist(GridBlock a1, GridBlock a2){
		int rowDist = Math.abs(a1.row - a2.row);
		int colDist = Math.abs(a1.column - a2.column);
		return (rowDist + colDist);
	}

	public void updatePathForEnemies(int delta){
		printCooldown += delta;
		if ( printCooldown > 4000){
			printCooldown = 0;

			for (enemyTank enemy : enemyTankArrayList){
				calculatePathTowardsBase(enemy);
			}
		}
	}

	
}