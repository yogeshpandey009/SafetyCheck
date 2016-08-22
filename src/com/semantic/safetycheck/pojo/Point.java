package com.semantic.safetycheck.pojo;

public class Point {

	private Float latitude;
	private Float longitude;

	public Point(Float latitude, Float longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public Float getLatitude() {
		return latitude;
	}
	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}
	public Float getLongitude() {
		return longitude;
	}
	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

}
