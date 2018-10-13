package jig;

import java.util.Iterator;
import java.util.LinkedList;

import jig.sat.SAT;
import jig.sat.SATImplementation;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Transform;

/**
 * Provides a container for images/animations and boundaries.
 * 
 * @author John Tasto
 * @author Alexander Smith
 */
public class Entity {
	public static final int UNDEFINED = 0;
	public static final int CIRCLE    = 1;
	public static final int AABB      = 2;

	private static boolean debug = false;
	public static boolean antiAliasing = true;
	public boolean debugThis = false;

	private static int coarseGrainedCollisionBoundary = UNDEFINED;
	private boolean coarseCollide;
	private boolean fineCollide;
	
	private float radius, minX, minY, maxX, maxY;
	private final LinkedList<OffsetShape>  offsetShapes;
	private final LinkedList<OffsetAnimation> animations;
	
	private Vector position;
	private double theta;
	private float  scale;
	
	private Transform transform;
	
	private SAT sat;
	
	/**
	 * Create an empty entity at the specified position
	 * @param position the position
	 */
	public Entity(final Vector position) {
		if (coarseGrainedCollisionBoundary == UNDEFINED)
			throw new IllegalStateException("Coarse grained collision boundary is undefined.");
		
		this.position = position;
		
		coarseCollide = false;
		fineCollide   = false;
		
		radius = 0f;
		minX = minY = Float.MAX_VALUE;
		maxX = maxY = Float.MIN_VALUE;
		offsetShapes = new LinkedList<OffsetShape>();
		animations = new LinkedList<OffsetAnimation>();
		
		theta = 0.0;
		scale = 1f;
		
		transform = null;
		
		sat = new SATImplementation();
	}
	
	/**
	 * Create an empty entity at the specified position
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public Entity(final float x, final float y) {
		this(new Vector(x, y));
	}
	
	/**
	 * Create an empty entity at position (0, 0)
	 */
	public Entity() {
		this(0f, 0f);
	}
	
	/**
	 * Sets the debug mode.  If turned on, this entity will draw coarse grained
	 * bounds, and force drawing any fine grained bounds. Additionally, the
	 * bounds will render in red if a collision is detected. 
	 * @param debug true to turn on debug mode
	 */
	public static void setDebug(final boolean debug) {
		Entity.debug = debug;
	}
	
	/**
	 * Get the debug mode which is true if set on either
	 * this particular instance or on the class as a whole.
	 * 
	 * @return current debugging state
	 */
	public boolean getDebug() {
		return Entity.debug || debugThis;
	}
	
	/**
	 * Sets the coarse grained collision boundary.  This method MUST be called
	 * before instantiating any entities and MUST only be called once.
	 * @param coarseShape the desired coarse grained collision shape
	 */
	public static void setCoarseGrainedCollisionBoundary(final int coarseShape) {
		if (coarseGrainedCollisionBoundary == UNDEFINED) {
			if (coarseShape == CIRCLE || coarseShape == AABB)
				coarseGrainedCollisionBoundary = coarseShape;
			else
				throw new IllegalArgumentException("Must specify a valid coarse grained collision boundary.");
		} else
			throw new IllegalStateException("Coarse grained collision boundary has already been defined.");
	}

	public static int getCoarseGrainedCollisionBoundary() {
		return coarseGrainedCollisionBoundary;
	}
	
	/**
	 * @see #setPosition(float, float)
	 * @param x The entity's new x position
	 * @param y The entity's new y position
	 */
	public void setPosition(final float x, final float y) {
		setPosition(new Vector(x, y));
	}
	
	/**
	 * Set the entity's position.
	 * @param position the enitity's new position
	 */
	public void setPosition(final Vector position) {
		this.position = position;
		transform = null;
	}
	
	/**
	 * Get the entity's position.
	 * @return A vector representing the position of the entity
	 */
	public Vector getPosition() {
		return position;
	}

	/**
	 * Set the entity's x coordinate.
	 * @param x The entity's new x coordinate
	 */
	public void setX(final float x) {
		position = position.setX(x);
		transform = null;
	}

	/**
	 * Get the entity's x coordinate.
	 * @return The x coordinate of the entity
	 */
	public float getX() {
		return position.getX();
	}

