package com.semantic.safetycheck.convertor;

import java.text.DecimalFormat;
import java.util.List;

import com.google.publicalerts.cap.Alert;
import com.google.publicalerts.cap.Area;
import com.google.publicalerts.cap.Circle;
import com.google.publicalerts.cap.Info;
import com.google.publicalerts.cap.Point;
import com.google.publicalerts.cap.ValuePair;

public class AlertToRDF {

	//private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	public static String EarthquakeAlertstoRDF(List<Alert> alerts) {
		StringBuffer rdf = new StringBuffer();
		rdf.append(generateRDFHeader());
		for(Alert alert: alerts)
			rdf.append(convertEarthquakeAlert(alert));
		rdf.append(generateRDFFooter());
		return rdf.toString();
	}
	
	private static String convertEarthquakeAlert(Alert alert) {
		DecimalFormat df = new DecimalFormat("#.00"); 
		StringBuffer rdf = new StringBuffer();
		Info infoItem = alert.getInfo(0);
		if(infoItem!=null) {
			List<ValuePair> params = infoItem.getParameterList();
			for(ValuePair param: params) {
				if("EventIDKey".equals(param.getValueName())) {
					rdf.append("\t\t<rdf:Description rdf:about=\"&sc;earthquake" + param.getValue() + "\">\n");					
				}
				if("EventTime".equals(param.getValueName())) {
					rdf.append("\t\t<sc:hasTime rdf:datatype=\"&xsd;string\">" + param.getValue() + "</sc:hasTime>\n");
				}
				if("Magnitude".equals(param.getValueName())) {
					rdf.append("\t\t<sc:hasMagnitude rdf:datatype=\"&xsd;float\">" + param.getValue() + "</sc:hasMagnitude>\n");
				}
			}
			Area area = infoItem.getArea(0);
			if(area != null) {
				Circle c = area.getCircle(0);
				if(c != null) {
					Point p = c.getPoint();
					if(p != null) {
						rdf.append("\t\t<sc:atLatitude rdf:datatype=\"&xsd;float\">" + df.format(p.getLatitude() + "") + "</sc:atLatitude>\n");
						rdf.append("\t\t<sc:atLongitude rdf:datatype=\"&xsd;float\">" + df.format(p.getLongitude() + "") + "</sc:atLongitude>\n");
					}
				}
			}
			rdf.append("\t\t</rdf:Description>\n\n");
		}
		return rdf.toString();
	}
	
	private static String generateRDFHeader() {
		StringBuffer rdf = new StringBuffer();
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
		return rdf.toString();
	}
	
	private static String generateRDFFooter() {
		return "</rdf:RDF>";
	}
}
