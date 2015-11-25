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
import com.semantic.safetycheck.dao.PersonDAO;
import com.semantic.safetycheck.pojo.Person;

@Path("/person")
public class PersonService {
	
	private ObjectMapper mapper = new ObjectMapper();
	private PersonDAO dao = new PersonDAO();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response person(InputStream incomingData) {
		
		List<Person> person = dao.getAllPersons(SafetyCheckServlet.data);
		
		String personData = "[]";
		try {
			personData = mapper.writeValueAsString(person);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// return HTTP response 200 in case of success
		return Response.status(200).entity(personData).build();
	}
	
}
