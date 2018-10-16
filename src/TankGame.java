import java.util.ArrayList;

import jig.Entity;
import jig.ResourceManager;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.*;

/**
 * A Simple Game of Bounce.
 * 
 * The game has three states: StartUp, Playing, and GameOver, the game
 * progresses through these states based on the user's input and the events that
 * occur. Each state is modestly different in terms of what is displayed and
 * what input is accepted.
 * 
 * In the playing state, our game displays a moving rectangular "ball" that
 * bounces off the sides of the game container. The ball can be controlled by
 * input from the user.
 * 
 * When the ball bounces, it appears broken for a short time afterwards and an
 * explosion animation is played at the impact site to add a bit of eye-candy
 * additionally, we play a short explosion sound effect when the game is
 * actively being played.
 * 
 * Our game also tracks the number of bounces and syncs the game update loop
 * with the monitor's refresh rate.
 * 
 * Graphics resources courtesy of qubodup:
 * http://opengameart.org/content/bomb-explosion-animation
 * 
 * Sound resources courtesy of DJ Chronos:
 * http://www.freesound.org/people/DJ%20Chronos/sounds/123236/
 * 
 * 
 * @author wallaces
 * 
 */
public class TankGame extends StateBasedGame {
	
	public static final int STARTUPSTATE = 0;
	public static final int PLAYINGSTATE = 1;
	public static final int GAMEOVERSTATE = 2;
	
	public static final String BALL_BALLIMG_RSC = "resource/ball.png";
	public static final String Tank_Up_RSC = "resource/playerTankUp.png";
	public static final String Brick_RSC = "resource/Brick.png";
	public static final String Tank_Left_RSC = "resource/playerTankLeft.png";
	public static final String Tank_Down_RSC = "resource/playerTankDown.png";
	public static final String Tank_Right_RSC = "resource/playerTankRight.png";
	public static final String BANG_EXPLOSIONSND_RSC = "resource/explosion.wav";
	public static final String BANG_EXPLOSIONIMG_RSC = "resource/explosion.wav";
	public static final String GAMEOVER_BANNER_RSC = "resource/GameOver.png";
	public static final String STARTUP_BANNER_RSC = "resource/PressSpace.png";


	public final int ScreenWidth;
	public final int ScreenHeight;

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
		addState(new PlayingState());
		
		// the sound resource takes a particularly long time to load,
		// we preload it here to (1) reduce latency when we first play it
		// and (2) because loading it will load the audio libraries and
		// unless that is done now, we can't *disable* sound as we
		// attempt to do in the startUp() method.
		ResourceManager.loadSound(BANG_EXPLOSIONSND_RSC);	

		// preload all the resources to avoid warnings & minimize latency...
		ResourceManager.loadImage(BALL_BALLIMG_RSC);
		ResourceManager.loadImage(BANG_EXPLOSIONIMG_RSC);
		ResourceManager.loadImage(Tank_Up_RSC);
		ResourceManager.loadImage(Tank_Left_RSC);
		ResourceManager.loadImage(Tank_Down_RSC);
		ResourceManager.loadImage(Tank_Right_RSC);
		ResourceManager.loadImage(GAMEOVER_BANNER_RSC);
		ResourceManager.loadImage(STARTUP_BANNER_RSC);
		ResourceManager.loadImage(Brick_RSC);



	}
	
	public static void main(String[] args) {
		AppGameContainer app;
		try {
			app = new AppGameContainer(new TankGame("Tank City!", 800, 800));
			app.setDisplayMode(800, 800, false);
			app.setVSync(true);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}

	}



	
}
