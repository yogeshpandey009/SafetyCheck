package com.semantic.safetycheck.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.publicalerts.cap.Alert;
import com.google.publicalerts.cap.CapValidator;
import com.google.publicalerts.cap.CapXmlBuilder;
import com.google.publicalerts.cap.CapXmlParser;
import com.google.publicalerts.cap.XmlSignatureValidator;
import com.google.publicalerts.cap.XmlSigner;
import com.google.common.collect.Sets;
import com.google.publicalerts.cap.Area;
import com.google.publicalerts.cap.Circle;
import com.google.publicalerts.cap.Group;
import com.google.publicalerts.cap.Info;
import com.google.publicalerts.cap.NotCapException;
import com.google.publicalerts.cap.Point;
import com.google.publicalerts.cap.Polygon;
import com.google.publicalerts.cap.Resource;
import com.google.publicalerts.cap.TrustStrategy;
import com.google.publicalerts.cap.ValuePair;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLinkImpl;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class EarthquakeSubscriber {

	public void testEndToEnd() throws Exception {
		// Generate a CAP document
		Alert alert = getValidAlertBuilder().build();

		// Write it out to XML
		CapXmlBuilder builder = new CapXmlBuilder();
		String xml = builder.toXml(alert);

		// Sign it
		XmlSigner signer = XmlSigner.newInstanceWithRandomKeyPair();
		String signedXml = signer.sign(xml);

		// Validate the signature
		XmlSignatureValidator signatureValidator = new XmlSignatureValidator(
				new MockTrustStrategy().setAllowMissingSignatures(false));
		// assertTrue(signatureValidator.validate(signedXml).isSignatureValid());

		// Parse it, with validation
		CapXmlParser parser = new CapXmlParser(true);
		Alert parsedAlert = parser.parseFrom(signedXml);

		// Assert lossless
		// assertEquals(alert, parsedAlert);

	}
	private static boolean noFetch = false;
	public static void main(String[] args) {
		try {
			URL url = new URL("http://alerts.weather.gov/cap/ga.php?x=0");
			// URL url = new
			// URL("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/4.5_week.atom");
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(url));
			@SuppressWarnings("unchecked")
			List<SyndEntry> entries = feed.getEntries();
			System.out.println(feed.getTitle());
			List<Alert> alerts = new ArrayList<Alert>();
			for (SyndEntry entry : entries) {
				System.out.println(entry.getTitle());
				if (noFetch)
					continue;
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
					StringBuilder sb = new StringBuilder();
					String line = capBr.readLine();
					while (line != null) {
						sb.append(line);
						line = capBr.readLine();
					}
					CapXmlParser parser = new CapXmlParser(false);
					Alert alert = parser.parseFrom(sb.toString());
					System.out.println(alert.getStatus() + ":"
							+ alert.getScope() + ":" + alert.getMsgType());
					List<Info> infoList = alert.getInfoList();
					for (Info infoItem : infoList) {
						System.out.println("\t" + infoItem.getHeadline());
						for (int i = 0; i < infoItem.getCategoryCount(); i++) {
							System.out.print("\t\tCategory: "
									+ infoItem.getCategory(i));
						}
						System.out.println();
						System.out.println("\t" + infoItem.getSeverity() + ":"
								+ infoItem.getUrgency());
						System.out.println("\t" + infoItem.getEvent());
						for (int i = 0; i < infoItem.getAreaCount(); i++) {
							System.out.println("\t\tArea affected: "
									+ infoItem.getArea(i));
						}
					}
					alerts.add(alert);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Alert.Builder getValidAlertBuilder() {
		return Alert
				.newBuilder()
				.setXmlns(CapValidator.CAP_LATEST_XMLNS)
				.setIdentifier("43b080713727")
				.setSender("hsas@dhs.gov")
				.setSent("2003-04-02T14:39:01-05:00")
				.setStatus(Alert.Status.ACTUAL)
				.setMsgType(Alert.MsgType.ALERT)
				.setSource("a source")
				.setScope(Alert.Scope.PUBLIC)
				.addCode("abcde")
				.setNote("a note")
				.setReferences(
						Group.newBuilder()
								.addValue(
										"hsas@dhs.gov,123,2003-02-02T14:39:01-05:00")
								.addValue(
										"hsas@dhs.gov,456,2003-03-02T14:39:01-05:00")
								.build())
				.setIncidents(
						Group.newBuilder().addValue("incident1")
								.addValue("incident2").build())
				.addInfo(getValidInfoBuilder());
	}

	public static Info.Builder getValidInfoBuilder() {
		return Info
				.newBuilder()
				.addCategory(Info.Category.SECURITY)
				.addCategory(Info.Category.SAFETY)
				.setEvent("Homeland Security Advisory System Update")
				.setUrgency(Info.Urgency.IMMEDIATE)
				.setSeverity(Info.Severity.SEVERE)
				.setCertainty(Info.Certainty.LIKELY)
				.setSenderName("Department of Homeland Security")
				.setHeadline("Homeland Security Sets Code ORANGE")
				.setDescription("DHS has set the threat level to ORANGE.")
				.setInstruction("Take Protective Measures.")
				.setWeb("http://www.dhs.gov/dhspublic/display?theme=29")
				.addParameter(
						ValuePair.newBuilder().setValueName("HSAS")
								.setValue("ORANGE").build())
				.setAudience("an audience")
				.setContact("a contact")
				.addEventCode(
						ValuePair.newBuilder().setValueName("EC")
								.setValue("v1").build())
				.setEffective("2003-04-02T14:39:01-05:00")
				.setOnset("2003-04-02T15:39:01+05:00")
				.setExpires("2003-04-02T16:39:01-00:00")
				.addArea(getValidAreaBuilder())
				.addResource(getValidResourceBuilder());
	}

	public static Area.Builder getValidAreaBuilder() {
		return Area
				.newBuilder()
				.setAreaDesc("U.S. nationwide")
				.addPolygon(
						Polygon.newBuilder()
								.addPoint(
										Point.newBuilder().setLatitude(1)
												.setLongitude(2).build())
								.addPoint(
										Point.newBuilder().setLatitude(3)
												.setLongitude(4).build())
								.addPoint(
										Point.newBuilder().setLatitude(5)
												.setLongitude(6).build())
								.addPoint(
										Point.newBuilder().setLatitude(1)
												.setLongitude(2).build())
								.build())
				.addCircle(
						Circle.newBuilder()
								.setPoint(
										Point.newBuilder().setLatitude(1)
												.setLongitude(2).build())
								.setRadius(0).build())
				.addGeocode(
						ValuePair.newBuilder().setValueName("G1")
								.setValue("v1").build()).setAltitude(5.5)
				.setCeiling(6.5);
	}

	public static Resource.Builder getValidResourceBuilder() {
		return Resource.newBuilder().setResourceDesc("Image file (GIF)")
				.setUri("http://www.dhs.gov/dhspublic/getAdvisoryImage")
				.setMimeType("image/gif").setSize(123);
	}

}

class MockTrustStrategy implements TrustStrategy {
	private boolean allowMissingSignatures = true;
	private boolean allowUntrustedCredentials = true;
	private final Set<PublicKey> trustedKeys = Sets.newHashSet();

	@Override
	public boolean allowMissingSignatures() {
		return allowMissingSignatures;
	}

	@Override
	public boolean allowUntrustedCredentials() {
		return allowUntrustedCredentials;
	}

	@Override
	public boolean isKeyTrusted(PublicKey key) {
		return trustedKeys.contains(key);
	}

	public MockTrustStrategy setAllowMissingSignatures(
			boolean allowMissingSignatures) {
		this.allowMissingSignatures = allowMissingSignatures;
		return this;
	}

	public MockTrustStrategy setAllowUntrustedCredentials(
			boolean allowUntrustedCredentials) {
		this.allowUntrustedCredentials = allowUntrustedCredentials;
		return this;
	}

	public MockTrustStrategy addTrustedKey(PublicKey trustedKey) {
		this.trustedKeys.add(trustedKey);
		return this;
	}

	public MockTrustStrategy removeTrustedKey(PublicKey untrustedKey) {
		this.trustedKeys.remove(untrustedKey);
		return this;
	}

	public MockTrustStrategy clearTrustedKeys() {
		this.trustedKeys.clear();
		return this;
	}
}