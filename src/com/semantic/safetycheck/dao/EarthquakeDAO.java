package com.semantic.safetycheck.dao;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.semantic.safetycheck.app.SafetyCheckHelper;
import com.semantic.safetycheck.pojo.Earthquake;

public class EarthquakeDAO {

	public List<Earthquake> getAllEarthquakes() {
		ResultSet rs = SafetyCheckHelper
				.runQuery(
						" select ?earthquake ?magnitude ?latitude ?longitude ?time where { ?earthquake rdf:type sc:Earthquake. ?earthquake sc:hasMagnitude ?magnitude . ?earthquake sc:atLongitude ?longitude . ?earthquake sc:atLatitude ?latitude. ?earthquake sc:hasTime ?time }"); // add the query string
		List<Earthquake> earthquakes = new ArrayList<Earthquake>();
		while (rs.hasNext()) {
			QuerySolution soln = rs.nextSolution();
			RDFNode earthquake = soln.get("?earthquake");
			if (earthquake != null) {
				earthquakes.add(new Earthquake(earthquake.toString(), soln
						.getLiteral("?magnitude").getFloat(), soln.getLiteral(
						"?time").getString(), soln.getLiteral("?latitude")
						.getFloat(), soln.getLiteral("?longitude").getFloat()));

			}

		}
		return earthquakes;
	}

	public List<Earthquake> getImpactedByEarthquakes(String personId) {
		ResultSet rs = SafetyCheckHelper
				.runQuery(
						"select ?earthquake ?lat ?lon ?mag ?time"
								+ " where { <" + personId + ">  sc:isImpactedBy ?earthquake."
								+ " ?earthquake rdf:type sc:Earthquake. ?earthquake sc:hasMagnitude ?mag."
								+ " ?earthquake sc:atLongitude ?lon. ?earthquake sc:atLatitude ?lat."
								+ " ?earthquake sc:hasTime ?time }"); // add the query string
		List<Earthquake> earthquakes = new ArrayList<Earthquake>();
		while (rs.hasNext()) {
			QuerySolution soln = rs.nextSolution();
			RDFNode earthquake = soln.get("?earthquake");
			if (earthquake != null) {
				earthquakes.add(new Earthquake(earthquake.toString(), soln
						.getLiteral("?mag").getFloat(), soln.getLiteral("?time").getString(), soln.getLiteral("?lat")
						.getFloat(), soln.getLiteral("?lon").getFloat()));
			}

		}
		return earthquakes;
	}

}
