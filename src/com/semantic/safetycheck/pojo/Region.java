package com.semantic.safetycheck.pojo;

public class Region {

	private String id;
	private String name;
	private Float latitude;
	private Float longitude;
	private Integer population;

	public Region(String id, String name, Float latitude, Float longitude, Integer population) {
		super();
		if (id.indexOf("#") == -1){
			this.id = id;
		} else {
			this.id = id.substring(id.indexOf("#"));
		}
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.population = population;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Integer getPopulation() {
		return population;
	}

	public void setPopulation(Integer population) {
		this.population = population;
	}

}
