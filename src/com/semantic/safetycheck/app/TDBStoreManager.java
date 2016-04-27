package com.semantic.safetycheck.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.base.file.Location;
import com.hp.hpl.jena.util.FileManager;

public class TDBStoreManager {

	protected Dataset dataset = null;
	private final String dataDir = "./data/tdb";
	private final String rulesPath = "WEB-INF/classes/rules.txt";
	private String path = "";
	protected OntModel ontModel = null;
	protected Model base = null;

	public TDBStoreManager(String path) {
		this.path = path;
		boolean alreadyExist = false;
		File f = new File(dataDir);
		if (!f.exists()) {
			f.mkdirs();
		}
		try {
			System.out.println("DB files are present at: " + f.getCanonicalPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Location location = Location.create(dataDir);
		dataset = TDBFactory.createDataset(location);
		if (dataset.listNames().hasNext()) {
			alreadyExist = true;
		}
		base = dataset.getDefaultModel();
		if (!alreadyExist) {
			// om = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM,
			// base);
			ontModel = ModelFactory.createOntologyModel();
			loadDataIntoTDB();
			runReasoner();
		}
	}

	private void saveIntoTDB() {
		dataset.begin(ReadWrite.WRITE);
		base.add(ontModel);
		dataset.commit();
		dataset.end();
	}

	/*
	 * public void load(InputStream is) { DatasetGraphTDB dsg =
	 * TDBInternal.getBaseDatasetGraphTDB(dataset .asDatasetGraph());
	 * TDBLoader.load(dsg, is, false); dataset.commit(); dataset.end(); try {
	 * is.close(); } catch (IOException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } }
	 */

	public Model readModel(String filename) {
		// TDB.sync(dataset);
		dataset.begin(ReadWrite.WRITE);
		Model model = FileManager.get().readModel(ontModel, filename, null,
				"RDF/XML");
		// base.add(model);
		// om.read(is, ns);
		dataset.commit();
		dataset.end();
		return model;
	}

	public void read(InputStream is, String ns) {
		dataset.begin(ReadWrite.WRITE);
		ontModel.read(is, ns);
		dataset.commit();
		dataset.end();
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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

		String owlFilePath = path + "WEB-INF/classes/SafetyCheck.owl";
		String friendsFilePath = path + "WEB-INF/classes/friends.rdf";
		String regionsFilePath = path + "WEB-INF/classes/region.rdf";
		String earthquakesFilePath = path + "WEB-INF/classes/earthquakes.rdf";
		readModel(owlFilePath);
		readModel(friendsFilePath);
		readModel(regionsFilePath);
		//readModel(earthquakesFilePath);
	}
	/*
	 * protected void begin(final ReadWrite write) { dataset.begin(write); }
	 * protected void end() { dataset.end(); }
	 */
	/*
	 * protected QueryExecution getQueryExe(final String query) { return
	 * QueryExecutionFactory.create(query, dataset); } protected Model
	 * getNamedModel(final String context) { return
	 * dataset.getNamedModel(context); }
	 * 
	 * public void addModel(final Model model) { dataset.begin(ReadWrite.WRITE)
	 * ; dataset.getDefaultModel().add(model) ; dataset.commit(); dataset.end();
	 * }
	 * 
	 * public void deleteModel(final String modelId) {
	 * dataset.begin(ReadWrite.WRITE); dataset.removeNamedModel(modelId);
	 * dataset.commit(); }
	 */
}
