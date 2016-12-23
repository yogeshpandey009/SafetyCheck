package com.semantic.safetycheck.app;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;

public class SafetyCheckQueryHelper {

	static String defaultNameSpace = "http://www.semanticweb.org/ontologies/2015/10/SafetyCheck#";

	public static QueryExecution buildQuery(String queryRequest) {

		StringBuffer queryStr = new StringBuffer();

		// Establish Prefixes
		// Set default Name space first
		queryStr.append("PREFIX sc" + ": <" + defaultNameSpace + "> ");
		queryStr.append("PREFIX rdfs" + ": <"
				+ "http://www.w3.org/2000/01/rdf-schema#" + "> ");
		queryStr.append("PREFIX rdf" + ": <"
				+ "http://www.w3.org/1999/02/22-rdf-syntax-ns#" + "> ");
		queryStr.append("PREFIX list: <http://jena.hpl.hp.com/ARQ/list#>");

		// Now add query
		queryStr.append(queryRequest);
		Query query = QueryFactory.create(queryStr.toString());
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://imod.poly.asu.edu:3030/sc/query", query);
		return qexec;
		/*
		ResultSet response = null;
		try {
			response = qexec.execSelect();
		} finally {
			 //qexec.close();
		}
		Dataset dataset = SafetyCheckServlet.store.dataset;
		dataset.begin(ReadWrite.READ) ;
		QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
		ResultSet response = null;
		 try {
		response = qexec.execSelect();
		 } finally {
			 dataset.commit();
			 dataset.end();
		 //qexec.close();
		 }
		List response = new ArrayList();
		try {
			ResultSet rs = qexec.execSelect();
			while (rs.hasNext()) {
				QuerySolution soln = rs.nextSolution();
				Object obj = qsp.process(soln);
				if (obj != null) {
					response.add(obj);
				}

			}
		} finally {
			 qexec.close();
		}
		return response;
		 */
	}

}
