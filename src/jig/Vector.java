package jig;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.Random;

import org.newdawn.slick.util.FastTrig;


/**
 * An immutable two dimensional vector with float components.
 * 
 * @author Scott Wallace
 * @author Kevin Glass
 * @author John Tasto
 */
public strictfp class Vector implements Serializable {

	private static final long serialVersionUID = 1977908645337480469L;
	
	private static Random random = new Random();

	private final float x;
	private final float y;
	
	/**
	 * Create a vector based on two float coordinates.
	 * @param x
	 * @param y
	 */
	public Vector(final float x, final float y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Create a vector based on the contents of a coordinate array.
	 * @param coords The coordinates array, index 0 = x, index 1 = y
	 */
	public Vector(final float[] v) {
		this(v[0], v[1]);
	}
	
	/**
	 * Create a new vector based on another.
	 * @param other The other vector to copy into this one
	 */
	public Vector(final Vector other) {
		this(other.getX(), other.getY());
	}
	
	/**
	 * Return a copy of this vector.
	 * @return The new instance that copies this vector
	 */
	public Vector copy() {
		return new Vector(x, y);
	}
	
	/**
	 * Create a new unit vector based on an angle.
	 * @param theta The angle of the vector in degrees
	 */
	public static Vector getUnit(final double theta) {
		double t = theta % 360.0;
		return new Vector((float) FastTrig.cos(StrictMath.toRadians(t)),
						  (float) FastTrig.sin(StrictMath.toRadians(t)));
	}
	
	/**
	 * Create a new vector with a specified angle and length.
	 * @param theta The angle of the vector in degrees
	 * @param length The length of the vector
	 * @return a new vector as described above
	 */
	public static Vector getVector(final double theta, final float length) {
		double t = theta % 360.0;
		return new Vector(length * (float) FastTrig.cos(StrictMath.toRadians(t)),
						  length * (float) FastTrig.sin(StrictMath.toRadians(t)));
	}
	
	/**
	 * Create a new vector with a random angle and specified length.
	 * @param length The length of the vector
	 * @return a new vector as described above
	 */
	public static Vector getRandom(final float length) {
		return getVector(random.nextDouble() * 360.0, length);
	}
	
	/**
	 * Create a new vector with a random angle and random length in the
	 * specified range.
	 * @param minLength The lower bound on the length (inclusive)
	 * @param maxLength The upper bound on the length (exclusive)
	 * @return a new vector as described above
	 */
	public static Vector getRandom(final float minLength, final float maxLength) {
		return getVector(random.nextDouble() * 360.0,
						 minLength + (random.nextFloat() * (maxLength - minLength)));
	}
	
	/**
	 * Create a new vector with a specified angle and random length in the
	 * specified range.
	 * @param theta The angle of the vector in degrees
	 * @param minLength The lower bound on the length (inclusive)
	 * @param maxLength The upper bound on the length (exclusive)
	 * @return a new vector as described above
	 */
	public static Vector getRandomLength(final double theta, final float minLength, final float maxLength) {
		return getVector(theta,
						 minLength + (random.nextFloat() * (maxLength - minLength)));
	}
	
	/**
	 * Create a new vector with a random angle in the specified range and
	 * specified length.
	 * @param minTheta The lower bound on the angle (inclusive) in degrees
	 * @param maxTheta The upper bound on the angle (exclusive) in degrees
	 * @param length The length of the vector
	 * @return a new vector as described above
	 */
	public static Vector getRandomAngle(final double minTheta, final double maxTheta, final float length) {
		return getVector(minTheta + (random.nextDouble() * (maxTheta - minTheta)),
						 length);
	}

	/**
	 * Create a new vector with a random angle in the specified range and random
	 * length in the specified range.
	 * @param minTheta The lower bound on the angle (inclusive) in degrees
	 * @param maxTheta The upper bound on the angle (exclusive) in degrees
	 * @param minLength The lower bound on the length (inclusive)
	 * @param maxLength The upper bound on the length (exclusive)
	 * @return a new vector as described above
	 */
	public static Vector getRandomRange(final double minTheta, final double maxTheta, final float minLength, final float maxLength) {
		return getVector(minTheta + (random.nextDouble() * (maxTheta - minTheta)),
						 minLength + (random.nextFloat() * (maxLength - minLength)));
	}
	
	/**
	 * Creates a new vector with random coordinates in the specified range.
	 * @param minX The lower bound on the x coordinate (inclusive).
	 * @param maxX The upper bound on the x coordinate (exclusive).
	 * @param minY The lower bound on the y coordinate (inclusive).
	 * @param maxY The upper bound on the y coordinate (exclusive).
	 * @return a new vector as described above
	 */
	public static Vector getRandomXY(final float minX, final float maxX, final float minY, final float maxY) {
		return new Vector(minX + (random.nextFloat() * (maxX - minX)),
						  minY + (random.nextFloat() * (maxY - minY)));
	}
	
	/**
	 * Adjust this vector by a given angle.
	 * @param theta The angle to adjust the angle by (in degrees)
	 * @return A new rotated vector - useful for chaining operations
	 */
	public Vector rotate(final double theta) {
		return setRotation(getRotation() + theta);
	}

	/**
	 * Calculate the components of the vector based on a angle.
	 * @param theta The angle to calculate the components from (in degrees)
	 * @return A new vector of the same length but pointing in the direction of
	 * theta
	 */
	public Vector setRotation(final double theta) {
		double t = theta % 360;
		float len = length();
		return new Vector(len * (float) FastTrig.cos(StrictMath.toRadians(t)), 
		                  len * (float) FastTrig.sin(StrictMath.toRadians(t)));
	} 
	
	/**
	 * Get the angle of this vector.
	 * @return The angle of this vector (in degrees)
	 */
	public double getRotation() {
		return StrictMath.toDegrees(StrictMath.atan2(y, x)) % 360.0;
	} 
	
	/**
	 * Get a new vector with a different x component.
	 * @param x
	 * @return a new vector with a new x and old y
	 */
	public Vector setX(final float x) {
		return new Vector(x, this.y);
	}
	
	/**
	 * Get a new vector with a different y component.
	 * @param y
	 * @return a new vector with a new x and old y
	 */
	public Vector setY(final float y) {
		return new Vector(this.x, y);
	}
	
	/**
	 * Get the x component.
	 * @return The x component
	 */
	public float getX() {
		return x;
	}

	/**
	 * Get the y component.
	 * @return The y component
	 */
	public float getY() {
		return y;
	}
	
	/**
	 * Computes a new, transformed, vector based on desired
	 * affine transform.
	 * <pre>
	 *  [ x']   [  m00  m01  m02  ] [ x ]   [ m00x + m01y + m02 ]
     *	[ y'] = [  m10  m11  m12  ] [ y ] = [ m10x + m11y + m12 ]
	 *  [ 1 ]   [   0    0    1   ] [ 1 ]   [         1         ]
     * </pre>
     * <pre>
	 * { m00 m10 m01 m11 m02 m12 }
	 * </pre>
	 * @param t the components of an affine transform
	 * as indicated above passed as a 6 element array of doubles in
	 * the order <code>m00 m10 m01 m11 m02 m12</code>
	 * @return a vector appropriately transformed
	 * @see java.awt.geom.AffineTransform
	 * @see java.awt.geom.AffineTransform#getMatrix(double[])
	 */
	public Vector transform(final float[] t) {
		return new Vector(t[0] * x + t[2] * y + t[4],
						  t[1] * x + t[3] * y + t[5]);
	}
	
	/**
	 * Dot this vector with another.
	 * @param other The other vector to dot with
	 * @return The dot product of the two vectors
	 */
	public float dot(final Vector other) {
		return x * other.getX() + y * other.getY();
	}
	
	/**
	 * Calculates the magnitude of the vector in the Z-dimension which would
	 * result from the cross product of 2 vectors in the X-Y plane (i.e., two
	 * <code>Vector2D</code> instances). In other words this performs the
	 * calculation:
	 * <pre>
	 * | 0 0  y | | b.x |   | 0 | 
	 * | 0 0 -x | | b.y | = | 0 | 
	 * | -y x 0 | |  0  |   | z |
	 * </pre>
	 * and returns that value <code>z</code> as a scalar.
	 * @param other The other vector to cross with
	 * @return The magnitude of the resulting 3-dimensional vector as a scalar
	 */
	public float cross(final Vector other) {
		return x * other.getY() - y * other.getX();
	}
	
	/**
	 * Creates a new vector that would result from the cross product of a vector
	 * in the Z-dimension with the specified magnitude and this vector. In other
	 * words, this return the following vector:
	 * <pre>
	 *  | 0  -s  0 | | x |     | -s*y |
	 *  | s   0  0 | | y |  =  |  s*x |
	 *  | 0   0  0 | | 0 |     |   0  |
	 * </pre>
	 * Most useful for rotation operations.
	 * @param z the magnitude of the vector in the Z-dimension
	 * @return the new vector that results from applying the above operation
	 */
	public Vector dCrossV(final float z) {
		return new Vector(-z * y, z * x);
	}

	/**
	 * A vector perpendicular to this vector.
	 * @return a new vector perpendicular to this vector
	 */
	public Vector getPerpendicular() {
	   return new Vector(-y, x);
	}
	
	/**
	 * Add a vector to this vector.
	 * @param v The vector to add
	 * @return A new vector - useful for chaining operations
	 */
	public Vector add(final Vector v) {
		return new Vector(x + v.getX(), y + v.getY());
	}
	
	/**
	 * Subtract a vector from this vector.
	 * @param v The vector to subtract
	 * @return A new vector - useful for chaining operations
	 */
	public Vector subtract(final Vector v) {
		return new Vector(x - v.getX(), y - v.getY());
	}

	/**
	 * Negate this vector .
	 * @return A copy of this vector negated
	 */
	public Vector negate() {
		return new Vector(-x, -y); 
	}
	
	/**
	 * Scale this vector by a value.
	 * @param a The value to scale this vector by
	 * @return A new vector - useful for chaining operations
	 */
	public Vector scale(final float a) {
		return new Vector(a * x, a * y);
	}
	
	/**
	 * Normalize the vector.
	 * @return A new vector in the same direction but of unit length
	 */
	public Vector unit() {
		float l = length();
		if (l == 0) return new Vector(this);
		return new Vector(x / l, y / l);
	}
	
	/**
	 * Set the length the vector.
	 * @param length the desired length
	 * @return A new vector in the same direction but of the desired length
	 */
	public Vector setLength(float length) {
		float l = length();
		if (l == 0) return copy();
		return new Vector(length * x / l, length * y / l);
	}

	/**
	 * Get the length of this vector.
	 * @return The length of this vector
	 */
	public float length() {
		return (float) Math.sqrt(lengthSquared());
	}
	
	/**
	 * The length of the vector, squared. This can sometimes be used in place of
	 * length and avoids the additional sqrt.
	 * @return The length of the vector squared
	 */
	public float lengthSquared() {
		return (x * x) + (y * y);
	}
	
	/**
	 * Get the distance from this point to another.
	 * @param other The other point we're measuring to
	 * @return The distance to the other point
	 */
	public float distance(final Vector other) {
		return (float) Math.sqrt(distanceSquared(other));
	}
	
	/**
	 * Get the distance from this point to another, squared. This can sometimes
	 * be used in place of distance and avoids the additional sqrt.
	 * @param other The other point we're measuring to 
	 * @return The distance to the other point squared
	 */
	public float distanceSquared(final Vector other) {
		float dx = other.getX() - getX();
		float dy = other.getY() - getY();
		return (dx*dx) + (dy*dy);
	}

	/**
	 * Project this vector onto another.
	 * @param axis The vector to project onto
	 * @result A new vector that is this projected onto the axis
	 */
	public Vector project(final Vector axis) {
		float dp = dot(axis.unit());
		return new Vector(dp * axis.getX(), dp * axis.getY());		
	}
	
	/**
	 * Find the angle from the end point of this vector to the end point of the
	 * specified vector. Equivalently, translate the origin to the end point of
	 * this vector and find the angle to the end point of the specified
	 * vector...
	 * <pre>
	 *              (0,-1) 
	 *              -PI/2
	 *                 &circ;
	 *                 |
	 * (-1,0) +-PI &lt;-- + --&gt; 0 (1,0) 
	 *                 |
	 *                 v
	 *               PI/2 
	 *               (0,1)
	 * </pre>
	 * @param a the other vector of interest
	 * @return the angle in degrees between end points
	 */
	public double angleTo(final Vector a) {
		return StrictMath.toDegrees(StrictMath.atan2(a.y - this.y, a.x - this.x)) % 360.0;
	}

	/**
	 * Reflects (bounces) the vector off of a surface.
	 * @param tangent the tangent to the surface in degrees
	 * @return a new vector reflected off the surface.
	 */
	public Vector bounce(final double tangent) {
		float m = (float) FastTrig.cos(2.0 * StrictMath.toRadians(tangent));
		float n = (float) FastTrig.sin(2.0 * StrictMath.toRadians(tangent));
		return new Vector(m*x + n*y, n*x - m*y);
	}

	/**
	 * Reflects the vector about the angle specified.
	 * @param normalunit the vector to reflect about must be unit length
	 * @return a new Vector reflected about the normal.
	 */
	public Vector reflect(final Vector normalunit) {
		Vector d = normalunit.scale(2f * normalunit.dot(this));
		return this.subtract(d);
	}
	
	/**
	 * Creates a new vector by taking the absolute value of each element in this
	 * vector.
	 * @return a new vector as described above
	 */
	public Vector abs() {
		return new Vector(Math.abs(x), Math.abs(y));
	}
	
	/**
	 * Clamps the X coordinate to the specified range.
	 * @param minX the lower bound (inclusive) of the resulting X coordinate
	 * @param maxX the upper bound (inclusive) of the resulting X coordinate
	 * @return a new Vector with the X coordinate clamped
	 */
	public Vector clampX(final float minX, final float maxX) {
		return new Vector(Math.min(maxX, Math.max(x, minX)), y);
	}

	/**
	 * Clamps the Y coordinate to the specified range.
	 * @param minY the lower bound (inclusive) of the resulting Y coordinate
	 * @param maxY the upper bound (inclusive) of the resulting Y coordinate
	 * @return a new Vector with the Y coordinate clamped
	 */
	public Vector clampY(final float minY, final float maxY) {
		return new Vector(x, Math.min(maxY, Math.max(y, minY)));
	}

	/**
	 * Clamps the coordinates to the specified rectangle.
	 * @param r the rectangular boundary region
	 * @return a new Vector2D with the coordinates clamped
	 */
	public Vector clamp(final Rectangle r) {
		return new Vector((float) Math.min(r.getMaxX(), Math.max(x, r.getMinX())),
						  (float) Math.min(r.getMaxY(), Math.max(y, r.getMinY())));
	}

	/**
	 * Clamps the vector to the specified length.
	 * @param minLength the lower bound (inclusive) of the resulting length
	 * @param maxLength the upper bound (inclusive) of the resulting length
	 * @return a new Vector2D with the length clamped
	 */
	public Vector clampLength(final float minLength, final float maxLength) {
		float lengthSquared = lengthSquared();
		if (lengthSquared == 0) return copy();
		if (lengthSquared > maxLength * maxLength) {
			float length = (float) Math.sqrt(lengthSquared);
			return new Vector(maxLength * x / length, maxLength * y / length);
		}
		if (lengthSquared < minLength * minLength) {
			float length = (float) Math.sqrt(lengthSquared);
			return new Vector(minLength * x / length, minLength * y / length);
		}
		return copy();
	}
	

	/**
	 * Gets the string representation of this vector.
	 * @return a string representing the vector
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "<" + x + ", " + y + ">";
	}

	/**
	 * Gets the string representation of this vector by subjecting each vector
	 * component to a string format.
	 * @return a string representing the vector
	 */
	public String toString(String compFormat) {
		return "<" + String.format(compFormat, x) + ", " + String.format(compFormat, y) + ">";
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
        return 997 * ((int)x) ^ 991 * ((int)y); //large primes! 
	}
	
	/**
	 * Checks for epsilon equivalence between this vector and another.
	 * @param other the vector to check for equivalence
	 * @param e the allowed variation in each dimension
	 * @return true iff the vectors are 'nearly' equal
	 */
	public boolean epsilonEquals(final Vector other, final double e) {
		if (Math.abs(x - other.x) <= e && Math.abs(y - other.y) <= e) {
			return true;
		}
		return false;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(final Object other) {
		if (other instanceof Vector) {
			return (((Vector) other).x == x) && (((Vector) other).y == y);
		}
		return false;
	}
}
