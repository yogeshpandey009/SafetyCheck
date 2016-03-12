package com.semantic.safetycheck.client.weather;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;

import com.semantic.javaobjectstordf.JavaObjectRdfSerializer;
import com.semantic.safetycheck.model.v1.Alert;
import com.semantic.safetycheck.model.v1.Alert.Info;
import com.semantic.safetycheck.model.v1.Alert.Info.Area;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLinkImpl;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
//import com.google.publicalerts.cap.Alert;
//import com.google.publicalerts.cap.Info;

public class WeatherClient {

	public List<Alert> fetchCapAlerts(String s_url) {
		List<Alert> alerts = new ArrayList<Alert>();

		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance("com.semantic.safetycheck.model.v1");
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			//Marshaller marshaller = jaxbContext.createMarshaller();
			//marshaller.setProperty("eclipselink.media-type", "application/json");
			//marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			URL url = new URL(s_url);
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(url));
			@SuppressWarnings("unchecked")
			List<SyndEntry> entries = feed.getEntries();
			System.out.println(feed.getTitle());
			for (SyndEntry entry : entries) {
				System.out.println(entry.getTitle());
				List<SyndLinkImpl> links = entry.getLinks();
				String capLink = "";
				for (SyndLinkImpl link : links) {
					if (StringUtils.containsIgnoreCase(link.getHref(), "cap")) {
						capLink = link.getHref();
						break;
					}
				}

				if (!capLink.isEmpty()) {
					URL capUrl = new URL(capLink);
					BufferedReader capBr = new BufferedReader(
							new InputStreamReader(capUrl.openStream()));
					/*
					 * StringBuilder sb = new StringBuilder(); String line =
					 * capBr.readLine(); while (line != null) { sb.append(line);
					 * line = capBr.readLine(); }
					 */
					// CapXmlParser parser = new CapXmlParser(false);
					// Alert alert = parser.parseFrom(sb.toString());
					Alert alert = (Alert) unmarshaller.unmarshal(capBr);
					System.out.println(alert.getStatus() + ":"
							+ alert.getScope() + ":" + alert.getMsgType());
					List<Info> infoList = alert.getInfo();
					for (Info infoItem : infoList) {
						System.out.println("\t" + infoItem.getHeadline());
						for (String c : infoItem.getCategory()) {
							System.out.print("\t\tCategory: " + c);
						}
						System.out.println();
						System.out.println("\t" + infoItem.getSeverity() + ":"
								+ infoItem.getUrgency());
						System.out.println("\t" + infoItem.getEvent());
						for (Area a : infoItem.getArea()) {
							System.out.print("\t\tArea: " + a.getAreaDesc());
							//marshaller.marshal(a, System.out);
						}
					}
					alerts.add(alert);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return alerts;
	}

	public static void main(String[] args) {
		WeatherClient pc = new WeatherClient();
		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream("weather.xml");
			List<Alert> alerts = pc.fetchCapAlerts("http://alerts.weather.gov/cap/ga.php?x=0");
			JavaObjectRdfSerializer serializer = new JavaObjectRdfSerializer(
					"http://www.weather.gov/weather#",
					"http://www.weather.gov/ontology#", "RDF/XML",
					"com.semantic.safetycheck");

			Collection<Object> objects = new LinkedList<Object>();
			objects.addAll(alerts);
			serializer.serialize(objects, outputStream);
			outputStream.close();
			System.out.println("Success.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
