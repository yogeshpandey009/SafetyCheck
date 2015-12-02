package com.semantic.safetycheck.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import com.semantic.safetycheck.app.SafetyCheckServlet;
import com.semantic.safetycheck.convertor.RDFGenerator;
import com.semantic.safetycheck.dao.EarthquakeDAO;
import com.semantic.safetycheck.pojo.Earthquake;

@Path("/")
public class EarthquakeService extends SCService {

	private EarthquakeDAO dao = new EarthquakeDAO();
	private ObjectMapper mapper = new ObjectMapper();

	@GET
	@Path("/earthquakes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response earthquakes(@Context UriInfo info) {
		List<Earthquake> earthquakes = null;
		Boolean success = Boolean.TRUE;
		String msg = "";
		String personId = info.getQueryParameters().getFirst("person");
		try {
			if(personId != null && StringUtils.isNoneEmpty(personId)) {
				earthquakes = dao.getImpactedByEarthquakes(
						SafetyCheckServlet.inf_data, SafetyCheckServlet.defaultNameSpace + personId);				
			} else {
				earthquakes = dao.getAllEarthquakes(SafetyCheckServlet.data);				
			}
		} catch (Exception e) {
			success = Boolean.FALSE;
			msg = e.getMessage();
		}
		String response = getResponse(success, earthquakes, msg);

		// return HTTP response 200 in case of success
		return Response.status(200).entity(response).build();
	}

	@POST
	@Path("/earthquake")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addEarthquake(InputStream incomingData) {
		Boolean success = Boolean.TRUE;
		String msg = "Earthquake information has been added";
		try {
			Earthquake earthquakeObj = mapper.readValue(incomingData,
					Earthquake.class);
			// Earthquake earthquakeObj = mapper.readValue(incomingData,
			// Earthquake.class);
			// Earthquake earthquakeObj = new Earthquake(3.0f,
			// "2015-11-12T00:22:32.520Z", -10.0f, 10.0f);
			earthquakeObj.setId("100000");
			String eq = RDFGenerator.singleEarthquakeRDF(earthquakeObj);
			SafetyCheckServlet.addEarthquakeInstance(eq);

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
