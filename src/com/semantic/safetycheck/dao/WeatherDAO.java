package com.semantic.safetycheck.dao;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.semantic.safetycheck.app.SafetyCheckQueryHelper;
import com.semantic.safetycheck.pojo.Point;
import com.semantic.safetycheck.pojo.Weather;

public class WeatherDAO {

	public List<Weather> getAllWeatherAlerts() {
		ResultSet rs = SafetyCheckQueryHelper
				.runQuery(" select ?weather ?area ?areaDesc ?sev ?time ?desc where "
						+ "{ ?weather rdf:type sc:Weather. OPTIONAL { ?weather sc:hasSeverity ?sev."
						+ " ?weather sc:hasArea ?area. ?weather sc:hasAreaDescription ?areaDesc."
						+ " ?weather sc:hasTime ?time. ?weather sc:hasDescription ?desc } }");
		List<Weather> weatherAlerts = new ArrayList<Weather>();
		while (rs.hasNext()) {
			QuerySolution soln = rs.nextSolution();
			Weather eq = solnToWeather(soln);
			if (eq != null) {
				weatherAlerts.add(eq);
			}
		}
		return weatherAlerts;
	}

	public List<Weather> getImpactedByWeatherAlerts(String personId) {
		ResultSet rs = SafetyCheckQueryHelper
				.runQuery("select ?weather ?area ?areaDesc ?sev ?time ?desc where { <"
						+ personId
						+ ">  sc:isImpactedBy ?weather."
						+ "{ ?weather rdf:type sc:Weather. OPTIONAL { ?weather sc:hasSeverity ?sev."
						+ " ?weather sc:hasArea ?area. ?weather sc:hasAreaDescription ?areaDesc."
						+ " ?weather sc:hasTime ?time. ?weather sc:hasDescription ?desc } }");

		List<Weather> weatherAlerts = new ArrayList<Weather>();
		while (rs.hasNext()) {
			QuerySolution soln = rs.nextSolution();
			Weather eq = solnToWeather(soln);
			if (eq != null) {
				weatherAlerts.add(eq);
			}

		}
		return weatherAlerts;
	}

	private Weather solnToWeather(QuerySolution soln) {
		RDFNode eqId = soln.get("?weather");
		if (eqId != null) {
			String id = eqId.toString();
			 String sev = null;
			 String time = null;
			 List<Point> points = null;
			 String desc = null;
			 String areaDesc = null;

			Literal sevLtr = soln.getLiteral("?sev");
			if(sevLtr != null) sev = sevLtr.getString();

			StmtIterator it = soln.getResource("?area").listProperties();
		    while( it.hasNext() ) {
		      Statement stmt = it.nextStatement();
		      System.out.println( "   * "+stmt );
		    }
			Literal timeLtr = soln.getLiteral("?time");
			if(timeLtr != null) time = timeLtr.getString();

			Literal descLtr = soln.getLiteral("?desc");
			if(descLtr != null) desc = descLtr.getString();

			Literal areaDescLtr = soln.getLiteral("?areaDesc");
			if(areaDescLtr != null) areaDesc = areaDescLtr.getString();

			return new Weather(id, sev, time, points, desc, areaDesc);
		}
		return null;
	}

}
