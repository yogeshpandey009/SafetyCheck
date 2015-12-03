 package com.semantic.safetycheck.pojo;

public class Person {

	private String id;
	private String location;
	private String name;
	private Float latitude;
	private Float longitude;
	
	public Person(){
		super();
	}
	
	public Person(String id,String name,String location,Float latitude,Float longitude){
		super();
		if (id.indexOf("#") == -1){
			this.id = id;
		} else {
			this.id = id.substring(id.indexOf("#"));
		}
		this.name = name;
		this.location = location;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

