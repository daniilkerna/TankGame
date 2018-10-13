package jig;

import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.util.FastTrig;

/**
 * An abstract base class representing a Convex Polygon and suitable for use with
 * the Separating Axis Theorem method of Collision Detection.
 * 
 * @author Scott Wallace
 * @author John Tasto
 * @author Alexander Smith
 *
 */
public class ConvexPolygon extends Shape {
	
	public ConvexPolygon(final float [] points) {
		super();
		this.points = points;
	}
	
	public ConvexPolygon(final Vector [] points) {
		super();
		float[] floatPoints = new float[2 * points.length];
		for (int i = 0; i < points.length; ++i) {
			floatPoints[2*i  ] = points[i].getX();
			floatPoints[2*i+1] = points[i].getY();
		}
		this.points = floatPoints;
	}
	
	/**
	 * Creates a rectangle with edges aligned with the x and y axes.
	 * 
	 * @param origin the upper left corner of the rectangle
	 * @param width the width 
	 * @param height the height
	 */
	public ConvexPolygon(final float width, final float height) {
		this(new float[] {-width/2f, -height/2f,
		                  +width/2f, -height/2f,
		                  +width/2f, +height/2f,
		                  -width/2f, +height/2f});
	}
	
	/**
	 * Creates a convex n-gon.
	 * 
	 * @param radius the radius of the circle that circumscribes the n-gon.
	 * @param n the desired number of sides/corners.
	 */
	public ConvexPolygon(final float radius, final int n) {
		super();
		float[] points = new float[2*n];
		double theta = 360.0 / n;
		for (int i = 0; i < n; ++i) {
			points[2*i  ] = radius * (float) FastTrig.cos(StrictMath.toRadians(i * theta));
			points[2*i+1] = radius * (float) FastTrig.sin(StrictMath.toRadians(i * theta));
		}
		this.points = points;
	}
	
	/**
	 * Creates a circle.
	 * 
	 * @param radius the radius of the circle.
	 */
	public ConvexPolygon(float radius) {
		this(new float [] {0f, -radius, 0f, radius});
	}

	public ConvexPolygon transform(final Transform transform) {
		float[] newPoints = new float[points.length];
		transform.transform(points, 0, newPoints, 0, points.length / 2);
		return new ConvexPolygon(newPoints);
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(40);
		sb.append("ConvexPolygon (");
		sb.append(points.length / 2);
		sb.append(")  [ ");
		for (int i = 0; i < points.length / 2; ++i) {
			sb.append('(');
			sb.append(String.format("%06.2f", points[2*i]));
			sb.append(',');
			sb.append(String.format(" %06.2f", points[2*i+1]));
			sb.append(") ");
		}
		
		sb.append(']');
		return sb.toString();
	}
	
	/**  From ConvexPolygon
	 * Renders additional 'markup' that could be useful for debugging.
	 * The markup can be turned on or off in the test environment.
	 * By default, this method renders the normal vectors from the mid point of each edge.
	 * 
	 * @param rc
	 *            the game frame's rendering context
	 * @param se
	 *            a shape engine
	 */
	/*
	public void renderMarkup(RenderingContext rc, ShapeEngine se) {

		Vector2f[] midpoints = computeEdgeMidPoints();
		Vector2f[] normals = computeLeftHandEdgeNormals();
		
		for (int i = 0; i < nCorners; i++) {
			se.renderVector(rc, color, midpoints[i], normals[i].scale(25));
		}
	}
	*/
	
	/*   From JohnTastoConvexPolygon
	public void renderMarkup(RenderingContext rc, ShapeEngine se) {

		Vector2f[] vp = calculateVertexPositions();
		
		int i;
		for(i = 1; i < vp.length; i++) {
			se.renderLine(rc, Color.red, vp[i-1].getX(), vp[i-1].getY(), vp[i].getX(), vp[i].getY());
		}
		se.renderLine(rc, Color.red, vp[i-1].getX(), vp[i-1].getY(), vp[0].getX(), vp[0].getY());
	
		super.renderMarkup(rc, se);
		}
	*/


}
