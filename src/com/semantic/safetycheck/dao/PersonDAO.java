package com.semantic.safetycheck.dao;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.semantic.safetycheck.app.SafetyCheck;
import com.semantic.safetycheck.pojo.Earthquake;
import com.semantic.safetycheck.pojo.Person;

public class PersonDAO {
	
	public List<Person> getAllPersons() {
		Model data = SafetyCheck.populateData();
		ResultSet rs = SafetyCheck.runQuery(" select ?person ?name ?location ?region ?lat ?lon where "
		+ "{ ?person rdf:type sc:Person. ?person sc:hasName ?name . "
		+ "?person sc:hasLocation ?location. OPTIONAL "
		+ "{?person sc:locatedAt ?region. ?region sc:hasLatitude ?lat."
		+ " ?region sc:hasLongitude ?lon.} }",data);
		
		List<Person> persons = new ArrayList<Person>();
		while (rs.hasNext()) {
			QuerySolution soln = rs.nextSolution();
			RDFNode person = soln.get("?person");
			if (person != null) {
				persons.add(new Person(soln.getLiteral("?name").toString(), soln.getLiteral("?location").toString(), soln.getLiteral("?lat").getFloat(),
						soln.getLiteral("?lon").getFloat()));
			}

		}
		return null;
	}


}
