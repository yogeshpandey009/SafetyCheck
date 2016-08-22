package com.google.pubsubhubbub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
//The Jetty webserver version used for this project is 7.0.1.v20091125
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.google.publicalerts.cap.Alert;
import com.google.publicalerts.cap.CapXmlParser;
import com.google.publicalerts.cap.Info;
import com.semantic.safetycheck.app.SafetyCheckServlet;
import com.semantic.safetycheck.convertor.AlertToRDF;
import com.sun.syndication.feed.synd.SyndEntry;
//Rome libraries (https://rome.dev.java.net/)
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLinkImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class PuSHhandler extends AbstractHandler {
	enum MessageStatus {
		ERROR, OK_Challenge, OK
	};

	public PuSHhandler() {
	}

	@SuppressWarnings("unchecked")
	private void processFeeds(String hubtopic, SyndFeed feed) {

		// checks if the channel is already created
		System.out.println("processFeed: " + feed.getTitle());
		List<SyndEntry> entries = feed.getEntries();
		System.out.println(feed.getTitle());
		List<Alert> alerts = new ArrayList<Alert>();
		for (SyndEntry entry : entries) {
			System.out.println(entry.getTitle());
			try {
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
						System.out.println("\t\tDescription: " + infoItem.getDescription());
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
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		String alertsRDF = AlertToRDF.convertAlertstoRDF(alerts);
		System.out.println(alertsRDF);
		SafetyCheckServlet.addAlertRDF(alertsRDF);
	}

	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String hubmode = null, hubtopic = null, hubchallenge = null, hubverify = null, hublease = null;
		ArrayList<String> approvedActions;
		MessageStatus stsMessage = MessageStatus.ERROR;

		if (request != null) {
			System.out.println("Request from: " + request.getRemoteAddr());
			// verification if the method is a GET or a POST
			if (request.getMethod().equals("GET")) {

				if (request.getParameter("hub.mode") != null) {
					hubmode = request.getParameter("hub.mode");
				}
				if (request.getParameter("hub.topic") != null) {
					hubtopic = request.getParameter("hub.topic");
				}
				if (request.getParameter("hub.challenge") != null) {
					hubchallenge = request.getParameter("hub.challenge");
				}
				if (request.getParameter("hub.verify_token") != null) {
					hubverify = request.getParameter("hub.verify_token");
				}
				if (request.getParameter("hub.lease_seconds") != null) {
					hublease = request.getParameter("hub.lease_seconds");
				}

				if (((hubmode.equals("subscribe")) || (hubmode
						.equals("unsubscribe")))
						&& (hubmode.length() > 0)
						&& (hubtopic.length() > 0)
						&& (hubchallenge.length() > 0)) {

					approvedActions = Web.getApprovedActions();

					for (String action : approvedActions) {

						if (action.startsWith(hubmode + ":" + hubtopic + ":"
								+ hubverify)) {

							stsMessage = MessageStatus.OK_Challenge;
							approvedActions.remove(action);
							break;
						}
					}

					// NOTE! This code should be changed for your case//
					Object channel = null;// NOTE! here you should check if the
											// channel already exist in your
											// subscriber.

					if (hubmode.equals("subscribe")) {

						if (channel != null) {
							// stsMessage=MessageStatus.ERROR;
						} else {
							if (stsMessage.equals(MessageStatus.OK_Challenge)) {
								// create a new channel
							} else {
								// Subscription Refreshing
								stsMessage = MessageStatus.OK_Challenge;
							}
						}

					} else if (hubmode.equals("unsubscribe")) {

						if (channel != null) {
							stsMessage = MessageStatus.OK_Challenge;
						} else {
							if (stsMessage.equals(MessageStatus.OK_Challenge)) {
								// remove subscription channel
							} else {
								// stsMessage=MessageStatus.ERROR;
							}
						}
					}
				}
				// feeds received from the hub
			} else if (request.getMethod().equals("POST")) {

				// receive an atom or an RSS feed
				if (request.getContentType().contains("application/atom+xml")
						|| request.getContentType().contains(
								"application/rss+xml")) {

					// read the body data and convert it to an InputStream
					InputStream in = request.getInputStream();

					// create the new SyndFeed object
					try {
						SyndFeedInput input = new SyndFeedInput();
						SyndFeed feed = input.build(new XmlReader(in));

						List<SyndLinkImpl> linkList = feed.getLinks();

						for (SyndLinkImpl link : linkList) {

							if (link.getRel().equals("self")) {
								hubtopic = link.getHref().toString();
							}
						}

						if (hubtopic == null) {
							hubtopic = feed.getUri();
						}
						// check if the channel exist. If so, add feeds to the
						// channel
						processFeeds(hubtopic, feed);
					} catch (FeedException e) {
						e.printStackTrace();
					}
					stsMessage = MessageStatus.OK;
				}
			}

			// Send message to the HUB
			response.setContentType("application/x-www-form-urlencoded");

			switch (stsMessage) {
				case OK :
					response.setStatus(HttpServletResponse.SC_OK);
					break;
				case OK_Challenge :
					response.setStatus(HttpServletResponse.SC_OK);
					response.getWriter().print(hubchallenge);
					break;
				default :
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
					break;
			}

			baseRequest.setHandled(true);
		}
	}
}