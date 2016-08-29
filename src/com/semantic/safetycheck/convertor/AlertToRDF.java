package com.semantic.safetycheck.convertor;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.jetty.util.log.Log;

import com.google.publicalerts.cap.Alert;
import com.google.publicalerts.cap.Area;
import com.google.publicalerts.cap.Circle;
import com.google.publicalerts.cap.Info;
import com.google.publicalerts.cap.Point;
import com.google.publicalerts.cap.Polygon;
import com.google.publicalerts.cap.ValuePair;
import com.google.pubsubhubbub.TopicUrl;

public class AlertToRDF {

	private static SimpleDateFormat new_formatter = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private static SimpleDateFormat old_formatter = new SimpleDateFormat(
			"yyyyMMdd'T'HHmmss.SSS'Z'");
	private static SimpleDateFormat iso_format = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXXX");


	public static String convertAlertstoRDF(List<Alert> alerts, String hubtopic) {
		TopicUrl topic = TopicUrl.getTopic(hubtopic);
		StringBuffer rdf = new StringBuffer();
		rdf.append(generateRDFHeader());
		for (Alert alert : alerts){
			try {
				if(TopicUrl.EARTHQUAKE.equals(topic)) {
					rdf.append(convertEarthquakeAlert(alert));
				} else {
					rdf.append(convertWeatherAlert(alert));
				}
			} catch (AlertConversionException e) {
				System.out.println(e.getMessage());
				//Log.warn(e.getCause().getMessage());
			}
		}
		rdf.append(generateRDFFooter());
		return rdf.toString();
	}

	private static String convertEarthquakeAlert(Alert alert) throws AlertConversionException {
		DecimalFormat df = new DecimalFormat("#.00");
		StringBuffer rdf = new StringBuffer();
		StringBuffer points_rdf = new StringBuffer();
		String lat = null, lng = null;
		try {
			rdf.append("\t<rdf:Description rdf:about=\"&sc;earthquake"
					+ alert.getIdentifier() + "\">\n");
			rdf.append("\t\t<rdf:type rdf:resource=\"&sc;Earthquake\"/>\n");

			Info infoItem = alert.getInfo(0);
			if (infoItem != null) {
				Area area = infoItem.getArea(0);
				List<ValuePair> params = infoItem.getParameterList();
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
							points_rdf.append("\t<rdf:Description rdf:about=\"&sc;" + lat + "_" + lng + "\">\n");
							points_rdf.append("\t\t<rdf:type rdf:resource=\"&sc;Point\"/>\n");
							points_rdf.append("\t\t<sc:hasLatitude rdf:datatype=\"&xsd;float\">" + lat + "</sc:hasLatitude>\n");
							points_rdf.append("\t\t<sc:hasLongitude rdf:datatype=\"&xsd;float\">" + lng + "</sc:hasLongitude>\n");
							points_rdf.append("\t</rdf:Description>\n\n");
						}
					}
					rdf.append("\t\t<sc:hasAreaDescription rdf:datatype=\"&xsd;string\">" + area.getAreaDesc() + "</sc:hasAreaDescription>\n");
				}
				rdf.append("\t</rdf:Description>\n\n");
			}
		} catch (Exception e) {
			throw new AlertConversionException("Unable to convert earthquake alert", e.getCause());
		}
		rdf.append(points_rdf);
		return rdf.toString();
	}
	
	private static String convertWeatherAlert(Alert alert) throws AlertConversionException {
		DecimalFormat df = new DecimalFormat("#.00");
		StringBuffer rdf = new StringBuffer();
		StringBuffer points_rdf = new StringBuffer();
		List<Polygon> polygons = null;
		try {
			rdf.append("\t<rdf:Description rdf:about=\"&sc;weather"
					+ alert.getIdentifier() + "\">\n");
			rdf.append("\t\t<rdf:type rdf:resource=\"&sc;Weather\"/>\n");

			Info infoItem = alert.getInfo(0);
			if (infoItem != null) {
				rdf.append("\t\t<sc:hasSeverity rdf:datatype=\"&xsd;string\">" + infoItem.getSeverity() + "</sc:hasSeverity>\n");
				try {
					Date date = iso_format.parse(infoItem.getEffective());
					rdf.append("\t\t<sc:atTime rdf:datatype=\"&xsd;dateTimeStamp\">"
							+ new_formatter.format(date)
							+ "</sc:atTime>\n");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					throw new AlertConversionException(e.getMessage());
				}
				String desc = infoItem.getDescription();
				rdf.append("\t\t<sc:hasDescription rdf:datatype=\"&xsd;string\">"
							+ desc + "</sc:hasDescription>\n");
				Area area = infoItem.getArea(0);
				if(area != null) {
					polygons = area.getPolygonList();
					for(Polygon poly: polygons) {
						for(Point p: poly.getPointList()) {
							String lat = df.format(p.getLatitude());
							String lng = df.format(p.getLongitude());
							rdf.append("\t\t<sc:hasArea rdf:resource=\"&sc;"+ lat + "_"+ lng +"\"/>\n");
							points_rdf.append("\t<rdf:Description rdf:about=\"&sc;" + lat + "_"+ lng + "\">\n");
							points_rdf.append("\t\t<rdf:type rdf:resource=\"&sc;Point\"/>\n");
							points_rdf.append("\t\t<sc:hasLatitude rdf:datatype=\"&xsd;float\">" + lat + "</sc:hasLatitude>\n");
							points_rdf.append("\t\t<sc:hasLongitude rdf:datatype=\"&xsd;float\">" + lng + "</sc:hasLongitude>\n");
							points_rdf.append("\t</rdf:Description>\n\n");
						}
					}
					rdf.append("\t\t<sc:hasAreaDescription rdf:datatype=\"&xsd;string\">" + area.getAreaDesc() + "</sc:hasAreaDescription>\n");
				}
				rdf.append("\t</rdf:Description>\n\n");
			}
		} catch (Exception e) {
			throw new AlertConversionException("Unable to convert weather alert", e.getCause());
		}
		rdf.append(points_rdf);
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
