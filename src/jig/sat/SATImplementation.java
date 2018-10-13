package jig.sat;

import java.util.ArrayList;
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
public class SATImplementation extends SAT {
	
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
		if (debug) System.out.println("   fixme!");
		
		if (p.length == 2) {
			Vector mid = getMidpoint(p[0], p[1]);
			float r = getRadius(p[0], p[1]);
			
			float pr = (float) Math.sqrt(x - mid.getX()) * (x - mid.getX()) + (y - mid.getY()) * (y - mid.getY());
			if (pr > r)
				return false;
			return true;
		}
		
		for (int i = 0; i < p.length; ++i)
			if (p[i].subtract(p[i==0 ? p.length-1 : i-1]).cross((new Vector(x, y)).subtract(p[i])) < 0)
				return false;
		return true;
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
		Vector[] normals = new Vector[p.length];
		for (int i = 0; i < p.length; ++i) {
			normals[i] = p[i].subtract(p[i==0 ? p.length-1 : i-1]);
			normals[i] = new Vector(normals[i].getY(), 0 - normals[i].getX()).unit();
		}
		return normals;
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
		List<Vector> axes = new ArrayList<Vector>();
		if (me.length == 2 && you.length == 2) {
			Vector mm = getMidpoint(me[0], me[1]);
			Vector ym = getMidpoint(you[0], you[1]);
			
			axes.add((new Vector(mm.getX() - ym.getX(), mm.getY() - ym.getY())).unit());
			return axes;
		}
		
		Vector[] normals;
		if (me.length == 2) {
			Vector mid = getMidpoint(me[0], me[1]);
			Vector closest = getClosest(mid, you);
			
			axes.add(mid.subtract(closest).unit());
		} else {
			normals = computeLeftHandEdgeNormals(me);
			for (Vector normal: normals) {
				boolean duplicate = false;
				for (Vector axis: axes)
					if (normal.equals(axis) || normal.equals(axis.scale(-1f)))
						duplicate = true;
				if (!duplicate)
					axes.add(normal);
			}
		}
		
		if (you != null && you.length == 2) {
			Vector mid = getMidpoint(you[0], you[1]);
			Vector closest = getClosest(mid, me);
			
			axes.add(mid.subtract(closest).unit());
		} else if (you != null) {
			normals = computeLeftHandEdgeNormals(you);
			for (Vector normal: normals) {
				boolean duplicate = false;
				for (Vector axis: axes)
					if (normal.equals(axis) || normal.equals(axis.scale(-1f)))
						duplicate = true;
				if (!duplicate)
					axes.add(normal);
			}			
		}
			
		return axes;
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
		double min1 = 0, max1 = 0, min2 = 0, max2 = 0;  // initializing to keep compiler happy
		boolean first = true;
		
		if (me.length == 2) {
			Vector mid = getMidpoint(me[0], me[1]);
			float r = getRadius(me[0], me[1]);
			double projection = mid.dot(axis);
			
			min1 = projection - r;
			max1 = projection + r;
		} else {
			for (Vector vertex : me) {
				double projection = vertex.dot(axis);
				if (first) {
					min1 = projection;
					max1 = projection;
					first = false;
				} else {
					min1 = Math.min(min1, projection);
					max1 = Math.max(max1, projection);
				}
			}
		}
		first = true;

		if (you.length == 2) {
			Vector mid = getMidpoint(you[0], you[1]);
			float r = getRadius(you[0], you[1]);
			double projection = mid.dot(axis);
			
			min2 = projection - r;
			max2 = projection + r;
		} else {
			for (Vector vertex : you) {
				double projection = vertex.copy().dot(axis);
				if (first) {
					min2 = projection;
					max2 = projection;
					first = false;
				} else {
					min2 = Math.min(min2, projection);
					max2 = Math.max(max2, projection);
				}
			}
		}
		if (verbose) System.out.format("x:%+06.1f  y:%+06.1f    min1:%+06.1f  max1:%+06.1f  " +
				"min2:%+06.1f  max2:%+06.1f\n", axis.getX(), axis.getY(), min1, max1, min2, max2);
		double overlap = NO_OVERLAP;
		if (min2 <= min1 && min1 <= max2) {
			if (verbose) System.out.format("   Intersecting by %+06.1f\n", max2 - min1);
			if (Math.abs(max2 - min1) < Math.abs(overlap) || overlap == NO_OVERLAP)
				overlap = max2 - min1;
		}
		if (min2 <= max1 && max1 <= max2) {
			if (verbose) System.out.format("   Intersecting by %+06.1f\n", min2 - max1);
			if (Math.abs(min2 - max1) < Math.abs(overlap) || overlap == NO_OVERLAP)
				overlap = min2 - max1;
		}
		if (min1 <= min2 && min2 <= max1) {
			if (verbose) System.out.format("   Intersecting by %+06.1f\n", max2 - min1);
			if (Math.abs(max2 - min1) < Math.abs(overlap) || overlap == NO_OVERLAP)
				overlap = max2 - min1;
		}
		if (min1 <= max2 && max2 <= max1) {
			if (verbose) System.out.format("   Intersecting by %+06.1f\n", min2 - max1);
			if (Math.abs(min2 - max1) < Math.abs(overlap) || overlap == NO_OVERLAP)
				overlap = min2 - max1;
		}
		return overlap;
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
		Vector[] meVerteces = getVerteces(me);
		Vector[] youVerteces = getVerteces(you);
		List<Vector> axes = getPotentialSeparatingAxes(meVerteces, youVerteces);
		double minSeparation = Double.MAX_VALUE;
		Vector direction = null;
		for (Vector axis : axes) {
			double overlap = intersectionTest(meVerteces, youVerteces, axis, verbose);
			if (verbose) System.out.format("   Min overlap is %+06.1f\n", overlap);
			if (overlap != NO_OVERLAP) {
				if (Math.abs(overlap) < Math.abs(minSeparation)) {
					minSeparation = overlap;
					direction = (overlap < 0 ? axis.scale(-1.0f) : axis);
				}
			} else {
				if (verbose) System.out.format("----------\n");
				return null;
			}
		}
		if (verbose) System.out.format("----------\n");
		//return direction.scale(direction);
		return direction;
	}
	
	private Vector getMidpoint(Vector v1, Vector v2) {
		return new Vector((v1.getX() + v2.getX()) / 2, (v1.getY() + v2.getY()) / 2);
	}
	
	private float getRadius(Vector v1, Vector v2) {
		return ((float) Math.sqrt((v2.getX() - v1.getX()) * (v2.getX() - v1.getX()) + 
				(v2.getY() - v1.getY()) * (v2.getY() - v1.getY()))) / 2.0f;
	}
	
	private Vector getClosest(Vector point, Vector[] points) {
		Vector closest = null;
		float dist = Float.MAX_VALUE;
		
		for (Vector v : points) {
			float ndist = point.distance(v);
			if (ndist < dist) {
				closest = v;
				dist = ndist;
			}
		}
		
		return closest;
	}
}
