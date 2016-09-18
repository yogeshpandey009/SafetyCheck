package com.google.pubsubhubbub;

import java.util.Arrays;

public enum TopicUrl {

	EARTHQUAKE("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/4.5_week.atom"),
	WEATHER("https://alerts.weather.gov/cap/us.php?x=0", "https://alerts.weather.gov/cap/us.atom");

	private String[] url;

	TopicUrl(String... url) {
		this.url = url;
	}

	public String getUrl() {
		return url[0];
	}

	public static TopicUrl getTopic(String url) {
		if (url != null) {
			for (TopicUrl t : TopicUrl.values()) {
				if (Arrays.asList(t.url).contains(url)) {
					return t;
				}
			}
		}
		return null;
	}
}
