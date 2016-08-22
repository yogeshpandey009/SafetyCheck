package com.semantic.safetycheck.app;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import com.google.pubsubhubbub.GoogleAlertSubscriber;
import com.hp.hpl.jena.reasoner.rulesys.BuiltinRegistry;
import com.semantic.safetycheck.builtin.ImpactZoneMatch;
import com.semantic.safetycheck.builtin.MatchLiteral;

/**
 * Servlet implementation class SafetyCheckServlet
 */
@SuppressWarnings("serial")
public class SafetyCheckServlet extends HttpServlet {

	public static final String defaultNameSpace = "http://www.semanticweb.org/ontologies/2015/10/SafetyCheck#";
	static ServletContext context = null;
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
		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				GoogleAlertSubscriber.initiate();
			}
		});
		t1.start();
		t2.start();
	}

	/**
	 * Default constructor.
	 */
	public SafetyCheckServlet() {
		// TODO Auto-generated constructor stub
	}

	public static void registerCustomBuiltinRules() {
		BuiltinRegistry.theRegistry.register(new MatchLiteral());
		BuiltinRegistry.theRegistry.register(new ImpactZoneMatch());
	}

	public static void addAlertRDF(String eq) {
		InputStream is = new ByteArrayInputStream(eq.getBytes());
		store.read(is, defaultNameSpace);
		store.runReasoner();
	}

}
