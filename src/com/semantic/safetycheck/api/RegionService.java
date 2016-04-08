package com.semantic.safetycheck.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.semantic.safetycheck.dao.RegionDAO;
import com.semantic.safetycheck.pojo.Region;

@Path("/regions")
public class RegionService extends SCService {
	
	private RegionDAO dao = new RegionDAO();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRegions() {
		List<Region> regions = null;
		Boolean success = Boolean.TRUE;
		String msg = "";
		try{
			regions = dao.getAllRegions();
		}
	   catch (Exception e) {
		    success = Boolean.FALSE;
			msg = e.getMessage();
		}
		String response = getResponse(success, regions, msg);
		return Response.status(200).entity(response).build();
	}
	
	
}
