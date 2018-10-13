package jig.sat;

import java.util.List;

import jig.ConvexPolygon;
import jig.Shape;
import jig.Vector;

/**
 * 
 * @author Scott Wallace
 * @author John Tasto
 * @author Alexander Smith
 *
 */
public abstract class SAT {
	
	protected static final double NO_OVERLAP = Double.MIN_VALUE;

	/**
	 * Task 1:
	 * 
	 * Determines whether a given point lies within the polygon. This method is
	 * used by the test environment to render your polygon from an existing texture.
	 * Specifically, the rendered image will contain only points that are 'contained'
	 * by your polygon.
	 * 
	 * @param p the verteces of the polygon
	 * @param x the x-coordinate of the specified test point
	 * @param y the y-coordinate of the specified test point
	 * @param debug true iff we should print debugging info
	 * 
	 * @return <code>true</code> iff the specified point lies on
	 * the inside of the polygon
	 *
	 */
	public boolean contains(Vector[] p, float x, float y, boolean debug) {
		return false;
	}
		
	/**
	 * Task 2:
	 * 
	 * Calculates the lefthand (outward pointing) edge normals. These should
	 * (obviously) be normal to each edge, outward facing, and in addition they
	 * should be of unit length;
	 * 
	 * Note: Edge i begins at vertex (i-1) and ends at the ith vertex Edge 0
	 * begins at vertex (nCorners - 1) and ends and vertex 0
	 * 
	 * Once this is completed, you should be able to render the markup for the
	 * convex polygons in the SATTestEnvironment
	 * 
	 * @param p the vertices of the polygon to find edge normals of
	 * 
	 * @return an array of unit length outward pointing edge normals
	 */
	public Vector[] computeLeftHandEdgeNormals(Vector[] p) {
		return null;
	}

	/**
	 * Task 3:
	 * 
	 * Returns all possible separating axes. Clever implementations will cull
	 * unnecessary or redundant axes to save time.
	 * 
	 * @param me the vertices of the first polygon
	 * @param you the second polygon whose axes should also be considered.
	 *  If this argument is <code>null</code> only the separating axes for
	 *  this polygon are returned.
	 *  
	 * @return a list of all possible separating axes.
	 */
	public List<Vector> getPotentialSeparatingAxes(Vector[] me, Vector[] you) {
		return null;
	}

	/**
	 * Task 4:
	 * 
	 * Checks for overlap between this ConvexPolygon and another along the
	 * specified separating axis. When the verbose flag is true various
	 * debugging information can be printed, logged, etc. When false
	 * implementations should be sure not to perform any unnecessary output.
	 * 
	 * @param me
	 *            the vertices polygon of the first polygon.
	 * @param you
	 *            the vertices polygon of the second polygon.
	 * @param axis
	 *            one of the potentially separating axes
	 * @return the amount of intersection along this axis or NO_OVERLAP
	 * 
	 * @see #getPotentialSeparatingAxes(ConvexPolygon)
	 */
	public double intersectionTest(Vector[] me, Vector[] you, Vector axis, boolean verbose) {
		return NO_OVERLAP;
	}

	/**
	 * Task 5:
	 * 
	 * Returns the direction that a shape should be moved to most quickly
	 * resolve a collision. This is along one of the separating axes (the axis
	 * in which there is minimum penetration). Note that this is not necessarily
	 * the direction that the object came from.
	 * 
	 * If no collision is detected, this method returns <code>null</code>
	 * 
	 * @param me
	 *            the first shape involved in the (potential) collision
	 * @param you
	 *            the second shape involved in the (potential) collision
	 * @param verbose
	 *            <code>true</code> if it is acceptable to print information
	 *            to stdout or stderr.
	 * @return the direction to move this shape to resolve the collision
	 * 
	 */
	public Vector minPenetration(Shape me, Shape you, boolean verbose) {
		return null;
	}

	
	/**
	 * Computes the mid points of each edge from its true coordinates.
	 * 
	 * @return a vector of midpoints edge i ends extends between vertex (i-1)
	 *         and vertex i
	 */
	public Vector[] computeEdgeMidPoints(Shape p) {
		Vector[] verteces = getVerteces(p);
		Vector[] midpoints = new Vector[verteces.length];

		Vector startVertex = verteces[verteces.length - 1];
		Vector endVertex;

		for (int i = 0; i < verteces.length; i++) {
			endVertex = verteces[i];
			midpoints[i] = new Vector(startVertex.getX() + (endVertex.getX() - startVertex.getX()) / 2f,
			                          startVertex.getY() + (endVertex.getY() - startVertex.getY()) / 2f);
			startVertex = endVertex;
		}
		return midpoints;
	}
	
	public Vector[] getVerteces(Shape p) {
		float floats[] = p.getPoints();
		Vector[] verteces = new Vector[floats.length / 2];
		for (int i = 0; i < verteces.length; ++i)
			verteces[i] = new Vector(floats[2*i], floats[2*i+1]);
		return verteces;
	}

}
