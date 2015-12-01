package com.semantic.safetycheck.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/test")
public class SafetyCheckService extends SCService {
 
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response verifyRESTService() {
		String result = "SafetyCheckWeb Successfully started..";
		// return HTTP response 200 in case of success
		return Response.status(200).entity(result).build();
	}
	
}
