package com.semantic.safetycheck.api;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.semantic.safetycheck.app.SafetyCheckServlet;
import com.semantic.safetycheck.dao.EarthquakeDAO;
import com.semantic.safetycheck.pojo.Earthquake;

@Path("/earthquakes")
public class EarthquakeService extends SCService {
	
	private EarthquakeDAO dao = new EarthquakeDAO();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response earthquakes(InputStream incomingData) {
		List<Earthquake> earthquakes = null;
		Boolean success = Boolean.TRUE;
		String msg = "";
		try {
			earthquakes = dao.getAllEarthquakes(SafetyCheckServlet.data);
		} catch(Exception e){
			success = Boolean.FALSE;
			msg = e.getMessage();
		}
		String earthquakeData = getResponse(success, earthquakes, msg);
		
		// return HTTP response 200 in case of success
		return Response.status(200).entity(earthquakeData).build();
	}
	
}
