package jig;

import org.newdawn.slick.geom.Transform;

/**
 * Shape interface that defines a subset of Slick Shape
 * for ConvexPolygon to implement.
 * 
 * @author John Tasto
 * @author Alexander Smith
 */
public abstract class Shape {

	/*
	 * Vertices listed in clockwise fashion.
	 */
	protected float[] points;
	
	private float minX, maxX, minY, maxY, radius;
	private boolean boundsCalculated, radiusCalculated;
	
	public Shape() {
		boundsCalculated = radiusCalculated = false;
	}
    
	public abstract Shape transform(final Transform transform);
    
	public float getMinX() {
		if (!boundsCalculated) calculateBounds();
		return minX;
	}
	
	public float getMaxX() {
		if (!boundsCalculated) calculateBounds();
		return maxX;
	}
	
	public float getMinY() {
		if (!boundsCalculated) calculateBounds();
		return minY;
	}
	
	public float getMaxY() {
		if (!boundsCalculated) calculateBounds();
		return maxY;
	}
	
	public float getCenterX() {
		if (!boundsCalculated) calculateBounds();
		if (points.length == 4)
			return (points[0] + points[2]) / 2;
		return (minX + maxX) / 2;
	}
	
	public float getCenterY() {
		if (!boundsCalculated) calculateBounds();
		if (points.length == 4)
			return (points[1] + points[3]) / 2;
		return (minY + maxY) / 2;
	}
	
	public float getWidth() {
		if (!boundsCalculated) calculateBounds();
		if (points.length == 4)
			return (float) Math.sqrt((points[2] - points[0]) * (points[2] - points[0]) + 
					(points[3] - points[1]) * (points[3] - points[1]));
		return maxX - minX;
	}
	
	public float getHeight() {
		if (!boundsCalculated) calculateBounds();
		if (points.length == 4)
			return getWidth();
		return maxY - minY;
	}
	
	private void calculateBounds() {
		minX = minY = Float.MAX_VALUE;
		maxX = maxY = Float.MIN_VALUE;
		if (points.length == 4) {
		// circles
			float mx = (points[0] + points[2]) / 2;
			float my = (points[1] + points[3]) / 2;
			float r = (float) Math.sqrt((points[2] - points[0]) * (points[2] - points[0]) + 
					(points[3] - points[1]) * (points[3] - points[1])) / 2;
			
			minX = mx - r;
			maxX = mx + r;
			minY = my - r;
			maxY = my + r;
		} else {
		// polygons
			for (int i = 0; i < points.length / 2; ++i) {
				minX = Math.min(minX, points[2*i  ]);
				maxX = Math.max(maxX, points[2*i  ]);
				minY = Math.min(minY, points[2*i+1]);
				maxY = Math.max(maxY, points[2*i+1]);
			}
		}
		boundsCalculated = true;
	}
	
	public float getBoundingCircleRadius() {
		if (!radiusCalculated) {
			radius = 0f;
			if (points.length == 4)
			// circles
				radius = Math.max(points[2], points[3]);
			else
			// polygons
				for (int i = 0; i < points.length / 2; ++i)
					radius = Math.max(radius, (float) Math.sqrt(points[2*i]*points[2*i] + 
							points[2*i+1]*points[2*i+1]));
		}
		return radius;
	}
	
	public float[] getPoints() {
		return points;
	}
	
	public int getPointCount() {
		return points.length / 2;
	}
	
}
