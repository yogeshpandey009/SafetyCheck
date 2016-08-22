package com.semantic.safetycheck.app;

import java.io.IOException;
import java.io.InputStream;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.DatasetAccessor;
import com.hp.hpl.jena.query.DatasetAccessorFactory;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.util.FileManager;

public class TDBStoreManager {

	// protected Dataset dataset = null;
	// private final String dataDir = "./data/tdb";
	String dataURI = "http://localhost:3030/sc/data";
	private final String rulesPath = "WEB-INF/classes/rules.txt";
	private String path = "";
	protected OntModel ontModel = null;
	protected Model base = null;
	DatasetAccessor accessor = null;
	public TDBStoreManager(String path) {
		this.path = path;
		accessor = DatasetAccessorFactory.createHTTP(dataURI);
		// File f = new File(dataDir);
		// if (!f.exists()) {
		// f.mkdirs();
		// }
		// try {
		// System.out.println("DB files are present at: " +
		// f.getCanonicalPath());
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// Location location = Location.create(dataDir);
		// dataset = TDBFactory.createDataset(location);
		// if (dataset.listNames().hasNext()) {
		// alreadyExist = true;
		// }
		// base = dataset.getDefaultModel();
		base = accessor.getModel();
		if (base.isEmpty()) {
			// om = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM,
			// base);
			// base = ModelFactory.createDefaultModel();
			ontModel = ModelFactory.createOntologyModel();
			loadDataIntoTDB();
			runReasoner();
			// accessor.putModel(ontModel);
		}
	}

	private void saveIntoTDB() {
		// dataset.begin(ReadWrite.WRITE);
		base.add(ontModel);
		accessor.putModel(base);
		// dataset.commit();
		// dataset.end();
	}

	public Model readFile(String filename) {
		// TDB.sync(dataset);
		// dataset.begin(ReadWrite.WRITE);
		Model model = FileManager.get().readModel(ontModel, filename, null,
				"RDF/XML");
		// base.add(model);
		// om.read(is, ns);
		// dataset.commit();
		// dataset.end();
		return model;
	}

	public void read(InputStream is, String ns) {
		// dataset.begin(ReadWrite.WRITE);
		ontModel.read(is, ns);
		// dataset.commit();
		// dataset.end();
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void runReasoner() {
		Reasoner reasoner = new GenericRuleReasoner(Rule.rulesFromURL(path
				+ rulesPath));
		reasoner.setDerivationLogging(true);
		InfModel infModel = ModelFactory.createInfModel(reasoner, ontModel);
		// infModel.prepare();
		ontModel.add(infModel.getDeductionsModel());
		saveIntoTDB();
	}

	private void loadDataIntoTDB() {

		//String owlFilePath = path + "WEB-INF/classes/SafetyCheck.owl";
		String owlFilePath = path + "WEB-INF/classes/SafetyCheck_V2.owl";
		String friendsFilePath = path + "WEB-INF/classes/friends.rdf";
		String regionsFilePath = path + "WEB-INF/classes/regions.rdf";
		// String earthquakesFilePath = path +
		// "WEB-INF/classes/earthquakes.rdf";
		readFile(owlFilePath);
		readFile(friendsFilePath);
		readFile(regionsFilePath);
		// readModel(earthquakesFilePath);
	}
}