	/**
	 * Sets the entity's y coordinate.
	 * @param y The entity's new y coordinate
	 */
	public void setY(final float y) {
		position = position.setY(y);
		transform = null;
	}

	/**
	 * Get the entity's y coordinate.
	 * @return The y coordinate of the entity
	 */
	public float getY() {
		return position.getY();
	}
	
	/**
	 * Translate the entity.<br>
	 * Positive tx moves right, positive ty moves down.
	 * @param tx The amount to add to the x coordinate.
	 * @param ty The amount to add to the y coordinate.
	 */
	public void translate(final float tx, final float ty) {
		translate(new Vector(tx, ty));
	}
	
	/**
	 * Translate the entity.<br>
	 * @param t The vector to add to the entity's position.
	 */
	public void translate(final Vector t) {
		position = position.add(t);
		transform = null;
	}
	
	/**
	 * Rotates entity relative to current angle.
	 * @param degrees The number of degrees to rotate by
	 */
	public void rotate(final double degrees) {
		if (coarseGrainedCollisionBoundary != CIRCLE)
			throw new IllegalStateException("Entity coarse grained collision boundary must be a circle to allow rotation.");
		theta = (theta + degrees) % 360;
		transform = null;
	}
	
	/**
	 * Rotates entity relative to initial angle.
	 * @param degrees The number of degrees to rotate by
	 */
	public void setRotation(final double degrees) {
		if (coarseGrainedCollisionBoundary != CIRCLE)
			throw new IllegalStateException("Entity coarse grained collision boundary must be a circle to allow rotation.");
		theta = degrees % 360;
		transform = null;
	}
	
	/**
	 * The number of degrees the entity is rotated relative to initial angle.
	 * @return the number of degrees as described above
	 */
	public double getRotation() {
		return this.theta;
	}
	
	/**
	 * Scale entity relative to current size.
	 * @param scale Scale factor
	 */
	public void scale(final float scale) {
		this.scale *= scale;
		transform = null;
	}
	
	/**
	 * Scale entity relative to initial size.
	 * @param scale Scale factor
	 */
	public void setScale(final float scale) {
		this.scale = scale;
		transform = null;
	}
	
	/**
	 * The current scale factor relative to initial size
	 * @return the current scale factor as described above
	 */
	public float getScale() {
		return this.scale;
	}
	
	/**
	 * Add another shape to this image. It will be drawn with its center at the
	 * position of this entity.
	 * @param shape The shape to add
	 */
	public void addShape(final Shape shape) {
		addShape(shape, new Vector(0f, 0f), null, null);
	}
	
	/**
	 * Add another shape to this image. It will be drawn with its center
	 * offset from the position of this entity.
	 * @param shape The shape to add
	 * @param offset Offset amount
	 */
	public void addShape(final Shape shape, final Vector offset) {
		addShape(shape, offset, null, null);
	}
	
	/**
	 * Add another shape to this image. It will be drawn with its center at the
	 * position of this entity.
	 * @param shape The shape to add
	 * @param fill Fill color
	 * @param stroke Stroke color
	 */
	public void addShape(final Shape shape, final Color fill, final Color stroke) {
		addShape(shape, new Vector(0f, 0f), fill, stroke);
	}
	
	/**
	 * Add another shape to this image. It will be drawn with its center
	 * offset from the position of this entity.
	 * @param shape The shape to add
	 * @param offset Offset amount
	 * @param fill Fill color
	 * @param stroke Stroke color
	 */
	public void addShape(final Shape shape, final Vector offset, final Color fill, final Color stroke) {
		offsetShapes.add(new OffsetShape(shape, offset, fill, stroke));
		calculateCoarseGrainedBounds();
	}
	
	/**
	 * Remove a shape from this entity
	 * @param shape The shape to remove from the entity
	 */
	public void removeShape(final Shape shape) {
		Iterator<OffsetShape> itr = offsetShapes.iterator();
		while (itr.hasNext()) {
			OffsetShape s = itr.next();
			if (s.getShape() == shape) {
				itr.remove();
				break;
			}
		}
		calculateCoarseGrainedBounds();
	}
	
	/**
	 * Get the number of shapes currently associated with this entity.
	 * @return the number of shapes
	 */
	public int getNumShapes() {
		return offsetShapes.size();
	}
	
