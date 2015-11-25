package com.semantic.safetycheck.pojo;

public class Earthquake {

	private String id;
	private Float magnitude;
	private String time;
	private Float latitude;
	private Float longitude;
	
	public Earthquake() {
		super();
	}

	public Earthquake(String id, Float magnitude, String time,
			Float latitude, Float longitude) {
		super();
		this.id = id;
		this.magnitude = magnitude;
		this.time = time;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Float getMagnitude() {
		return magnitude;
	}
	public void setMagnitude(Float magnitude) {
		this.magnitude = magnitude;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
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
