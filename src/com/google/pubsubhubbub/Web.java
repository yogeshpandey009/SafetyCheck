package com.google.pubsubhubbub;

import java.net.BindException;
import java.util.ArrayList;

//The Jetty webserver version used for this project is 7.0.1.v20091125
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;

public class Web extends Thread {
	
	static Web webserver;
	static int port = 8080;
	String contextPath = "/push";
	Server server = null;
	
	static ArrayList<String> approvedActions = new ArrayList<String>();

	public Web(int port) {
		
		Web.port = port;
		
		try {
			this.setup();
			
		} catch (BindException e){
			e.printStackTrace();
			System.out.println("Web function():" + "Address " + port + " already in use");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Web function():" + "webserver can not be setup");
		}
	}

	public static int getPort() {
		return port;
	}

	public static ArrayList<String> getApprovedActions() {
		return approvedActions;
	}

	
	public Web getInstance() {
		if (webserver == null) {
			return webserver;
		}
		return null;
	}

	public void addAction(String hubmode, String hubtopic, String hubverify) {
		String action=hubmode + ":" + hubtopic + ":" + hubverify;
		approvedActions.add(action);
	}

	public void setup() throws Exception {
		
		server = new Server(port);
		
		ContextHandler context = new ContextHandler();
		//The server only respond to the following contextPath URI
		context.setContextPath(contextPath);
		context.setResourceBase(".");
		context.setClassLoader(Thread.currentThread().getContextClassLoader());
		server.setHandler(context);
 
		context.setHandler(new PuSHhandler());
		server.start();

	}

	public void run() {
	}

}