package com.semantic.safetycheck.app;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.BuiltinRegistry;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.semantic.safetycheck.builtin.ImpactZoneMatch;
import com.semantic.safetycheck.builtin.MatchLiteral;

/**
 * Servlet implementation class SafetyCheckServlet
 */
@SuppressWarnings("serial")
public class SafetyCheckServlet extends HttpServlet {

	public static final String defaultNameSpace = "http://www.semanticweb.org/ontologies/2015/10/SafetyCheck#";
	ServletContext context = null;
	static public Model data = null;
	static public InfModel inf_data = null;

	@Override
	public void init() {
		context = getServletContext();
		data = populateData();
		// listEarthquakes(data);
		// listPersons(data);
		// listRegions(data);
		// listEarthquakes(data);
		registerCustomBuiltins();
		inf_data = addJenaRules(data);
		//listPersons(inf_data);
		// listAll(inf_data);
	}

	/**
	 * Default constructor.
	 */
	public SafetyCheckServlet() {
		// TODO Auto-generated constructor stub
	}


	public static void registerCustomBuiltins() {
		BuiltinRegistry.theRegistry.register(new MatchLiteral());
		BuiltinRegistry.theRegistry.register(new ImpactZoneMatch());
	}

	public Model populateData() {
		Model data = ModelFactory.createOntologyModel();

		InputStream owlFile = context
				.getResourceAsStream("/WEB-INF/classes/SafetyCheck.owl");
		InputStream friendsFile = context
				.getResourceAsStream("/WEB-INF/classes/friends.rdf");
		InputStream regionsFile = context
				.getResourceAsStream("/WEB-INF/classes/region.rdf");
		InputStream earthquakesFile = context
				.getResourceAsStream("/WEB-INF/classes/earthquakes_10.rdf");
		data.read(owlFile, defaultNameSpace);
		data.read(friendsFile, defaultNameSpace);
		data.read(regionsFile, defaultNameSpace);
		data.read(earthquakesFile, defaultNameSpace);
		try {
			owlFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			friendsFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			regionsFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			earthquakesFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}


	public InfModel addJenaRules(Model model) {

		Reasoner reasoner = new GenericRuleReasoner(
				Rule.rulesFromURL(context.getRealPath("/WEB-INF/classes/rules.txt")));
		reasoner.setDerivationLogging(true);
		InfModel inf = ModelFactory.createInfModel(reasoner, model);

		// GenericRuleReasoner rdfsReasoner = (GenericRuleReasoner)
		// ReasonerRegistry
		// .getRDFSReasoner();
		//
		// // Steal its rules, and add one of our own, and create a
		// // reasoner with these rules
		// List<Rule> customRules = new
		// ArrayList<Rule>(rdfsReasoner.getRules());
		//
		//
		// //String customRule =
		// "[rule1: (?person rdf:type sc:Person) (?loc rdf:type sc:Region) (?person sc:hasLocation ?locName) (?loc sc:hasLocationName ?locName) -> (?person sc:locatedAt ?loc)]";
		// //customRules.add(Rule.parseRule(customRule));
		// Reasoner reasoner = new GenericRuleReasoner(customRules);
		//
		// reasoner.setDerivationLogging(true);
		// InfModel inf = ModelFactory.createInfModel(reasoner, model);
		return inf;
	}
	
	public static void addEarthquakeInstance(String eq){
		InputStream is = new ByteArrayInputStream(eq.getBytes());
		
		SafetyCheckServlet.data.read(is, SafetyCheckServlet.defaultNameSpace);
		System.out.println("Written");
	}

}
