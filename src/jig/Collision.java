package jig;

public final class Collision {
	private final Shape thisShape;
	private final Shape otherShape;
	private final Vector minPenetration;
	
	public Collision(final Shape thisShape, final Shape otherShape, final Vector minPenetration) {
		this.thisShape = thisShape;
		this.otherShape = otherShape;
		this.minPenetration = minPenetration;
	}
	
	public Collision(final Shape thisShape, final Shape otherShape) {
		this.thisShape = thisShape;
		this.otherShape = otherShape;
		this.minPenetration = null;
	}
	
	public Collision() {
		thisShape = null;
		otherShape = null;
		minPenetration = null;
	}
	
	public Shape getThisShape() {
		return thisShape;
	}
	
	public Shape getOtherShape() {
		return otherShape;
	}
	
	public Vector getMinPenetration() {
		return minPenetration;
	}	
}
