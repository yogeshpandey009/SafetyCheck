package com.semantic.safetycheck.app;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import com.google.pubsubhubbub.GoogleAlertSubscriber;
import com.hp.hpl.jena.reasoner.rulesys.BuiltinRegistry;
import com.semantic.safetycheck.builtin.EQImpactZoneMatch;
import com.semantic.safetycheck.builtin.MatchRegion;
import com.semantic.safetycheck.builtin.WeatherImpactZoneMatch;

/**
 * Servlet implementation class SafetyCheckServlet
 */
@SuppressWarnings("serial")
public class SafetyCheckServlet extends HttpServlet {

	public static final String defaultNameSpace = "http://www.semanticweb.org/ontologies/2015/10/SafetyCheck#";
	ServletContext context = null;
	GoogleAlertSubscriber alertSbscr = null;
	static public TDBStoreManager store = null;

	@Override
	public void init() {
		context = getServletContext();
		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				registerCustomBuiltinRules();
				store = new TDBStoreManager(context.getRealPath(File.separator));
			}
		});
		t1.start();
		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				alertSbscr = GoogleAlertSubscriber.initiate();
			}
		});
		t2.start();
	}

	@Override
	public void destroy() {
		if(alertSbscr != null) {
			//alertSbscr.shutdown();
		}
	}

	/**
	 * Default constructor.
	 */
	public SafetyCheckServlet() {
		// TODO Auto-generated constructor stub
	}

	public void registerCustomBuiltinRules() {
		BuiltinRegistry.theRegistry.register(new MatchRegion());
		BuiltinRegistry.theRegistry.register(new EQImpactZoneMatch());
		BuiltinRegistry.theRegistry.register(new WeatherImpactZoneMatch());
	}

	public static void addAlertRDF(String eq) {
		InputStream is = new ByteArrayInputStream(eq.getBytes());
		store.read(is, defaultNameSpace);
		store.saveData();
	}

}
