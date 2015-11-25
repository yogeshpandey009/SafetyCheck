package com.semantic.safetycheck.pojo;

public class Person {

	private String location;
	private String name;
	private Float lat;
	private Float lon;
	
	public Person(){
		super();
	}
	
	public Person(String name,String location,Float lat,Float lon){
		super();
		this.name = name;
		this.location = location;
		this.lat = lat;
		this.lon = lon;
	}
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	
}

