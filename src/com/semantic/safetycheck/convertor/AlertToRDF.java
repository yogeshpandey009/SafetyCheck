package com.semantic.safetycheck.convertor;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.publicalerts.cap.Alert;
import com.google.publicalerts.cap.Area;
import com.google.publicalerts.cap.Circle;
import com.google.publicalerts.cap.Info;
import com.google.publicalerts.cap.Point;
import com.google.publicalerts.cap.Polygon;
import com.google.publicalerts.cap.ValuePair;

public class AlertToRDF {

	private static SimpleDateFormat new_formatter = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private static SimpleDateFormat old_formatter = new SimpleDateFormat(
			"yyyyMMdd'T'HHmmss.SSS'Z'");

	public static String convertAlertstoRDF(List<Alert> alerts) {
		StringBuffer rdf = new StringBuffer();
		rdf.append(generateRDFHeader());
		for (Alert alert : alerts)
			try{
				rdf.append(convertEarthquakeAlert(alert));
			} catch(Exception e) {
				try {
					rdf.append(convertWeatherAlert(alert));
				} catch(Exception e2) {
					e.printStackTrace();
					e2.printStackTrace();
				}
			}
		rdf.append(generateRDFFooter());
		return rdf.toString();
	}

	private static String convertEarthquakeAlert(Alert alert) {
		DecimalFormat df = new DecimalFormat("#.00");
		StringBuffer rdf = new StringBuffer();
		Info infoItem = alert.getInfo(0);
		String lat = null, lng = null;
		if (infoItem != null) {
			Area area = infoItem.getArea(0);
			List<ValuePair> params = infoItem.getParameterList();
			for (ValuePair param : params) {
				if ("EventIDKey".equals(param.getValueName())) {
					rdf.append("\t<rdf:Description rdf:about=\"&sc;earthquake"
							+ param.getValue() + "\">\n");
					rdf.append("\t\t<rdf:type rdf:resource=\"&sc;Earthquake\"/>\n");
					break;
				}
			}
			for (ValuePair param : params) {
				if ("EventTime".equals(param.getValueName())) {
					try {
						Date date = old_formatter.parse(param.getValue());
						rdf.append("\t\t<sc:atTime rdf:datatype=\"&xsd;dateTimeStamp\">"
								+ new_formatter.format(date)
								+ "</sc:atTime>\n");
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if ("Magnitude".equals(param.getValueName())) {
					rdf.append("\t\t<sc:hasMagnitude rdf:datatype=\"&xsd;float\">" + param.getValue() + "</sc:hasMagnitude>\n");
				}
			}
			String desc = infoItem.getDescription();
			if (desc != null) {
				rdf.append("\t\t<sc:hasDescription rdf:datatype=\"&xsd;string\">"
						+ desc + "</sc:hasDescription>\n");
			}
			if(area != null) {
				Circle c = area.getCircle(0);
				if (c != null) {
					Point p = c.getPoint();
					if (p != null) {
						lat = df.format(p.getLatitude());
						lng = df.format(p.getLongitude());
						rdf.append("\t\t<sc:hasArea rdf:resource=\"&sc;"+ lat + "_"+ lng +"\"/>\n");
					}
				}
				rdf.append("\t\t<sc:hasAreaDescription rdf:datatype=\"&xsd;string\">" + area.getAreaDesc() + "</sc:hasAreaDescription>\n");
			}
			rdf.append("\t</rdf:Description>\n\n");
			if(lat != null && lng != null) {
				rdf.append("\t<rdf:Description rdf:about=\"&sc;" + lat + "_" + lng + "\">\n");
				rdf.append("\t\t<rdf:type rdf:resource=\"&sc;Point\"/>\n");
				rdf.append("\t\t<sc:hasLatitude rdf:datatype=\"&xsd;float\">" + lat + "</sc:hasLatitude>\n");
				rdf.append("\t\t<sc:hasLongitude rdf:datatype=\"&xsd;float\">" + lng + "</sc:hasLongitude>\n");
				rdf.append("\t</rdf:Description>\n\n");				
			}
		}
		return rdf.toString();
	}
	
	private static String convertWeatherAlert(Alert alert) {
		DecimalFormat df = new DecimalFormat("#.00");
		StringBuffer rdf = new StringBuffer();
		Info infoItem = alert.getInfo(0);
		Polygon poly = null;
		if (infoItem != null) {
			Area area = infoItem.getArea(0);
			List<ValuePair> params = infoItem.getParameterList();
			for (ValuePair param : params) {
				if ("EventIDKey".equals(param.getValueName())) {
					rdf.append("\t<rdf:Description rdf:about=\"&sc;weather"
							+ param.getValue() + "\">\n");
					rdf.append("\t\t<rdf:type rdf:resource=\"&sc;Weather\"/>\n");
					break;
				}
			}
			for (ValuePair param : params) {
				if ("EventTime".equals(param.getValueName())) {
					try {
						Date date = old_formatter.parse(param.getValue());
						rdf.append("\t\t<sc:atTime rdf:datatype=\"&xsd;dateTimeStamp\">"
								+ new_formatter.format(date)
								+ "</sc:atTime>\n");
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if ("Magnitude".equals(param.getValueName())) {
					rdf.append("\t\t<sc:hasSeverity rdf:datatype=\"&xsd;float\">" + param.getValue() + "</sc:hasSeverity>\n");
				}
			}
			String desc = infoItem.getDescription();
			if (desc != null) {
				rdf.append("\t\t<sc:hasDescription rdf:datatype=\"&xsd;string\">"
						+ desc + "</sc:hasDescription>\n");
			}
			if(area != null) {
				poly = area.getPolygon(0);
				if(poly != null) {
					for(Point p: poly.getPointList()) {
						String lat = df.format(p.getLatitude());
						String lng = df.format(p.getLongitude());
						rdf.append("\t\t<sc:hasArea rdf:resource=\"&sc;"+ lat + "_"+ lng +"\"/>\n");
					}					
				}
				rdf.append("\t\t<sc:hasAreaDescription rdf:datatype=\"&xsd;string\">" + area.getAreaDesc() + "</sc:hasAreaDescription>\n");
			}
			rdf.append("\t</rdf:Description>\n\n");
			if(poly != null) {
				for(Point p: poly.getPointList()) {
					String lat = df.format(p.getLatitude());
					String lng = df.format(p.getLongitude());
					rdf.append("\t<rdf:Description rdf:about=\"&sc;" + lat + "_"+ lng + "\">\n");
					rdf.append("\t\t<rdf:type rdf:resource=\"&sc;Point\"/>\n");
					rdf.append("\t\t<sc:hasLatitude rdf:datatype=\"&xsd;float\">" + lat + "</sc:hasLatitude>\n");
					rdf.append("\t\t<sc:hasLongitude rdf:datatype=\"&xsd;float\">" + lng + "</sc:hasLongitude>\n");
					rdf.append("\t</rdf:Description>\n\n");
				}
			}
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
		rdf.append("	<!ENTITY foaf \"http://xmlns.com/foaf/0.1/\" >\n");
		rdf.append("]>\n");
		rdf.append("<rdf:RDF\n");
		rdf.append("	xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" \n");
		rdf.append("	xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" \n");
		rdf.append("	xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" \n");
		rdf.append("	xmlns:sc=\"http://www.semanticweb.org/ontologies/2015/10/SafetyCheck#\" \n");
		rdf.append("	xmlns:foaf=\"http://xmlns.com/foaf/0.1/\">\n\n");
		return rdf.toString();
	}

	private static String generateRDFFooter() {
		return "</rdf:RDF>";
	}
}
