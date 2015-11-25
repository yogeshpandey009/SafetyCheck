package com.semantic.safetycheck.app;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.BuiltinRegistry;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.util.FileManager;
import com.semantic.safetycheck.builtin.ImpactZoneMatch;
import com.semantic.safetycheck.builtin.MatchLiteral;

/**
 * Servlet implementation class SafetyCheckServlet
 */
@WebServlet("/SafetyCheckServlet")
public class SafetyCheckServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static String defaultNameSpace = "http://www.semanticweb.org/ontologies/2015/10/SafetyCheck#";

	@Override
	public void init(){
		Model data = populateData();
		//listEarthquakes(data);
		//listPersons(data);
		//listRegions(data);
		//listEarthquakes(data);
		registerCustomBuiltins();
		InfModel inf_data = addJenaRules(data);
		listPersons(inf_data);
		//listAll(inf_data);
	}
	
    /**
     * Default constructor. 
     */
    public SafetyCheckServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public static void registerCustomBuiltins() {
		BuiltinRegistry.theRegistry.register(new MatchLiteral());
		BuiltinRegistry.theRegistry.register(new ImpactZoneMatch());
	}

	public static Model populateData() {
		Model data = ModelFactory.createOntologyModel();
		InputStream owlFile = FileManager.get().open(
				"ontologies/SafetyCheck.owl");
		InputStream friendsFile = FileManager.get().open(
				"rdf/friends.rdf");
		InputStream regionsFile = FileManager.get().open(
				"rdf/region.rdf");
		InputStream earthquakesFile = FileManager.get().open(
				"rdf/earthquakes_10.rdf");
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

	public static void listAll(Model model) {
		ResultSet rs = runQuery(" select ?x ?y where { ?x rdf:type ?y. }",
				model); // add the query string
		while (rs.hasNext()) {
			QuerySolution soln = rs.nextSolution();
			RDFNode x = soln.get("?x");
			if (x != null) {
				System.out.println("x:" + soln.get("?x").toString());
			} else
				System.out.println("No x found!");

		}

	}

	public static void listEarthquakes(Model model) {
		ResultSet rs = runQuery(
				" select ?earthquake ?magnitude ?latitude ?longitude where { ?earthquake rdf:type sc:Earthquake. ?earthquake sc:hasMagnitude ?magnitude . ?earthquake sc:atLongitude ?longitude . ?earthquake sc:atLatitude ?latitude. }",
				model); // add the query string
		while (rs.hasNext()) {
			QuerySolution soln = rs.nextSolution();
			RDFNode earthquake = soln.get("?earthquake");
			if (earthquake != null) {
				System.out.print("Earthquake of magnitude "
						+ soln.getLiteral("?magnitude").getFloat());
				System.out.print(" atLatitude "
						+ soln.getLiteral("?latitude").getFloat());
				System.out.println(" atLongitude "
						+ soln.getLiteral("?longitude").getFloat());

			} else
				System.out.println("No Earthquakes found!");

		}

	}

	public static void listPersons(Model model) {
		ResultSet rs = runQuery(
				" select ?person ?name ?location ?region ?lat ?lon ?mag "
				+ "where { ?person rdf:type sc:Person. ?person sc:hasName ?name . ?person sc:hasLocation ?location. "
				+ "OPTIONAL {?person sc:isImpactedBy ?earthquake. ?earthquake sc:hasMagnitude ?mag.} "
				+ "OPTIONAL {?person sc:locatedAt ?region. ?region sc:hasLatitude ?lat. ?region sc:hasLongitude ?lon.} }",
				model); // add the query string
		while (rs.hasNext()) {
			QuerySolution soln = rs.nextSolution();
			RDFNode person = soln.get("?person");
			if (person != null) {
				System.out.print("Person with name "
						+ soln.getLiteral("?name").getString());
				System.out.println(" located at "
						+ soln.getLiteral("?location").getString());
				if(soln.get("?region") != null) {
					System.out.print(" latitude "
							+ soln.getLiteral("?lat").getString());
					System.out.println(" longitude "
							+ soln.getLiteral("?lon").getString());
				}
				if(soln.get("?mag") != null) {
					System.out.println(" Magnitude "
							+ soln.getLiteral("?mag").getString());
				}
				
			} else
				System.out.println("No Person found!");

		}

	}

	public static void listRegions(Model model) {
		ResultSet rs = runQuery(
				" select ?region ?latitude ?longitude where { ?region rdf:type sc:Region. ?region sc:hasLatitude ?latitude . ?region sc:hasLongitude ?longitude. }",
				model); // add the query string
		while (rs.hasNext()) {
			QuerySolution soln = rs.nextSolution();
			RDFNode region = soln.get("?region");
			if (region != null) {
				System.out.print("Region " + region.toString()
						+ " has co-oridinates of ");
				System.out
						.print(soln.getLiteral("?latitude").getString() + " ");
				System.out.println(soln.getLiteral("?longitude").getString());
			} else
				System.out.println("No Region found!");

		}

	}

	public static ResultSet runQuery(String queryRequest, Model model) {

		StringBuffer queryStr = new StringBuffer();

		// Establish Prefixes
		// Set default Name space first
		queryStr.append("PREFIX sc" + ": <" + defaultNameSpace + "> ");
		queryStr.append("PREFIX rdfs" + ": <"
				+ "http://www.w3.org/2000/01/rdf-schema#" + "> ");
		queryStr.append("PREFIX rdf" + ": <"
				+ "http://www.w3.org/1999/02/22-rdf-syntax-ns#" + "> ");

		// Now add query
		queryStr.append(queryRequest);
		Query query = QueryFactory.create(queryStr.toString());
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		ResultSet response = null;
		// try {
		response = qexec.execSelect();
		// } finally {
		// qexec.close();
		// }
		return response;
	}

	public static InfModel addJenaRules(Model model) {

		Reasoner reasoner = new GenericRuleReasoner(
				Rule.rulesFromURL("file:ontologies/rules.txt"));
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

}
