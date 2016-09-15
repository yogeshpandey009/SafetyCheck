package com.semantic.safetycheck.convertor;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.semantic.safetycheck.pojo.Earthquake;
import com.semantic.safetycheck.pojo.Point;
import com.semantic.safetycheck.pojo.Weather;

public class RDFGenerator {

	public static void main(String[] args) throws Exception{
		RDFGenerator rGen = new RDFGenerator();
		String str = rGen.createRDF();
		rGen.writeRDF(str);
	}

	public String createRDF() {
		ArrayList<ArrayList<String>> rowCols = CSVManager.loadCSV();
		StringBuilder rdf = new StringBuilder();
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
		StringBuilder rdf = new StringBuilder();
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
		StringBuilder rdf = new StringBuilder();
		String lat = df.format(Double.parseDouble(eq.getLatitude()+""));
		String lng = df.format(Double.parseDouble(eq.getLongitude()+""));
		rdf.append("<?xml version=\"1.0\"?>\n");
		rdf.append("<!DOCTYPE rdf:RDF [\n");
		rdf.append("	<!ENTITY xsd \"http://www.w3.org/2001/XMLSchema#\" >\n");
		rdf.append("	<!ENTITY rdfs \"http://www.w3.org/2000/01/rdf-schema#\" >\n");
		rdf.append("	<!ENTITY rdf \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" >\n");
		rdf.append("	<!ENTITY sc \"http://www.semanticweb.org/ontologies/2015/10/SafetyCheck#\" >\n");
		rdf.append("	<!ENTITY foaf \"http://xmlns.com/foaf/0.1/\" >\n");
		rdf.append("]>\n");              
		rdf.append("<rdf:RDF\n");
		rdf.append("	xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" \n");
		rdf.append("	xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" \n");
		rdf.append("	xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" \n");
		rdf.append("	xmlns:sc=\"http://www.semanticweb.org/ontologies/2015/10/SafetyCheck#\" \n");
		rdf.append("	xmlns:foaf=\"http://xmlns.com/foaf/0.1/\">\n\n");

		rdf.append("	<rdf:Description rdf:about=\"&sc;" + lat + "_" + lng + "\">\n");
		rdf.append("		<rdf:type rdf:resource=\"&sc;Point\"/>\n");
		rdf.append("		<sc:hasLatitude rdf:datatype=\"&xsd;float\">" + lat + "</sc:hasLatitude>\n");
		rdf.append("		<sc:hasLongitude rdf:datatype=\"&xsd;float\">" + lng + "</sc:hasLongitude>\n");
		rdf.append("	</rdf:Description>\n\n");

		rdf.append("	<rdf:Description rdf:about=\"&sc;earthquake" + eq.getId() + "\">\n");
		rdf.append("		<rdf:type rdf:resource=\"&sc;Earthquake\"/>\n");
		rdf.append("		<sc:atTime rdf:datatype=\"&xsd;dateTimeStamp\">" + eq.getTimeAsFormat()  + "</sc:atTime>\n");
		rdf.append("		<sc:hasMagnitude rdf:datatype=\"&xsd;float\">" + eq.getMagnitude() + "</sc:hasMagnitude>\n");
		rdf.append("		<sc:hasDescription rdf:datatype=\"&xsd;string\">" + eq.getDesc() + "</sc:hasDescription>\n");
		rdf.append("		<sc:hasAreaDescription rdf:datatype=\"&xsd;string\">" + eq.getDesc() + "</sc:hasAreaDescription>\n");
		rdf.append("		<sc:hasArea rdf:resource=\"&sc;"+ lat + "_"+ lng +"\"/>\n");
		rdf.append("	</rdf:Description>\n\n");
		rdf.append("</rdf:RDF>");
		return rdf.toString();
	}
	
	public static String singleWeatherRDF(Weather w) {
		StringBuilder rdf = new StringBuilder();
		rdf.append("<?xml version=\"1.0\"?>\n");
		rdf.append("<!DOCTYPE rdf:RDF [\n");
		rdf.append("	<!ENTITY xsd \"http://www.w3.org/2001/XMLSchema#\" >\n");
		rdf.append("	<!ENTITY rdfs \"http://www.w3.org/2000/01/rdf-schema#\" >\n");
		rdf.append("	<!ENTITY rdf \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" >\n");
		rdf.append("	<!ENTITY sc \"http://www.semanticweb.org/ontologies/2015/10/SafetyCheck#\" >\n");
		rdf.append("	<!ENTITY foaf \"http://xmlns.com/foaf/0.1/\" >\n");
		rdf.append("]>\n");              
		rdf.append("<rdf:RDF\n");
		rdf.append("	xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" \n");
		rdf.append("	xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" \n");
		rdf.append("	xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" \n");
		rdf.append("	xmlns:sc=\"http://www.semanticweb.org/ontologies/2015/10/SafetyCheck#\" \n");
		rdf.append("	xmlns:foaf=\"http://xmlns.com/foaf/0.1/\">\n\n");

		StringBuilder points_str = new StringBuilder();
		for(Point p: w.getPoints()) {
			rdf.append("	<rdf:Description rdf:about=\"&sc;" + p.getLatitude() + "_"+ p.getLongitude() + "\">\n");
			rdf.append("		<rdf:type rdf:resource=\"&sc;Point\"/>\n");
			rdf.append("		<sc:hasLatitude rdf:datatype=\"&xsd;float\">" + p.getLatitude() + "</sc:hasLatitude>\n");
			rdf.append("		<sc:hasLongitude rdf:datatype=\"&xsd;float\">" + p.getLongitude()  + "</sc:hasLongitude>\n");
			rdf.append("	</rdf:Description>\n\n");
			points_str.append(p.getLatitude() + "_"+ p.getLongitude()).append(",");
		}
		points_str.setLength(points_str.length()-1);

		rdf.append("	<rdf:Description rdf:about=\"&sc;" + w.getId() + "\">\n");
		rdf.append("		<rdf:type rdf:resource=\"&sc;Weather\"/>\n");
		rdf.append("		<sc:atTime rdf:datatype=\"&xsd;dateTimeStamp\">" + w.getTimeAsFormat()  + "</sc:atTime>\n");
		rdf.append("		<sc:hasSeverity rdf:datatype=\"&xsd;string\">" + w.getSeverity() + "</sc:hasSeverity>\n");
		rdf.append("		<sc:hasArea>");
		rdf.append("			<rdf:Bag>");
		for(Point p: w.getPoints()) {
			rdf.append("			<rdf:li rdf:resource=\"&sc;" + p.getLatitude() + "_"+ p.getLongitude() + "\"/>\n");
		}
		rdf.append("			</rdf:Bag>");
		rdf.append("		</sc:hasArea>");
		rdf.append("		<sc:hasPolygon rdf:datatype=\"&xsd;string\">" + points_str.toString() + "</sc:hasPolygon>\n");
		rdf.append("		<sc:hasDescription rdf:datatype=\"&xsd;string\">" + w.getDesc() + "</sc:hasDescription>\n");
		rdf.append("		<sc:hasAreaDescription rdf:datatype=\"&xsd;string\">" + w.getDesc() + "</sc:hasAreaDescription>\n");
		rdf.append("	</rdf:Description>\n\n");

		rdf.append("</rdf:RDF>");
		return rdf.toString();
	}
	
	private static String populateEarthquakes(List<Earthquake> eqList) {
		DecimalFormat df = new DecimalFormat("#.00"); 
		StringBuilder rdf = new StringBuilder();
		for(Earthquake eq: eqList) {
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
		}
		return rdf.toString();
	}
	
	private void writeRDF(String str)throws Exception {
		PrintWriter out = new PrintWriter("./resources/earthquakes.rdf");
		out.println(str);
		out.close();
		System.out.println("Done");
	}

}
