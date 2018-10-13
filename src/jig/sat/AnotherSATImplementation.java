package jig.sat;

import java.util.ArrayList;
import java.util.List;

import jig.Shape;
import jig.Vector;

public class AnotherSATImplementation extends SAT {
	public boolean contains(Vector[] p, float x, float y, boolean debug) {
		return false;
	}
	
	public Vector[] computeLeftHandEdgeNormals(Vector[] p) {
		Vector[] normals = new Vector[p.length];
		return normals;
	}

	public List<Vector> getPotentialSeparatingAxes(Vector[] me, Vector[] you) {
		List<Vector> axes = new ArrayList<Vector>();
		return axes;
	}

	public double intersectionTest(Vector[] me, Vector[] you, Vector axis, boolean verbose) {
		return NO_OVERLAP;
	}
	
	public Vector minPenetration(Shape me, Shape you, boolean verbose) {
		return new Vector(0, -1);
	}
}