	/**
	 * Add an image to the entity.  It will be drawn with its center at the
	 * position of this entity.
	 * @param image The image to add
	 */
	public void addImage(final Image image) {
		addImage(image, new Vector(0f, 0f));
	}
	
	/**
	 * Add an image to the entity.  It will be drawn with its center offset
	 * from the position of this entity.  
	 * @param image The image to add
	 * @param offset Offset amount
	 */
	public void addImage(final Image image, final Vector offset) {
		animations.add(new OffsetAnimation(image, offset));
	}
	
	/**
	 * Add an image to the entity, along with an automatically generated
	 * rectangle for collision detection.  It will be drawn with its center at
	 * the position of this entity.  
	 * SlickShape used for collision detection
	 * @param image The image to add
	 */
	public void addImageWithBoundingBox(final Image image) {
		addImageWithBoundingBox(image, new Vector(0f, 0f));
	}
	
	/**
	 * Add an image to the entity, along with an automatically generated
	 * rectangle for collision detection.  It will be drawn with its center
	 * offset from the position of this entity.  
	 * @param image The image to add
	 * @param offset Offset amount
	 */
	public void addImageWithBoundingBox(final Image image, final Vector offset) {
		animations.add(new OffsetAnimation(image, offset));
		offsetShapes.add(new OffsetShape(new ConvexPolygon((float) image.getWidth(),
		                                                    (float) image.getHeight()),
		                                                    offset));
		calculateCoarseGrainedBounds();
	}
	
	/**
	 * Add an animation to the entity.  It will be drawn with its center at the
	 * position of this entity.  Note that to set the frame of the animation,
	 * it is necessary to maintain a reference to the animation after it has
	 * been added.
	 * @param animation The animation to add
	 */
	public void addAnimation(final Animation animation) {
		addAnimation(animation, new Vector(0f, 0f));
	}
	
	/**
	 * Add an animation to the entity.  It will be drawn with its center offset
	 * from the position of this entity.  Note that to set the frame of the
	 * animation, it is necessary to maintain a reference to the animation after
	 * it has been added.
	 * @param animation The animation to add
	 * @param offset Offset amount
	 */
	public void addAnimation(final Animation animation, final Vector offset) {
		animations.add(new OffsetAnimation(animation, offset));
	}
	
	/**
	 * Remove an image from the entity <br>
	 * Removes the first animation that contains the image
	 * @param image The image to remove from the entity
	 */
	public void removeImage(final Image image) {
		Iterator<OffsetAnimation> itr = animations.iterator();
		searchImages:
		while (itr.hasNext()) {
			OffsetAnimation a = itr.next();
			for (int i = 0; i < a.getAnimation().getFrameCount(); ++i) {
				if (a.getAnimation().getImage(i) == image) {
					itr.remove();
					break searchImages;
				}
			}
		}
	}
	
	/**
	 * Removes an animation from the entity
	 * @param animation The animation to remove from the entity
	 */
	public void removeAnimation(final Animation animation) {
		Iterator<OffsetAnimation> itr = animations.iterator();
		while (itr.hasNext()) {
			OffsetAnimation a = itr.next();
			if (a.getAnimation() == animation) {
				itr.remove();
				break;
			}
		}
	}
	
	/**
	 * @return the number of images, including animations.
	 */
	public int getNumImages() {
		return getNumAnimations();
	}
	
	/**
	 * @return the number of animations, including images.
	 */
	public int getNumAnimations() {
		return animations.size();
	}
	
