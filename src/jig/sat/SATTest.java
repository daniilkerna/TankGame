package jig.sat;

import java.util.Iterator;
import java.util.LinkedList;

import javax.imageio.spi.ServiceRegistry;

import jig.ConvexPolygon;
import jig.Entity;
import jig.ResourceManager;
import jig.Shape;
import jig.Vector;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class SATTest extends BasicGame {
	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 600;
	
//	private static final float LINE_WIDTH = 2.0f;
	
	private static final String POLY1_HEADER = "Shape 1: ";
	private static final String POLY2_HEADER = "Shape 2: ";
	private static final String COLLISION_HEADER = "Collision: ";

	private static final float CLASS_Y = 12;
	private static final float POLY1_DATA_X = 120;
	private static final float POLY1_DATA_Y = 500;
	private static final float POLY2_DATA_X = 320;
	private static final float POLY2_DATA_Y = 500;
	private static final float COLLISION_DATA_X = 520;
	private static final float COLLISION_DATA_Y = 500;
	
	private static final float BOUNDS_MIN_X = 50;
	private static final float BOUNDS_MAX_X = 750;
	private static final float BOUNDS_MIN_Y = 50;
	private static final float BOUNDS_MAX_Y = 450;
	
	private static final float DEFAULT_RADIUS = 50.0f;
	
	private Entity e1;
	private Shape s1;
	private Entity e2;
	private Shape s2;
	
	private Color poly1Color = Color.black;
	private Color poly2Color = Color.red;
	private Color fontColor = Color.black;
	
	private Color trueCollision = new Color(0, 160, 0);
	private Color falseCollision = Color.red;
	
	private SAT implementation;
	private LinkedList<SAT> implementations;
	
	private Image bg;
	
	public SATTest(String title) {
		super(title);
		
		Entity.setCoarseGrainedCollisionBoundary(Entity.CIRCLE);
		e1 = new Entity(200, 200);
		e1.addShape(s1 = createShape(4), null, poly1Color);
		e2 = new Entity((BOUNDS_MIN_X + BOUNDS_MAX_X) / 2, (BOUNDS_MIN_Y + BOUNDS_MAX_Y) / 2);
		e2.addShape(s2 = createShape(4), null, poly2Color);
	}
	
	@Override
	public void init(GameContainer container) throws SlickException {
		ResourceManager.loadImage("jig/sat/resource/test_background.png");
		bg = ResourceManager.getImage("jig/sat/resource/test_background.png");
		
		implementations = new LinkedList<SAT>();
		for (Iterator<SAT> f = ServiceRegistry.lookupProviders(SAT.class); f.hasNext();)
			implementations.add(f.next());
		if (implementations.size() == 0)
			throw new IllegalStateException("Couldn't find a valid SAT implementation.");
		nextImplementation();
	}

	@Override
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		bg.draw();
		
		e1.render(g);
		e2.render(g);
		
		Vector axis = implementation.minPenetration(e1.getGloballyTransformedShapes().get(0), 
				e2.getGloballyTransformedShapes().get(0), true);
		if(axis != null) {
			Color c = g.getColor();
			g.setColor(poly1Color);
			axis = axis.scale(100);
			g.drawLine(e1.getX(), e1.getY(), e1.getX() + axis.getX(), e1.getY() + axis.getY());
			g.setColor(c);
		}
		
		Color c = g.getColor();
		g.setColor(fontColor);

		String p1x = String.format("%3.3f", e1.getX());
		String p1y = String.format("%3.3f", e1.getX());
		String p1a = String.format("%3.3f", e1.getRotation());
		String p1s = String.format("%3.3f", e1.getScale());

		String p2x = String.format("%3.3f", e2.getX());
		String p2y = String.format("%3.3f", e2.getY());
		String p2a = String.format("%3.3f", e2.getRotation());
		String p2s = String.format("%3.3f", e2.getScale());
		
		String pos1 = "X: " + p1x + "\nY: " + p1y + "\nAngle: " + p1a + "\nScale: " + p1s;
		String pos2 = "X: " + p2x + "\nY: " + p2y + "\nAngle: " + p2a + "\nScale: " + p2s;
		String coll = axis != null ? "TRUE" : "FALSE";
		
		int w1 = container.getDefaultFont().getWidth(POLY1_HEADER);
		int w2 = container.getDefaultFont().getWidth(POLY2_HEADER);
		int w3 = container.getDefaultFont().getWidth(COLLISION_HEADER);
		
		g.drawString(POLY1_HEADER, POLY1_DATA_X, POLY1_DATA_Y);
		g.drawString(pos1, POLY1_DATA_X + w1, POLY1_DATA_Y);
		
		g.drawString(POLY2_HEADER, POLY2_DATA_X, POLY2_DATA_Y);
		g.drawString(pos2, POLY2_DATA_X + w2, POLY2_DATA_Y);
		
		String name = implementation.getClass().getName();
		g.drawString(name, WINDOW_WIDTH / 2 - container.getDefaultFont().getWidth(name) / 2, CLASS_Y);
		
		g.drawString(COLLISION_HEADER, COLLISION_DATA_X, COLLISION_DATA_Y);
		if (coll.equals("TRUE"))
			g.setColor(trueCollision);
		else
			g.setColor(falseCollision);
		g.drawString(coll, COLLISION_DATA_X + w3, COLLISION_DATA_Y);
		
		g.setColor(c);
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		Input in = container.getInput();
		boolean shift = in.isKeyDown(Input.KEY_LSHIFT) || in.isKeyDown(Input.KEY_RSHIFT);
		
		if (in.isKeyPressed(Input.KEY_N))
			nextImplementation();
		
		if (in.isKeyDown(Input.KEY_R)) {
			e1.setRotation(0);
			e2.setRotation(0);
			e1.setScale(1.0f);
			e2.setScale(1.0f);
		}
		
		if (in.isKeyDown(Input.KEY_RIGHT)) {
			if (shift)
				e2.translate(0.15f * delta, 0);
			else
				e1.translate(0.15f * delta, 0);
		} else if(in.isKeyDown(Input.KEY_LEFT)) {
			if (shift)
				e2.translate(-0.15f * delta, 0);
			else
				e1.translate(-0.15f * delta, 0);
		}
		
		if (in.isKeyDown(Input.KEY_DOWN)) {
			if (shift)
				e2.translate(0, 0.15f * delta);
			else
				e1.translate(0, 0.15f * delta);
		} else if(in.isKeyDown(Input.KEY_UP)) {
			if (shift)
				e2.translate(0, -0.15f * delta);
			else
				e1.translate(0, -0.15f * delta);
		}
		
		if (in.isKeyDown(Input.KEY_D)) {
			if (shift)
				e2.rotate(0.20f * delta);
			else
				e1.rotate(0.20f * delta);
		} else if(in.isKeyDown(Input.KEY_A)) {
			if (shift)
				e2.rotate(-0.20f * delta);
			else
				e1.rotate(-0.20f * delta);
		}

		if (in.isKeyDown(Input.KEY_W)) {
			if (shift)
				e2.setScale(e2.getScale() + 0.005f * delta);
			else
				e1.setScale(e1.getScale() + 0.005f * delta);
		} else if(in.isKeyDown(Input.KEY_S)) {
			if (shift)
				e2.setScale(e2.getScale() - 0.005f * delta);
			else
				e1.setScale(e1.getScale() - 0.005f * delta);
		}
		
		if (e1.getScale() > 2.0f)
			e1.setScale(2.0f);
		else if (e1.getScale() < 0.5f)
			e1.setScale(0.5f);
		
		if (e2.getScale() > 2.0f)
			e2.setScale(2.0f);
		else if (e2.getScale() < 0.5f)
			e2.setScale(0.5f);
		
		if (in.isKeyPressed(Input.KEY_2)) {
			if (shift) {
				e2.removeShape(s2);
				e2.addShape(s2 = createShape(2), null, poly2Color);
			} else {
				e1.removeShape(s1);
				e1.addShape(s1 = createShape(2), null, poly1Color);
			}
		} else if (in.isKeyPressed(Input.KEY_3)) {
			if (shift) {
				e2.removeShape(s2);
				e2.addShape(s2 = createShape(3), null, poly2Color);
			} else {
				e1.removeShape(s1);
				e1.addShape(s1 = createShape(3), null, poly1Color);
			}
		} else if (in.isKeyPressed(Input.KEY_4)) {
			if (shift) {
				e2.removeShape(s2);
				e2.addShape(s2 = createShape(4), null, poly2Color);
			} else {
				e1.removeShape(s1);
				e1.addShape(s1 = createShape(4), null, poly1Color);
			}
		} else if (in.isKeyPressed(Input.KEY_5)) {
			if (shift) {
				e2.removeShape(s2);
				e2.addShape(s2 = createShape(5), null, poly2Color);
			} else {
				e1.removeShape(s1);
				e1.addShape(s1 = createShape(5), null, poly1Color);
			}
		} else if (in.isKeyPressed(Input.KEY_6)) {
			if (shift) {
				e2.removeShape(s2);
				e2.addShape(s2 = createShape(6), null, poly2Color);
			} else {
				e1.removeShape(s1);
				e1.addShape(s1 = createShape(6), null, poly1Color);
			}
		} else if (in.isKeyPressed(Input.KEY_7)) {
			if (shift) {
				e2.removeShape(s2);
				e2.addShape(s2 = createShape(7), null, poly2Color);
			} else {
				e1.removeShape(s1);
				e1.addShape(s1 = createShape(7), null, poly1Color);
			}
		} else if (in.isKeyPressed(Input.KEY_8)) {
			if (shift) {
				e2.removeShape(s2);
				e2.addShape(s2 = createShape(8), null, poly2Color);
			} else {
				e1.removeShape(s1);
				e1.addShape(s1 = createShape(8), null, poly1Color);
			}
		} else if (in.isKeyPressed(Input.KEY_9)) {
			if (shift) {
				e2.removeShape(s2);
				e2.addShape(s2 = createShape(9), null, poly2Color);
			} else {
				e1.removeShape(s1);
				e1.addShape(s1 = createShape(9), null, poly1Color);
			}
		}
		
		test(e1);
		test(e2);
	}

	private ConvexPolygon createShape(int n) {
		return new ConvexPolygon(DEFAULT_RADIUS, n);
	}
	
	public void test(Entity e) {
		Shape test = e.getGloballyTransformedShapes().get(0);
		if (test.getMinX() < BOUNDS_MIN_X)
			e.translate(BOUNDS_MIN_X - test.getMinX(), 0);
		else if (test.getMaxX() > BOUNDS_MAX_X)
			e.translate(BOUNDS_MAX_X - test.getMaxX(), 0);
		
		if (test.getMinY() < BOUNDS_MIN_Y)
			e.translate(0, BOUNDS_MIN_Y - test.getMinY());
		else if (test.getMaxY() > BOUNDS_MAX_Y)
			e1.translate(0, BOUNDS_MAX_Y - test.getMaxY());
	}
	
	public static void main(String[] args) throws SlickException {
		AppGameContainer app = new AppGameContainer(new SATTest("SAT"));
		app.setDisplayMode(WINDOW_WIDTH, WINDOW_HEIGHT, false);
		app.setVSync(true);
		app.start();
	}
	
	private void nextImplementation() {
		implementation = implementations.remove();
		implementations.add(implementation);
	}
}
