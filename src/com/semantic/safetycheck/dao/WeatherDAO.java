package com.semantic.safetycheck.dao;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.semantic.safetycheck.app.SafetyCheckQueryHelper;
import com.semantic.safetycheck.pojo.Point;
import com.semantic.safetycheck.pojo.Weather;

public class WeatherDAO {

	/*
	public List<Weather> getAllWeatherAlerts() {
		ResultSet rs = SafetyCheckQueryHelper
				.runQuery(" select ?weather ?areaDesc ?sev ?time ?desc"
						+ " (GROUP_CONCAT(?lat) AS ?lats)"
						+ " (GROUP_CONCAT(?lon) AS ?lons) where"
						+ " { ?weather rdf:type sc:Weather. ?weather sc:hasSeverity ?sev."
						+ " ?weather sc:hasAreaDescription ?areaDesc. ?weather sc:hasArea ?area."
						+ " ?area sc:hasLongitude ?lon. ?area sc:hasLatitude ?lat."
						+ " ?weather sc:atTime ?time. ?weather sc:hasDescription ?desc }"
						+ " GROUP BY ?weather ?areaDesc ?sev ?time ?desc");

		return computeWeatherResultSet(rs);
	}
	*/
	public List<Weather> getAllWeatherAlerts() {
		QueryExecution qexec = SafetyCheckQueryHelper
				.buildQuery(" select ?weather ?areaDesc ?sev ?time ?desc"
						+ " (GROUP_CONCAT(?lat) AS ?lats)"
						+ " (GROUP_CONCAT(?lon) AS ?lons) where"
						+ " { ?weather rdf:type sc:Weather. ?weather sc:hasSeverity ?sev."
						+ " ?weather sc:hasAreaDescription ?areaDesc. ?weather sc:hasArea ?area."
						//+ " ?area ?t ?statement."
						//+ " ?statement a ?type."
						//+ " ?area rdf:rest* [ rdf:first ?point ] ."
						+ " ?area rdfs:member ?point."
						+ " ?point sc:hasLongitude ?lon. ?point sc:hasLatitude ?lat."
						+ " ?weather sc:atTime ?time. ?weather sc:hasDescription ?desc }"
						+ " GROUP BY ?weather ?areaDesc ?sev ?time ?desc");
						//+ " order by ?weather ?area");
		List<Weather> result = computeWeatherResultSet(qexec.execSelect());
		qexec.close();
		return result;
	}

	public List<Weather> getImpactedByWeatherAlerts(String personId) {
		QueryExecution qexec = SafetyCheckQueryHelper
				.buildQuery("select ?weather ?areaDesc ?sev ?time ?desc"
						+ " (GROUP_CONCAT(?lat) AS ?lats)"
						+ " (GROUP_CONCAT(?lon) AS ?lons) where { <"
						+ personId
						+ ">  sc:isImpactedBy ?weather."
						+ "{ ?weather rdf:type sc:Weather. ?weather sc:hasSeverity ?sev."
						+ " ?weather sc:hasAreaDescription ?areaDesc. ?weather sc:hasArea ?area. "
						+ " ?area rdfs:member ?point."
						+ " ?point sc:hasLongitude ?lon. ?point sc:hasLatitude ?lat."
						+ " ?weather sc:atTime ?time. ?weather sc:hasDescription ?desc }"
						+ " GROUP BY ?weather ?areaDesc ?sev ?time ?desc");

		List<Weather> result = computeWeatherResultSet(qexec.execSelect());
		qexec.close();
		return result;
	}

	private List<Weather> computeWeatherResultSet(ResultSet rs) {
		//Map<String, Weather> weatherAlerts = new HashMap<>();
		ArrayList<Weather> weatherAlerts = new ArrayList<Weather>();
		while (rs.hasNext()) {
			QuerySolution soln = rs.nextSolution();
			Weather	w = solnToWeather(soln);
			if(w != null)
				weatherAlerts.add(w);
		}
		return weatherAlerts;
	}

	private Weather solnToWeather(QuerySolution soln) {
		if (soln != null) {
			String sev = null;
			String time = null;
			List<Point> points = new ArrayList<>();
			String desc = null;
			String areaDesc = null;

			RDFNode weather = soln.get("?weather");
			if(weather != null) {
				String wId = weather.toString();

				Literal sevLtr = soln.getLiteral("?sev");
				if (sevLtr != null)
					sev = sevLtr.getString();

				Literal timeLtr = soln.getLiteral("?time");
				if (timeLtr != null)
					time = timeLtr.getString();

				//Resource areaRes = soln.getResource("?area");
				//RDFNode areaNode = soln.get("?area");
				//List<RDFNode> areaList = soln.getResource("?area").asResource().as(RDFList.class).asJavaList();
				//ExtendedIterator<RDFNode> items = rdfList.iterator();
	            //while ( items.hasNext() ) {
	            //	Resource item = items.next().asResource();
	            //}
				Literal latLtr = soln.getLiteral("?lats");
				Literal lonLtr = soln.getLiteral("?lons");

				if(latLtr != null && lonLtr != null) {
					String latsStr = latLtr.getString();
					String lonStr = lonLtr.getString();
					String[] lats = latsStr.split(" ");
					String[] lons = lonStr.split(" ");

					int i = 0;
					while(i < lats.length && i < lons.length) {
						Point p = new Point(Float.parseFloat(lats[i]), Float.parseFloat(lons[i]));
						points.add(p);
						i++;
					}
				}

				Literal descLtr = soln.getLiteral("?desc");
				if (descLtr != null)
					desc = descLtr.getString();

				Literal areaDescLtr = soln.getLiteral("?areaDesc");
				if (areaDescLtr != null)
					areaDesc = areaDescLtr.getString();

				return new Weather(wId, sev, time, points, desc, areaDesc);
			}
		}
		return null;
	}
}
