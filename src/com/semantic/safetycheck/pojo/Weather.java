package com.semantic.safetycheck.pojo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Weather {

	private SimpleDateFormat formatter = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private static SimpleDateFormat iso_format = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXXX");

	private String id;
	private String severity;
	private Date time;
	private List<Point> points;
	private String desc;
	private String areaDesc;

	public Weather() {
		super();
	}

	public Weather(String id, String severity, String time, List<Point> points,
			String desc, String areaDesc) {
		super();
		if (id.indexOf("#") == -1) {
			this.id = id;
		} else {
			this.id = id.substring(id.indexOf("#"));
		}
		this.severity = severity;
		this.time = parseTime(time);
		this.points = points;
		this.desc = desc;
		this.areaDesc = areaDesc;
	}

	public Weather(String severity, String time, List<Point> points,
			String desc) {
		super();
		this.severity = severity;
		this.time = parseTime(time);
		this.points = points;
		this.desc = desc;
	}

	private Date parseTime(String time) {
		// 2015-11-12T00:22:32.520Z
		Date date = null;
		try {
			date = formatter.parse(time);
			// System.out.println(date.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTimeAsFormat() {
		return formatter.format(time);
	}
	public Date getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = new Date(Long.parseLong(time));
		// this.time = parseTime(time);
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

	public String getAreaDesc() {
		return areaDesc;
	}

	public void setAreaDesc(String areaDesc) {
		this.areaDesc = areaDesc;
	}



	public static Comparator<Weather> WTimeComparator = new Comparator<Weather>() {

		public int compare(Weather eq1, Weather eq2) {
			return eq2.getTime().compareTo(eq1.getTime());
		}
	};

	public static void main(String[] args) {
		new Weather().parseTime("2015-11-12T00:22:32.520Z");
	}


}
