package com.semantic.safetycheck.dao;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.semantic.safetycheck.app.SafetyCheckHelper;
import com.semantic.safetycheck.pojo.Region;

public class RegionDAO {

	public List<Region> getAllRegions(Model data) {

		ResultSet rs = SafetyCheckHelper
				.runQuery(
						" select ?region ?name ?latitude ?longitude"
								+ " where { ?region rdf:type sc:Region. ?region sc:hasLocationName ?name."
								+ " ?region sc:hasLatitude ?latitude . ?region sc:hasLongitude ?longitude. }",
						data);
		List<Region> regions = new ArrayList<Region>();
		while (rs.hasNext()) {
			QuerySolution soln = rs.nextSolution();
			RDFNode region = soln.get("?region");
			if (region != null) {

				regions.add(new Region(region.toString(), soln.getLiteral(
						"?name").getString(), soln.getLiteral("?latitude")
						.getFloat(), soln.getLiteral("?longitude").getFloat()));
			}
		}
		return regions;
	}

}
