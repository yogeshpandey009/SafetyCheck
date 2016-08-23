package com.google.pubsubhubbub;

public enum TopicUrl {

	EARTHQUAKE("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/4.5_week.atom"),
	WEATHER("https://alerts.weather.gov/cap/us.atom");//https://alerts.weather.gov/cap/us.php?x=0

	private String url;

	TopicUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public static TopicUrl getTopic(String url) {
		if (url != null) {
			for (TopicUrl t : TopicUrl.values()) {
				if (url.equalsIgnoreCase(t.url)) {
					return t;
				}
			}
		}
		return null;
	}
}
