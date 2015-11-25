package com.semantic.safetycheck.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

public class SCService {

	private Map<String, Object> resultMap = new HashMap<String, Object>();
	private ObjectMapper mapper = new ObjectMapper();

	public String getResponse(Boolean success, Object data, String msg) {
		resultMap.put("success", success);
		resultMap.put("data", data);
		resultMap.put("msg", msg);
		String response = "";
		try {
			response = mapper.writeValueAsString(resultMap);
		} catch (IOException e) {
			response = e.getMessage();
			e.printStackTrace();
		}
		return response;

	}

}
