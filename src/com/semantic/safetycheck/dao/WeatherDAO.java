package com.semantic.safetycheck.dao;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.semantic.safetycheck.app.SafetyCheckQueryHelper;
import com.semantic.safetycheck.pojo.Point;
import com.semantic.safetycheck.pojo.Weather;

public class WeatherDAO {

	public List<Weather> getAllWeatherAlerts() {
		ResultSet rs = SafetyCheckQueryHelper
				.runQuery(" select ?weather ?areaDesc ?sev ?time ?desc"
						+ " (GROUP_CONCAT(?lat) AS ?lats)"
						+ " (GROUP_CONCAT(?lon) AS ?lons) where"
						+ " { ?weather rdf:type sc:Weather. OPTIONAL { ?weather sc:hasSeverity ?sev."
						+ " ?weather sc:hasAreaDescription ?areaDesc. ?weather sc:hasArea ?area."
						+ " ?area sc:hasLongitude ?lon. ?area sc:hasLatitude ?lat."
						+ " ?weather sc:atTime ?time. ?weather sc:hasDescription ?desc } }"
						+ " GROUP BY ?weather ?areaDesc ?sev ?time ?desc");

		return computeWeatherResultSet(rs);
	}

	public List<Weather> getImpactedByWeatherAlerts(String personId) {
		ResultSet rs = SafetyCheckQueryHelper
				.runQuery("select ?weather ?areaDesc ?sev ?time ?desc"
						+ " (GROUP_CONCAT(?lat) AS ?lats)"
						+ " (GROUP_CONCAT(?lon) AS ?lons) where { <"
						+ personId
						+ ">  sc:isImpactedBy ?weather."
						+ "{ ?weather rdf:type sc:Weather. OPTIONAL { ?weather sc:hasSeverity ?sev."
						+ " ?weather sc:hasAreaDescription ?areaDesc. ?weather sc:hasArea ?area. "
						+ " ?area sc:hasLongitude ?lon. ?area sc:hasLatitude ?lat. "
						+ " ?weather sc:atTime ?time. ?weather sc:hasDescription ?desc } }"
						+ " GROUP BY ?weather ?areaDesc ?sev ?time ?desc");

		return computeWeatherResultSet(rs);
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

				Literal latLtr = soln.getLiteral("?lats");
				Literal lonLtr = soln.getLiteral("?lons");

				String latsStr = latLtr.getString();
				String lonStr = lonLtr.getString();
				if(latsStr != null && lonStr != null) {
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
