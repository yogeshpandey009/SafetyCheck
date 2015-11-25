package com.sematic.safetycheck.dao;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.semantic.safetycheck.app.SafetyCheck;
import com.semantic.safetycheck.pojo.Earthquake;

public class EarthquakeDAO {
	
	public List<Earthquake> getAllEarthquakes() {
		Model data = SafetyCheck.populateData();
		ResultSet rs = SafetyCheck.runQuery(
				" select ?earthquake ?magnitude ?latitude ?longitude where { ?earthquake rdf:type sc:Earthquake. ?earthquake sc:hasMagnitude ?magnitude . ?earthquake sc:atLongitude ?longitude . ?earthquake sc:atLatitude ?latitude. }",
				data); // add the query string
		List<Earthquake> earthquakes = new ArrayList<Earthquake>();
		while (rs.hasNext()) {
			QuerySolution soln = rs.nextSolution();
			RDFNode earthquake = soln.get("?earthquake");
			if (earthquake != null) {
				earthquakes.add(new Earthquake(earthquake.toString(), soln.getLiteral("?magnitude").getFloat(), "",
						soln.getLiteral("?latitude").getFloat(), soln.getLiteral("?longitude").getFloat()));

			}

		}
		return null;
	}


	
}
