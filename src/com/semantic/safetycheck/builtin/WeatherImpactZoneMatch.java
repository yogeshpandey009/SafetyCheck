package com.semantic.safetycheck.builtin;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.reasoner.rulesys.RuleContext;
import com.hp.hpl.jena.reasoner.rulesys.builtins.BaseBuiltin;
import com.semantic.safetycheck.pojo.Point;

public class WeatherImpactZoneMatch extends BaseBuiltin {
	/**
	 * Return a name for this builtin, normally this will be the name of the
	 * functor that will be used to invoke it.
	 */
	public String getName() {
		return "weatherImpactMatch";
	}

	/**
	 * Return the expected number of arguments for this functor or 0 if the
	 * number is flexible.
	 */
	public int getArgLength() {
		return 3;
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
		Node wpoly = getArg(0, args, context);
		Node rLatNode = getArg(1, args, context);
		Node rLonNode = getArg(2, args, context);
		if (rLatNode.isLiteral() && rLonNode.isLiteral() && wpoly.isLiteral()) {
			//List<Node> pointNodes = Util.convertList(wAreaNode, context);
			String[] coordinates = ((String)wpoly.getLiteralValue()).split(",");
			Object rLatObj = rLatNode.getLiteralValue();
			Object rLonObj = rLonNode.getLiteralValue();
			if (rLatObj instanceof Float && rLonObj instanceof Float) {
				Float rLat = (Float) rLatObj;
				Float rLon = (Float) rLonObj;
				List<Point> points = new ArrayList<>();
				for(String coordinate: coordinates) {
					//pNode.getLiteral().toString();
					String[] vals = coordinate.split("_");
					//float wLat = 0;
					//float wLon = 0;
					points.add(new Point(Float.parseFloat(vals[0]), Float.parseFloat(vals[1])));
				}
				return contains(points, new Point(rLat, rLon));
			}
		}

		// Doesn't (yet) handle partially bound cases
		return false;
	}

	/**
     * Return true if the given point is contained inside the boundary.
     * See: http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
     * @param test The point to check
     * @return true if the point is inside the boundary, false otherwise
     *
     */
	public boolean contains(List<Point> points, Point test) {
      int i;
      int j;
      boolean result = false;
      int size = points.size();
      for (i = 0, j = size - 1; i < size; j = i++) {
        if ((points.get(i).getLongitude() > test.getLongitude()) != (points.get(j).getLongitude() > test.getLongitude()) &&
            (test.getLatitude() < (points.get(j).getLatitude() - points.get(i).getLatitude()) * (test.getLongitude() - points.get(i).getLongitude()) / (points.get(j).getLongitude() - points.get(i).getLongitude()) + points.get(i).getLatitude())) {
          result = !result;
         }
      }
      return result;
    }
    /*
	public boolean contains(List<Point> points, Point test) {
		Polygon poly = new Polygon();
		for(Point p : points) {
			poly.addPoint((int)(p.getLatitude() * 100), (int)(p.getLongitude() * 100));
		}
		return poly.contains((int)(test.getLatitude() * 100), (int)(test.getLongitude() * 100));
	}
	*/
}