	/**
	 * Test for collision between this entity and another entity.<br>
	 * Begins by checking coarse grained boundaries for collisions, and if they
	 * collide, checks for collisions between shapes.
	 * @param other The other entity to test collisions with
	 * @return null if no collision, otherwise a collision object with details
	 */
	public Collision collides(final Entity other) {
		
		// used for render markup etc
		coarseCollide = other.coarseCollide = false;
		fineCollide   = other.fineCollide   = false;
		
		// test coarse grained boundaries
		switch (coarseGrainedCollisionBoundary) {
		case CIRCLE:
			float minDistance = this.getCoarseGrainedRadius() + other.getCoarseGrainedRadius();
			if (this.position.distanceSquared(other.position) < minDistance * minDistance)
				coarseCollide = other.coarseCollide = true;
			else
				return null;
			break;
		case AABB:
			if (this.getCoarseGrainedMinX() < other.getCoarseGrainedMaxX() && this.getCoarseGrainedMaxX() > other.getCoarseGrainedMinX() && this.getCoarseGrainedMinY() < other.getCoarseGrainedMaxY() && this.getCoarseGrainedMaxY() > other.getCoarseGrainedMinY())
				coarseCollide = other.coarseCollide = true;
			else
				return null;
			break;
		}
		
		// test fine grained boundaries
		if (!offsetShapes.isEmpty() && !other.offsetShapes.isEmpty()) {
			return fineGrainedCollides(other);
		} else {
			// One or both entities do not have a shape, but circles collided, so
			// consider this a collision
			return new Collision();
		}
	}
	
	/*
	 * FOR CONVEXPOLYGON / SAT
	 */
	private Collision fineGrainedCollides(final Entity other) {
		LinkedList<Shape> otherPositionedBoundaries = other.getGloballyTransformedShapes();
		int t = 0;
		for (Shape thisShape: this.getGloballyTransformedShapes()) {
			int o = 0;
			for (Shape otherShape: otherPositionedBoundaries) {
				Vector minPenetration = sat.minPenetration(thisShape, otherShape, false);
				if (minPenetration != null) {
					fineCollide = other.fineCollide = true;
					return new Collision(offsetShapes.get(t).getShape(),
										 other.offsetShapes.get(o).getShape(),
										 minPenetration);
				}
				o++;
			}
			t++;
		}
		return null;
	}
	
	/*
	 * FOR SLICK SHAPES
	 */
//	private Collision fineGrainedCollides(final Entity other) {
//		LinkedList<Shape> otherPositionedBoundaries = other.getPositionedBoundaries();
//		int t = 0;
//		for (Shape thisShape: this.getPositionedBoundaries()) {
//			int o = 0;
//			for (Shape otherShape: otherPositionedBoundaries) {
//				if (thisShape.intersects(otherShape) ||
//					contains(thisShape, otherShape)  ||
//					contains(otherShape, thisShape))
//					return new Collision(boundaries.get(t).getOriginalShape(),
//										 other.boundaries.get(o).getOriginalShape(),
//							 			 null);
//				o++;
//			}
//			t++;
//		}
//		return null;
//	}
//
//
//	private static boolean contains(final Shape outerShape, final Shape innerShape) {
//		for (int i = 0; i < innerShape.getPointCount(); ++i) {
//			float[] pt = innerShape.getPoint(i);
//			if (!outerShape.contains(pt[0], pt[1])) {
//				return false;
//			}
//		}
//		return true;
//	}

	
	/**
	 * Draws all boundaries and images associated with the entity at their 
	 * designated offset values.
	 * @param g The current graphics context
	 */
	public void render(final Graphics g) {
				
		g.setAntiAlias(Entity.antiAliasing);
		
		// draw images
		for (OffsetAnimation a: animations) {
			g.pushTransform();
			Vector offset = a.getOffset();
			Vector halfSize = a.getSize().scale(.5f);
			g.translate(position.getX() - halfSize.getX() + offset.getX(),
						position.getY() - halfSize.getY() + offset.getY());
			g.rotate(halfSize.getX(), halfSize.getY(), (float) theta);
			g.scale(scale, scale);
			a.getAnimation().draw(0f, 0f);
			g.popTransform();
		}
		
		// draw fine grained collision boundaries
		for (OffsetShape b: offsetShapes) {
			Shape shape = b.getGloballyTransformedShape();
			Color stroke = b.getStroke();
			if (Entity.debug || debugThis) {
				if (fineCollide)
					stroke = Color.red;
				else if (stroke == null)
					stroke = Color.gray;
			}
			float[] points = shape.getPoints();
			int pointCount = shape.getPointCount();
			if (b.getFill() != null) {
				g.setColor(b.getFill());
				if (pointCount == 2) {
					float d = shape.getWidth();
					g.fillOval(shape.getMinX(), shape.getMinY(), d, d);
				} else {
					//TODO: don't use Slick's Polygon (inherits from Shape)
					Polygon s = new Polygon(shape.getPoints());
					g.fill(s);
				}
			}
			if (stroke != null) {
				g.setColor(stroke);
				if (pointCount == 2) {
					float d = shape.getWidth();
					g.drawOval(shape.getMinX(), shape.getMinY(), d, d);
				} else {
					for (int i = 0; i < pointCount - 1; ++i) {
						g.drawLine(points[2*i], points[2*i+1], points[2*i+2], points[2*i+3]);
					}
					g.drawLine(points[2*pointCount-2], points[2*pointCount-1], points[0], points[1]);
				}
			}
		}
		
		// draw coarse grained collision boundary if debug is turned on
		if (Entity.debug || debugThis) {
			if (coarseCollide) g.setColor(Color.red);
			else               g.setColor(Color.gray);
			switch (coarseGrainedCollisionBoundary) {
			case CIRCLE:
				float radius = getCoarseGrainedRadius();
				float diameter = radius * 2;
				g.drawOval(position.getX() - radius, position.getY() - radius, diameter, diameter);
				break;
			case AABB:
				float width  = getCoarseGrainedMaxX() - getCoarseGrainedMinX();
				float height = getCoarseGrainedMaxY() - getCoarseGrainedMinY();
				g.drawRect(getCoarseGrainedMinX(), getCoarseGrainedMinY(), width, height);
				break;
			}

		}
		
	}

