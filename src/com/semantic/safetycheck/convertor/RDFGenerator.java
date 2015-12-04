package com.semantic.safetycheck.convertor;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.semantic.safetycheck.pojo.Earthquake;

public class RDFGenerator {

	public static void main(String[] args) throws Exception{
		RDFGenerator rGen = new RDFGenerator();
		String str = rGen.createRDF();
		rGen.writeRDF(str);
	}

	public String createRDF() {
		ArrayList<ArrayList<String>> rowCols = CSVManager.loadCSV();
		StringBuffer rdf = new StringBuffer(100);
		rdf.append("<?xml version=\"1.0\"?>\n");
		rdf.append("<!DOCTYPE rdf:RDF [\n");
		rdf.append("	<!ENTITY xsd \"http://www.w3.org/2001/XMLSchema#\" >\n");
		rdf.append("	<!ENTITY rdfs \"http://www.w3.org/2000/01/rdf-schema#\" >\n");
		rdf.append("	<!ENTITY rdf \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" >\n");
		rdf.append("	<!ENTITY sc \"http://www.semanticweb.org/ontologies/2015/10/SafetyCheck#\" >\n");
		rdf.append("]>\n");              
		rdf.append("<rdf:RDF\n");
		rdf.append("	xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" \n");
		rdf.append("	xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" \n");
		rdf.append("	xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" \n");
		rdf.append("	xmlns:sc=\"http://www.semanticweb.org/ontologies/2015/10/SafetyCheck#\">\n\n");

		rdf.append(populateEarthquake(rowCols));

		rdf.append("</rdf:RDF>");

		// System.out.println(rdf.toString());
		return rdf.toString();
	}

	/**
	 * <NamedIndividual rdf:about="&sc;earthquake1"> <rdf:type rdf:resource=
	 * "&sc;StrongEarthquake"/> <sc:hasMagnitude rdf:datatype="&xsd;float">4.5
	 * </sc:hasMagnitude>
	 * <sc:atLatitude rdf:datatype="&xsd;float">80.0</sc:atLatitude>
	 * <sc:atLongitude rdf:datatype="&xsd;float">90.0</sc:atLongitude>
	 * </NamedIndividual>
	 * 
	 * @param rowCols
	 * @return
	 */
	private String populateEarthquake(ArrayList<ArrayList<String>> rowCols) {
		DecimalFormat df = new DecimalFormat("#.00"); 
		StringBuffer rdf = new StringBuffer(100);
		for (int x = 1; x < rowCols.size(); x++) {
			ArrayList<String> row = rowCols.get(x);
			rdf.append("	<rdf:Description rdf:about=\"&sc;earthquake" + x + "\">\n");
			double mag = Double.parseDouble(row.get(4));
			if (mag > 4) {
				rdf.append("		<rdf:type rdf:resource=\"&sc;StrongEarthquake\"/>\n");
			} else {
				rdf.append("		<rdf:type rdf:resource=\"&sc;WeakEarthquake\"/>\n");
			}
			rdf.append("		<sc:hasTime rdf:datatype=\"&xsd;string\">" + row.get(0)  + "</sc:hasTime>\n");
			rdf.append("		<sc:hasMagnitude rdf:datatype=\"&xsd;float\">" + mag + "</sc:hasMagnitude>\n");
			rdf.append("		<sc:atLatitude rdf:datatype=\"&xsd;float\">" + df.format(Double.parseDouble(row.get(1))) + "</sc:atLatitude>\n");
			rdf.append("		<sc:atLongitude rdf:datatype=\"&xsd;float\">" + df.format(Double.parseDouble(row.get(2))) + "</sc:atLongitude>\n");
			rdf.append("	</rdf:Description>\n\n");

		}

		return rdf.toString();
	}
	
	public static String singleEarthquakeRDF(Earthquake eq) {
		DecimalFormat df = new DecimalFormat("#.00"); 
		StringBuffer rdf = new StringBuffer(100);
		rdf.append("<?xml version=\"1.0\"?>\n");
		rdf.append("<!DOCTYPE rdf:RDF [\n");
		rdf.append("	<!ENTITY xsd \"http://www.w3.org/2001/XMLSchema#\" >\n");
		rdf.append("	<!ENTITY rdfs \"http://www.w3.org/2000/01/rdf-schema#\" >\n");
		rdf.append("	<!ENTITY rdf \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" >\n");
		rdf.append("	<!ENTITY sc \"http://www.semanticweb.org/ontologies/2015/10/SafetyCheck#\" >\n");
		rdf.append("]>\n");              
		rdf.append("<rdf:RDF\n");
		rdf.append("	xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" \n");
		rdf.append("	xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" \n");
		rdf.append("	xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" \n");
		rdf.append("	xmlns:sc=\"http://www.semanticweb.org/ontologies/2015/10/SafetyCheck#\">\n\n");

		rdf.append("	<rdf:Description rdf:about=\"&sc;earthquake" + eq.getId() + "\">\n");
		double mag = Double.parseDouble(eq.getMagnitude()+"");
		if (mag > 4) {
			rdf.append("		<rdf:type rdf:resource=\"&sc;StrongEarthquake\"/>\n");
		} else {
			rdf.append("		<rdf:type rdf:resource=\"&sc;WeakEarthquake\"/>\n");
		}
		rdf.append("		<sc:hasTime rdf:datatype=\"&xsd;string\">" + eq.getTimeAsFormat()  + "</sc:hasTime>\n");
		rdf.append("		<sc:hasMagnitude rdf:datatype=\"&xsd;float\">" + mag + "</sc:hasMagnitude>\n");
		rdf.append("		<sc:atLatitude rdf:datatype=\"&xsd;float\">" + df.format(Double.parseDouble(eq.getLatitude()+"")) + "</sc:atLatitude>\n");
		rdf.append("		<sc:atLongitude rdf:datatype=\"&xsd;float\">" + df.format(Double.parseDouble(eq.getLongitude()+"")) + "</sc:atLongitude>\n");
		rdf.append("	</rdf:Description>\n\n");

		rdf.append("</rdf:RDF>");
		return rdf.toString();
	}
	
	private void writeRDF(String str)throws Exception {
		PrintWriter out = new PrintWriter("./resources/earthquakes.rdf");
		out.println(str);
		out.close();
		System.out.println("Done");
	}

}
