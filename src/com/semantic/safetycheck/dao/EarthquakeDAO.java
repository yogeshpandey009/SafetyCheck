package com.semantic.safetycheck.dao;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.semantic.safetycheck.app.SafetyCheckQueryHelper;
import com.semantic.safetycheck.pojo.Earthquake;

public class EarthquakeDAO {

	public List<Earthquake> getAllEarthquakes() {
		QueryExecution qexec = SafetyCheckQueryHelper
				.buildQuery(" select ?earthquake ?point ?lat ?lon ?mag ?time ?desc ?areaDesc where "
						+ "{ ?earthquake rdf:type sc:Earthquake. ?earthquake sc:hasMagnitude ?mag."
						+ " ?earthquake sc:hasAreaDescription ?areaDesc. ?earthquake sc:hasArea ?point."
						+ " ?point sc:hasLongitude ?lon. ?point sc:hasLatitude ?lat."
						+ " ?earthquake sc:atTime ?time. ?earthquake sc:hasDescription ?desc }"
						+ " ORDER BY DESC(?time)"
						+ " LIMIT 500");

		List<Earthquake> result = computeEarthquakeResultSet(qexec.execSelect());
		qexec.close();
		return result;
	}

	public List<Earthquake> getImpactedByEarthquakes(String personId) {
		QueryExecution qexec = SafetyCheckQueryHelper
				.buildQuery("select ?earthquake ?point ?lat ?lon ?mag ?time ?desc ?areaDesc where { <"
						+ personId
						+ ">  sc:isImpactedBy ?earthquake."
						+ " ?earthquake rdf:type sc:Earthquake. ?earthquake sc:hasMagnitude ?mag."
						+ " ?earthquake sc:hasAreaDescription ?areaDesc. ?earthquake sc:hasArea ?point."
						+ " ?point sc:hasLongitude ?lon. ?point sc:hasLatitude ?lat."
						+ " ?earthquake sc:atTime ?time. ?earthquake sc:hasDescription ?desc }");

		List<Earthquake> result = computeEarthquakeResultSet(qexec.execSelect());
		qexec.close();
		return result;
	}

	private List<Earthquake> computeEarthquakeResultSet(ResultSet rs) {
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
			Float mag = null;
			String time = null;
			Float lat = null;
			Float lon = null;
			String desc = null;

			Literal magLtr = soln.getLiteral("?mag");
			if (magLtr != null)
				mag = magLtr.getFloat();

			Literal latLtr = soln.getLiteral("?lat");
			if (latLtr != null)
				lat = latLtr.getFloat();

			Literal lonLtr = soln.getLiteral("?lon");
			if (lonLtr != null)
				lon = lonLtr.getFloat();

			Literal timeLtr = soln.getLiteral("?time");
			if (timeLtr != null)
				time = timeLtr.getString();

			Literal descLtr = soln.getLiteral("?desc");
			if (descLtr != null)
				desc = descLtr.getString();

			return new Earthquake(id, mag, time, lat, lon, desc);
		}
		return null;
	}
}
