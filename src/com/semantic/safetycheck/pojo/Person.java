package com.semantic.safetycheck.pojo;

public class Person {

	private String location;
	private String name;
	private Float latitude;
	private Float longitude;
	
	public Person(){
		super();
	}
	
	public Person(String name,String location,Float latitude,Float longitude){
		super();
		this.name = name;
		this.location = location;
		this.latitude = latitude;
		this.longitude = longitude;
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

