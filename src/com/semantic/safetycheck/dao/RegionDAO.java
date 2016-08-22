package com.semantic.safetycheck.dao;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.semantic.safetycheck.app.SafetyCheckQueryHelper;
import com.semantic.safetycheck.pojo.Region;

public class RegionDAO {

	public List<Region> getAllRegions() {

		ResultSet rs = SafetyCheckQueryHelper
				.runQuery(
						" select ?region ?name ?point ?latitude ?longitude ?population"
								+ " where { ?region rdf:type sc:Region. ?region sc:hasRegionName ?name."
								+ " ?region sc:hasPoint ?point. ?region sc:hasPopulation ?population."
								+ " ?point sc:hasLatitude ?latitude . ?point sc:hasLongitude ?longitude. }");
		List<Region> regions = new ArrayList<Region>();
		while (rs.hasNext()) {
			QuerySolution soln = rs.nextSolution();
			RDFNode region = soln.get("?region");
			if (region != null) {
				regions.add(new Region(region.toString(), soln.getLiteral(
						"?name").getString(), soln.getLiteral("?latitude")
						.getFloat(), soln.getLiteral("?longitude").getFloat(),
						soln.getLiteral("?population").getInt()));
			}
		}
		return regions;
	}

}
