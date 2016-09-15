package com.google.pubsubhubbub;

import java.net.InetAddress;

public class GoogleAlertSubscriber {

	private Web webserver;
	private Subscriber sbcbr;
	private String hostname = "http://" + "f48a573d.ngrok.io";
	private Integer webserverPort = 8181;
	private String hub = "http://alert-hub.appspot.com/";
	//private String eq_hub_topic = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/4.5_week.atom";
	//private String w_hub_topic = "https://alerts.weather.gov/cap/us.php?x=0";
	private static GoogleAlertSubscriber instance = null;

	private void startServer() {
		try {
			webserver = new Web(webserverPort);

			sbcbr = new Subscriber(webserver);

			// InetAddress addr = InetAddress.getLocalHost();
			// hostname = addr.getHostName();
			// hostname = "http://" + "c693a05e.ngrok.io";// + ":" + //
			// Integer.toString(webserverPort);
			System.out.println("Hostname: " + hostname);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("WebServer can not start");
		}
	}

	private void startSubscriber(String hub_topic) {
		try {
			int statusCode = sbcbr.subscribe(hub, hub_topic, hostname, null,
					null);
			if (statusCode == 204) {
				System.out
						.println("the status code of the subscription is 204: the request was verified and that the subscription is active");
			} else if (statusCode == 202) {
				System.out
						.println("the status code of the subscription is 202: the subscription has yet to be verified (i.e., the hub is using asynchronous verification)");
			} else {
				System.out.println("the status code of the subscription is:"
						+ statusCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized static GoogleAlertSubscriber initiate() {
		if (instance == null)
			instance = new GoogleAlertSubscriber();
		return instance;
	}

	private GoogleAlertSubscriber() {
		startServer();
		startSubscriber(TopicUrl.EARTHQUAKE.getUrl());
		startSubscriber(TopicUrl.WEATHER.getUrl());
	}

}
