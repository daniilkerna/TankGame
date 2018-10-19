import java.util.Iterator;

import jig.ResourceManager;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.HorizontalSplitTransition;


/**
 * This state is active when the Game is over. In this state, the ball is
 * neither drawn nor updated; and a gameover banner is displayed. A timer
 * automatically transitions back to the StartUp State.
 * 
 * Transitions From PlayingState
 * 
 * Transitions To StartUpState
 */
class GameOverState extends BasicGameState {
	

	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {

	}

	
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {

		TankGame tg = (TankGame)game;

		for (Bang b : tg.explosions)
			b.render(g);

		if (tg.victory){
			g.drawImage(ResourceManager.getImage(TankGame.Victory_BANNER_RSC), tg.ScreenWidth/2 - 200, tg.ScreenHeight/2 - 50);
		}
		else{
			g.drawImage(ResourceManager.getImage(TankGame.Defeat_BANNER_RSC), tg.ScreenWidth/2 - 200, tg.ScreenHeight/2 - 150);
		}


	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {


		// check if there are any finished explosions, if so remove them
		for (Iterator<Bang> i = ((TankGame)game).explosions.iterator(); i.hasNext();) {
			if (!i.next().isActive()) {
				i.remove();
			}
		}

	}

	@Override
	public int getID() {
		return TankGame.GAMEOVERSTATE;
	}
	
}