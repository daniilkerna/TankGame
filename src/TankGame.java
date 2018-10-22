import java.util.ArrayList;

import jig.Entity;
import jig.ResourceManager;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.*;

/**
 * A Simple Game of Tank.
 * 
 * The game has three states: StartUp, Playing, and GameOver, the game
 * progresses through these states based on the user's input and the events that
 * occur. Each state is modestly different in terms of what is displayed and
 * what input is accepted.
 *
 * by Daniil Kernazhytski
 *
 * WSU Fall 2018
 * 
 */
public class TankGame extends StateBasedGame {
	
	public static final int STARTUPSTATE = 0;
	public static final int PLAYINGSTATE = 1;
	public static final int PLAYINGSTATE2 = 2;
	public static final int GAMEOVERSTATE = 3;

	public static final String Tank_Up_RSC = "resource/playerTankUp.png";
	public static final String Brick_RSC = "resource/Brick.png";
	public static final String Tank_Left_RSC = "resource/playerTankLeft.png";
	public static final String Tank_Down_RSC = "resource/playerTankDown.png";
	public static final String Tank_Right_RSC = "resource/playerTankRight.png";
	public static final String Enemy_Tank_Up_RSC = "resource/EnemyTankUp.png";
	public static final String Enemy_Tank_Left_RSC = "resource/EnemyTankLeft.png";
	public static final String Enemy_Tank_Down_RSC = "resource/EnemyTankDown.png";
	public static final String Enemy_Tank_Right_RSC = "resource/EnemyTankRight.png";
	public static final String BANG_EXPLOSIONSND_RSC = "resource/explosion.wav";
	public static final String BANG_EXPLOSIONIMG_RSC = "resource/explosion.png";
	public static final String GAMEOVER_BANNER_RSC = "resource/GameOver.png";
	public static final String STARTUP_BANNER_RSC = "resource/PressSpace.png";
	public static final String Victory_BANNER_RSC = "resource/Victory_banner.png";
	public static final String Defeat_BANNER_RSC = "resource/defeat.png";
	public static final String Bullet_RSC = "resource/Bullet.png";
	public static final String Base_RSC = "resource/Base.png";
	public static final String Stone_RSC = "resource/Stone.png";



	public final int ScreenWidth;
	public final int ScreenHeight;
	public boolean victory = false;

	ArrayList<Bang> explosions;

	/**
	 * Create the BounceGame frame, saving the width and height for later use.
	 * 
	 * @param title
	 *            the window's title
	 * @param width
	 *            the window's width
	 * @param height
	 *            the window's height
	 */
	public TankGame(String title, int width, int height) {
		super(title);
		ScreenHeight = height;
		ScreenWidth = width;

		Entity.setCoarseGrainedCollisionBoundary(Entity.AABB);
		//Entity.setCoarseGrainedCollisionBoundary(Entity.CIRCLE);
		explosions = new ArrayList<Bang>(10);
				
	}


	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		addState(new StartUpState());
		addState(new GameOverState());
		addState(new PlayingState2());
		addState(new PlayingState());
		
		// the sound resource takes a particularly long time to load,
		// we preload it here to (1) reduce latency when we first play it
		// and (2) because loading it will load the audio libraries and
		// unless that is done now, we can't *disable* sound as we
		// attempt to do in the startUp() method.
		ResourceManager.loadSound(BANG_EXPLOSIONSND_RSC);

		// preload all the resources to avoid warnings & minimize latency...
		ResourceManager.loadImage(BANG_EXPLOSIONIMG_RSC);
		ResourceManager.loadImage(Tank_Up_RSC);
		ResourceManager.loadImage(Tank_Left_RSC);
		ResourceManager.loadImage(Tank_Down_RSC);
		ResourceManager.loadImage(Tank_Right_RSC);
		ResourceManager.loadImage(Enemy_Tank_Up_RSC);
		ResourceManager.loadImage(Enemy_Tank_Left_RSC);
		ResourceManager.loadImage(Enemy_Tank_Down_RSC);
		ResourceManager.loadImage(Enemy_Tank_Right_RSC);
		ResourceManager.loadImage(GAMEOVER_BANNER_RSC);
		ResourceManager.loadImage(STARTUP_BANNER_RSC);
		ResourceManager.loadImage(Victory_BANNER_RSC);
		ResourceManager.loadImage(Defeat_BANNER_RSC);
		ResourceManager.loadImage(Brick_RSC);
		ResourceManager.loadImage(Bullet_RSC);
		ResourceManager.loadImage(Base_RSC);
		ResourceManager.loadImage(Stone_RSC);





	}
	
	public static void main(String[] args) {
		AppGameContainer app;
		try {
			app = new AppGameContainer(new TankGame("Tank City!", 600, 600));
			app.setDisplayMode(600, 600, false);
			app.setVSync(true);
			app.setShowFPS(false);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}

	}

	public void controlLevel(){
		Input input = this.getContainer().getInput();
		if (input.isKeyDown(Input.KEY_1)){
			enterState(1);
		}
		if (input.isKeyDown(Input.KEY_2)){
			enterState(2);
		}
	}



	
}
