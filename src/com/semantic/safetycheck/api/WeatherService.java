package com.semantic.safetycheck.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import com.semantic.safetycheck.app.SafetyCheckServlet;
import com.semantic.safetycheck.convertor.RDFGenerator;
import com.semantic.safetycheck.dao.WeatherDAO;
import com.semantic.safetycheck.pojo.Weather;

@Path("/weather")
public class WeatherService  extends SCService {

	private WeatherDAO dao = new WeatherDAO();
	private ObjectMapper mapper = new ObjectMapper();
	private static int i = 0;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response weatherAlerts(@Context UriInfo info) {
		List<Weather> weatherAlerts = null;
		Boolean success = Boolean.TRUE;
		String msg = "";
		String personId = info.getQueryParameters().getFirst("person");
		try {
			if(personId != null && StringUtils.isNoneEmpty(personId)) {
				weatherAlerts = dao.getImpactedByWeatherAlerts(SafetyCheckServlet.defaultNameSpace + personId);
			} else {
				weatherAlerts = dao.getAllWeatherAlerts();
			}
			if(weatherAlerts != null) {
				Collections.sort(weatherAlerts, Weather.WTimeComparator);
			}
		} catch (Exception e) {
			success = Boolean.FALSE;
			msg = e.getMessage();
			e.printStackTrace();
		}
		String response = getResponse(success, weatherAlerts, msg);

		// return HTTP response 200 in case of success
		return Response.status(200).entity(response).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addWeatherAlert(InputStream incomingData) {
		Boolean success = Boolean.TRUE;
		String msg = "Weather Alert information has been added";
		try {
			Weather weatherObj = mapper.readValue(incomingData,
					Weather.class);
			// Earthquake earthquakeObj = mapper.readValue(incomingData,
			// Earthquake.class);
			// Earthquake earthquakeObj = new Earthquake(3.0f,
			// "2015-11-12T00:22:32.520Z", -10.0f, 10.0f);
			weatherObj.setId(100000 + i++ + "");
			String eq = RDFGenerator.singleWeatherRDF(weatherObj);
			SafetyCheckServlet.addAlertRDF(eq);

		} catch (IOException e) {
			success = Boolean.FALSE;
			msg = e.getMessage();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String response = getResponse(success, null, msg);
		return Response.status(200).entity(response).build();
	}

}
