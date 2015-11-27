package com.semantic.safetycheck.dao;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.semantic.safetycheck.app.SafetyCheckHelper;
import com.semantic.safetycheck.pojo.Person;

public class PersonDAO {
	
	public List<Person> getAllPersons(Model data) {
		
		ResultSet rs = SafetyCheckHelper.runQuery(" select ?person ?name ?location ?region ?lat ?lon where "
		+ "{ ?person rdf:type sc:Person. ?person sc:hasName ?name . "
		+ "?person sc:hasLocation ?location. OPTIONAL "
		+ "{?person sc:locatedAt ?region. ?region sc:hasLatitude ?lat."
		+ " ?region sc:hasLongitude ?lon.} }",data);
		System.out.println(rs);
		List<Person> persons = new ArrayList<Person>();
		while (rs.hasNext()) {
			QuerySolution soln = rs.nextSolution();
			String name ="";
			String location = "";
			Float latitude = new Float(0);
			Float longitude = new Float(0);
			RDFNode person = soln.get("?person");
			if (person != null) {
				if(null != soln.getLiteral("?name"))
					name = soln.getLiteral("?name").toString();
				if(null != soln.get("?location"))
					location = soln.get("?location").toString();
				if(null != soln.getLiteral("?lat"))
					latitude = soln.getLiteral("?lat").getFloat();
				if(null != soln.getLiteral("?lon"))
					longitude = soln.getLiteral("?lat").getFloat();
					
				
				persons.add(new Person(person.toString(),name, location, latitude,
						longitude));
			}

		}
		return persons;
	}


}
