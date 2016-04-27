package com.semantic.safetycheck.dao;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.semantic.safetycheck.app.SafetyCheckHelper;
import com.semantic.safetycheck.pojo.Earthquake;

public class EarthquakeDAO {

	public List<Earthquake> getAllEarthquakes() {
		ResultSet rs = SafetyCheckHelper
				.runQuery(" select ?earthquake ?lat ?lon ?mag ?time ?desc where "
						+ "{ ?earthquake rdf:type sc:Earthquake. ?earthquake sc:hasMagnitude ?mag."
						+ " ?earthquake sc:atLongitude ?lon. ?earthquake sc:atLatitude ?lat."
						+ " ?earthquake sc:hasTime ?time. OPTIONAL { ?earthquake sc:hasDesc ?desc } }");
		List<Earthquake> earthquakes = new ArrayList<Earthquake>();
		while (rs.hasNext()) {
			QuerySolution soln = rs.nextSolution();
			Earthquake eq = solnToEarthquake(soln);
			if (eq != null) {
				earthquakes.add(eq);
			}

		}
		return earthquakes;
	}

	public List<Earthquake> getImpactedByEarthquakes(String personId) {
		ResultSet rs = SafetyCheckHelper
				.runQuery("select ?earthquake ?lat ?lon ?mag ?time ?desc where { <"
						+ personId
						+ ">  sc:isImpactedBy ?earthquake."
						+ " ?earthquake rdf:type sc:Earthquake. ?earthquake sc:hasMagnitude ?mag."
						+ " ?earthquake sc:atLongitude ?lon. ?earthquake sc:atLatitude ?lat."
						+ " ?earthquake sc:hasTime ?time. OPTIONAL { ?earthquake sc:atDesc ?desc } }");

		List<Earthquake> earthquakes = new ArrayList<Earthquake>();
		while (rs.hasNext()) {
			QuerySolution soln = rs.nextSolution();
			Earthquake eq = solnToEarthquake(soln);
			if (eq != null) {
				earthquakes.add(eq);
			}

		}
		return earthquakes;
	}

	private Earthquake solnToEarthquake(QuerySolution soln) {
		RDFNode eqId = soln.get("?earthquake");
		if (eqId != null) {
			String id = eqId.toString();
			Float mag = soln.getLiteral("?mag").getFloat();
			Float lat = soln.getLiteral("?lat").getFloat();
			Float lon = soln.getLiteral("?lon").getFloat();
			String time = soln.getLiteral("?time").getString();
			String desc = "";
			Literal descLtr = soln.getLiteral("?desc");
			if(descLtr != null) desc = descLtr.getString();

			return new Earthquake(id, mag, time, lat, lon, desc);
		}
		return null;
	}

}