	/**
	 * Calculates bounds large enough to contain everything currently in this entity, used
	 * for coarse grained collision detection.
	 */
	private void calculateCoarseGrainedBounds() {
		switch (coarseGrainedCollisionBoundary) {
		case CIRCLE:
			radius = 0f;
			for (Shape shape: getLocallyOffsetShapes()) {
				radius = Math.max(radius, shape.getBoundingCircleRadius());
			}
			minX = minY = -radius;
			maxX = maxY = radius;
			break;
		case AABB:
			minX = minY = Float.MAX_VALUE;
			maxX = maxY = Float.MIN_VALUE;
			for (Shape shape: getLocallyOffsetShapes()) {
				minX = Math.min(minX, shape.getMinX());
				maxX = Math.max(maxX, shape.getMaxX());
				minY = Math.min(minY, shape.getMinY());
				maxY = Math.max(maxY, shape.getMaxY());
			}
			break;
		}
	}
	
	public void setCoarseGrainedRadius(final float radius) {
		if (coarseGrainedCollisionBoundary != CIRCLE)
			throw new IllegalStateException("Coarse grained radius is meaningless when not using circles for coarse grained collision detection.");
		this.radius = radius;
	}

	public float getCoarseGrainedRadius() {
		if (coarseGrainedCollisionBoundary != CIRCLE)
			throw new IllegalStateException("Coarse grained radius is meaningless when not using circles for coarse grained collision detection.");
		return scale * radius;
	}
	
	public void  setCoarseGrainedMinX(final float minX)     { this.minX = minX;                   }
	public void  setCoarseGrainedMinY(final float minY)     { this.minY = minY;                   }
	public void  setCoarseGrainedMaxX(final float maxX)     { this.maxX = maxX;                   }
	public void  setCoarseGrainedMaxY(final float maxY)     { this.maxY = maxY;                   }

	public float getCoarseGrainedMinX()                     { return scale * minX + getX();       }
	public float getCoarseGrainedMinY()                     { return scale * minY + getY();       }
	public float getCoarseGrainedMaxX()                     { return scale * maxX + getX();       }
	public float getCoarseGrainedMaxY()                     { return scale * maxY + getY();       }
	
	public float getCoarseGrainedWidth()                    { return scale * maxX - scale * minX; }
	public float getCoarseGrainedHeight()                   { return scale * maxY - scale * minY; }
	
	/**
	 * Gets a list of all associated untransformed shapes. <br/>
	 * Note: this is probably not incredibly useful to the end user.
	 * Usually it would make more sense to call 
	 * getGloballyTransformedShapes
	 * 
	 * @see #getGloballyTransformedShapes() 
	 * @return All of the boundaries associated with this entity
	 */
	public LinkedList<Shape> getShapes() {
		LinkedList<Shape> shapes = new LinkedList<Shape>();
		for (OffsetShape offsetShape: offsetShapes)
			shapes.add(offsetShape.getShape());
		return shapes;
	}
	
