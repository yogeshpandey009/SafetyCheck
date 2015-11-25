package com.semantic.safetycheck.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.map.ObjectMapper;

import com.semantic.safetycheck.app.SafetyCheckServlet;
import com.semantic.safetycheck.dao.EarthquakeDAO;
import com.semantic.safetycheck.pojo.Earthquake;

@Path("/earthquakes")
public class EarthquakeService {
	
	private ObjectMapper mapper = new ObjectMapper();
	private EarthquakeDAO dao = new EarthquakeDAO();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response earthquakes(InputStream incomingData) {
		
		List<Earthquake> earthquakes = dao.getAllEarthquakes(SafetyCheckServlet.data);
		
		String earthquakeData = "[]";
		try {
			earthquakeData = mapper.writeValueAsString(earthquakes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// return HTTP response 200 in case of success
		return Response.status(200).entity(earthquakeData).build();
	}
	
}
