package com.semantic.safetycheck.builtin;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.reasoner.rulesys.RuleContext;
import com.hp.hpl.jena.reasoner.rulesys.builtins.BaseBuiltin;

public class EQImpactZoneMatch extends BaseBuiltin {
	/**
	 * Return a name for this builtin, normally this will be the name of the
	 * functor that will be used to invoke it.
	 */
	public String getName() {
		return "impactMatch";
	}

	/**
	 * Return the expected number of arguments for this functor or 0 if the
	 * number is flexible.
	 */
	public int getArgLength() {
		return 5;
	}

	/**
	 * This method is invoked when the builtin is called in a rule body.
	 * 
	 * @param args
	 *            the array of argument values for the builtin, this is an array
	 *            of Nodes, some of which may be Node_RuleVariables.
	 * @param length
	 *            the length of the argument list, may be less than the length
	 *            of the args array for some rule engines
	 * @param context
	 *            an execution context giving access to other relevant data
	 * @return return true if the buildin predicate is deemed to have succeeded
	 *         in the current environment
	 */
	public boolean bodyCall(Node[] args, int length, RuleContext context) {
		checkArgs(length, context);
		// BindingEnvironment env = context.getEnv();
		Node eLatNode = getArg(0, args, context);
		Node eLonNode = getArg(1, args, context);
		Node rLatNode = getArg(2, args, context);
		Node rLonNode = getArg(3, args, context);
		Node magNode = getArg(4, args, context);
		if (eLatNode.isLiteral() && eLonNode.isLiteral() && rLatNode.isLiteral() && rLonNode.isLiteral()
				&& magNode.isLiteral()) {
			Object eLatObj = eLatNode.getLiteralValue();
			Object eLonObj = eLonNode.getLiteralValue();
			Object rLatObj = rLatNode.getLiteralValue();
			Object rLonObj = rLonNode.getLiteralValue();
			Object magObj = magNode.getLiteralValue();

			if (eLatObj instanceof Float && eLonObj instanceof Float && rLatObj instanceof Float
					&& rLonObj instanceof Float && magObj instanceof Float) {
				Float eLat = (Float) eLatObj;
				Float eLon = (Float) eLonObj;
				Float rLat = (Float) rLatObj;
				Float rLon = (Float) rLonObj;
				Float mag = (Float) magObj;
				float radiusInKm = computeEarthquakeRadius(mag);
				float radiusInDeg = radiusInKm / 110;
				return liesInsideEarthquake(eLat, eLon, rLat, rLon, radiusInDeg);
				/*
				 * Float diff = Math.abs(eLat - rLat); Float diff2 =
				 * Math.abs(eLong - rLong); if (diff <= mag && diff2 <= mag) {
				 * return true; // return env.bind(args[2], n2); }
				 */
			}
		}
		// Doesn't (yet) handle partially bound cases
		return false;
	}

	// References:
	// http://www.aees.org.au/wp-content/uploads/2015/12/Paper_134.pdf
	// http://www.cqsrg.org/tools/perceptionradius/
	// http://seismo.cqu.edu.au/CQSRG/PerceptionRadius/
	private float computeEarthquakeRadius(float mag) {
		return (float) Math.exp((mag - 0.13) / 1.01);
	}

	/**
	 * 
	 * @param cX
	 *            epicenter X coordinate
	 * @param cY
	 *            epicenter Y coordinate
	 * @param x
	 *            point X coordinate
	 * @param y
	 *            point Y coordinate
	 * @param r
	 *            radius
	 * @return
	 */
	private boolean liesInsideEarthquake(float cX, float cY, float x, float y, float r) {
		float dx = x - cX;
		float dy = y - cY;
		return dx * dx + dy * dy <= r * r;
	}
}
