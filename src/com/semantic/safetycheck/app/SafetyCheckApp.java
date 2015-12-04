package com.semantic.safetycheck.app;

import java.io.IOException;
import java.io.InputStream;

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

public class SafetyCheckApp {

	static String defaultNameSpace = "http://www.semanticweb.org/ontologies/2015/10/SafetyCheck#";

	public static void main(String... args) {
		Model data = populateData();

		// listEarthquakes(data);
		listPersons(data);
		// listRegions(data);
		listEarthquakes(data);
		registerCustomBuiltins();
		InfModel inf_data = addJenaRules(data);
		listPersons(inf_data);
		listImpactedByEarthquakes(inf_data);
		listPersonsImpacted(inf_data);
		// listEarthquakes(inf_data);
		// listAll(inf_data);

	}

	public static void registerCustomBuiltins() {
		BuiltinRegistry.theRegistry.register(new MatchLiteral());
		BuiltinRegistry.theRegistry.register(new ImpactZoneMatch());
	}

	public static Model populateData() {
		Model data = ModelFactory.createOntologyModel();
		InputStream owlFile = FileManager.get().open(
				"resources/SafetyCheck.owl");
		InputStream friendsFile = FileManager.get().open(
				"resources/friends.rdf");
		InputStream regionsFile = FileManager.get()
				.open("resources/region.rdf");
		InputStream earthquakesFile = FileManager.get().open(
				"resources/earthquakes_10.rdf");
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
		System.out.println("listEarthquakes");
		/*ResultSet rs = runQuery(
				" select ?person ?name ?location ?latitude ?longitude where { "
				+ " <"+eid+"> sc:impacts ?person. "
				+ " ?person sc:hasName ?name."
				+ " ?person sc:hasLocation ?location."
				+ " ?person sc:hasLatitude ?latitude."
				+ " ?person sc:hasLongitude ?longitude }",
				model); */
		ResultSet rs = runQuery(
				" select ?person ?name ?location ?latitude ?longitude  "
						+ "where { ?earthquake rdf:type sc:Earthquake."
						+ "?earthquake sc:atLongitude ?longitude . "
						+ "?earthquake sc:atLatitude ?latitude."
						+ " ?person  sc:isImpactedBy ?earthquake.    }", model); // add
																					// the
																					// query
																					// string

		while (rs.hasNext()) {
			//System.out.println("listEarthquakes3");
			QuerySolution soln = rs.nextSolution();
			RDFNode person = soln.get("?person");
			if (person != null) {
				System.out.println(soln.getLiteral("?name").getString());
			} else
				System.out.println("Not found!");

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
				if (soln.get("?region") != null) {
					System.out.print(" latitude "
							+ soln.getLiteral("?lat").getString());
					System.out.println(" longitude "
							+ soln.getLiteral("?lon").getString());
				}
				if (soln.get("?mag") != null) {
					System.out.println(" Magnitude "
							+ soln.getLiteral("?mag").getString());
				}

			} else
				System.out.println("No Person found!");

		}

	}

	public static void listImpactedByEarthquakes(Model model) {
		String personId = "http://www.semanticweb.org/ontologies/2015/10/SafetyCheck#friend9";

		System.out.println(personId + " has been impactedBy ");
		ResultSet rs = runQuery(
				"select ?earthquake ?lat ?lon ?mag ?time "
						+ "where { <"
						+ personId
						+ ">  sc:isImpactedBy ?earthquake. ?earthquake sc:hasMagnitude ?mag. "
						+ "?earthquake sc:atLatitude ?lat. ?earthquake sc:atLongitude ?lon ."
						+ "?earthquake sc:hasTime ?time } ", model);
		while (rs.hasNext()) {
			QuerySolution soln = rs.nextSolution();
			RDFNode earthquake = soln.get("?earthquake");
			if (earthquake != null) {
				System.out.print(earthquake.toString());
				System.out.println(" of magnitude "
						+ soln.getLiteral("?mag").getString());
				System.out.print("at " + soln.getLiteral("?lat").getFloat());
				System.out.println(" " + soln.getLiteral("?lon").getFloat());
				System.out
						.println("on " + soln.getLiteral("?time").getString());

			}
		}
	}

	public static void listPersonsImpacted(Model model) {
		String earthquakeId = "http://www.semanticweb.org/ontologies/2015/10/SafetyCheck#earthquake8";
		System.out.println(earthquakeId + " has impacted ");
		ResultSet rs = runQuery(
				" select ?person ?name ?location ?region ?lat ?lon "
						+ " where { ?person sc:isImpactedBy <"
						+ earthquakeId
						+ ">. ?person sc:hasName ?name . ?person sc:hasLocation ?location."
						+ " ?person sc:locatedAt ?region. ?region sc:hasLatitude ?lat. ?region sc:hasLongitude ?lon. }",
				model);
		while (rs.hasNext()) {
			QuerySolution soln = rs.nextSolution();
			RDFNode person = soln.get("?person");
			if (person != null) {
				System.out.println(soln.getLiteral("?name").getString());
				System.out.println(soln.getLiteral("?location").getString());
				System.out.print("at " + soln.getLiteral("?lat").getFloat());
				System.out.println(" " + soln.getLiteral("?lon").getFloat());
			}
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
				Rule.rulesFromURL("file:resources/rules.txt"));
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

	// private void bindReasoner(Model schema) {
	// Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
	// reasoner = reasoner.bindSchema(schema);
	// inferredFriends = ModelFactory.createInfModel(reasoner, _friends);
	// }

}