	/**
	 * Gets a list of all associated shapes, transformed only by its offset.
	 * @see #getGloballyTransformedShapes()
	 * @return All of the boundaries associated with this entity
	 */
	public LinkedList<Shape> getLocallyOffsetShapes() {
		LinkedList<Shape> shapes = new LinkedList<Shape>();
		for (OffsetShape offsetShape: offsetShapes)
			shapes.add(offsetShape.getLocallyOffsetShape());
		return shapes;
	}
	
	/**
	 * Gets a list of all associated boundaries, transformed by the current
	 * rotation, scale, and offset.
	 * @see #getGloballyTransformedShapes()
	 * @return All of the boundaries associated with this entity
	 */
	public LinkedList<Shape> getLocallyTransformedShapes() {
		LinkedList<Shape> shapes = new LinkedList<Shape>();
		for (OffsetShape offsetShape: offsetShapes)
			shapes.add(offsetShape.getLocallyTranformedShape());
		return shapes;
	}
	
	/**
	 * Gets a list of all associated boundaries, transformed by the current
	 * rotation, scale, offset, and position.
	 * @return All of the boundaries associated with this entity
	 */
	public LinkedList<Shape> getGloballyTransformedShapes() {
		LinkedList<Shape> shapes = new LinkedList<Shape>();
		for (OffsetShape offsetShape: offsetShapes)
			shapes.add(offsetShape.getGloballyTransformedShape());
		return shapes;
	}
	
	private Transform getTransform() {
		if (transform == null)
			transform = Transform.createRotateTransform((float)(theta * Math.PI / 180.0))
		   .concatenate(Transform.createScaleTransform((float)scale, (float)scale));
		return transform;
	}
	
	
	
	
	private abstract class OffsetItem {
		private float unscaledLength;
		private double initialTheta;
		protected Vector originalOffset;
		
		public OffsetItem(final Vector offset) {
			originalOffset = offset;
			unscaledLength = offset.length();
			initialTheta   = offset.getRotation();
		}
		
		// rotated and scaled
		public Vector getOffset() {
			return Vector.getVector(initialTheta + theta, unscaledLength * scale);
		}
		
	}
	
	private final class OffsetShape extends OffsetItem {
		private final Shape shape;
		private final Color fill;  // NOTE: may disappear, filling is supported via Slick shapes
		private final Color stroke;
		
		public OffsetShape(final Shape shape, final Vector offset) {
			this(shape, offset, null, null);
		}
		
		public OffsetShape(final Shape shape, final Vector offset, final Color fill, final Color stroke) {
			super(offset);
			this.shape  = shape;
			this.fill   = fill;
			this.stroke = stroke;
		}
		
		// no transform applied
		public Shape getShape() {
			return shape;
		}
		
		// offset
		public Shape getLocallyOffsetShape() {
			return shape.transform(Transform.createTranslateTransform(originalOffset.getX(), originalOffset.getY()));
		}
		
		// rotated, scaled, and offset
		public Shape getLocallyTranformedShape() {
			Vector offset = getOffset();
			return shape.transform(getTransform())
			            .transform(Transform.createTranslateTransform(offset.getX(), offset.getY()));
		}
		
		// rotated, scaled, offset, and positioned
		public Shape getGloballyTransformedShape() {
			Vector offset = getOffset();
			return shape.transform(getTransform())
			            .transform(Transform.createTranslateTransform(offset.getX(), offset.getY()))
			            .transform(Transform.createTranslateTransform(position.getX(), position.getY()));
		}
		
		public Color getFill()   { return fill;   }
		public Color getStroke() { return stroke; }
	}

	private final class OffsetAnimation extends OffsetItem {
		private final Animation animation;
		private final Vector size;
		
		public OffsetAnimation(final Image image, final Vector offset) {
			super(offset);
			animation = new Animation(false);
			animation.addFrame(image, 1);
			size = new Vector(image.getWidth(), image.getHeight());
		}
		
		public OffsetAnimation(final Animation animation, final Vector offset) {
			super(offset);
			this.animation = animation;
			size = new Vector(animation.getWidth(), animation.getHeight());
		}
		
		public Vector getSize() {
			return size.scale(scale);
		}
		
		public Animation getAnimation() {
			return animation;
		}
	}
	
}
