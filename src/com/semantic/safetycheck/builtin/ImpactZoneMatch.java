package com.semantic.safetycheck.builtin;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.reasoner.rulesys.RuleContext;
import com.hp.hpl.jena.reasoner.rulesys.builtins.BaseBuiltin;

public class ImpactZoneMatch extends BaseBuiltin {
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
		return 2;
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
		Node n1 = getArg(0, args, context);
		Node n2 = getArg(1, args, context);
		if (n1.isLiteral() && n2.isLiteral()) {
			Object v1 = n1.getLiteralValue();
			Object v2 = n2.getLiteralValue();

			if (v1 instanceof Float && v2 instanceof Float) {
				Float s1 = (Float) v1;
				Float s2 = (Float) v2;
				Float diff = Math.abs(s1 - s2);
				if (diff < 100) {
					return true;
					// return env.bind(args[2], n2);
				}
			}
		}
		// Doesn't (yet) handle partially bound cases
		return false;
	}
}
