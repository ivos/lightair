package net.sf.lightair.internal;

import net.sf.lightair.internal.db.Converter;
import net.sf.lightair.internal.db.Delete;
import net.sf.lightair.internal.db.Execute;
import net.sf.lightair.internal.db.Insert;
import net.sf.lightair.internal.db.Structure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Api {

	private static final Logger log = LoggerFactory.getLogger(Api.class);

	private static String propertiesFileName;
	private static Map<String, Map<String, String>> properties;
	private static Map<String, Connection> connections;
	private static Map<String, Map<String, Map<String, Map<String, Object>>>> structures;

	public static void initialize(String propertiesFileName) {
		log.info("Initializing Light Air.");
		Api.propertiesFileName = propertiesFileName;
		performInitialization();
		log.debug("Light Air has initialized.");
	}

	public static void shutdown() {
		log.debug("Shutting down Light Air.");
		Connections.close(connections);
		structures = null;
		connections = null;
		properties = null;
		log.info("Light Air has shut down.");
	}

	public static void reInitialize() {
		log.info("Re-initializing Light Air.");
		performInitialization();
		log.debug("Light Air has re-initialized.");
	}

	private static void performInitialization() {
		properties = Properties.load(propertiesFileName);
		connections = Connections.open(properties);
		structures = Structure.loadAll(properties, connections);
	}

	public static void generateXsd(String propertiesFileName) {
		Xsd.generate(properties, structures);
	}

	public static void setup(Map<String, List<String>> fileNames) {
		log.info("Performing setup for files {}.", fileNames);
		Map<String, List<Map<String, Object>>> xmlDatasets = Xml.read(fileNames);
		Map<String, List<Map<String, Object>>> datasets = Converter.convert(structures, xmlDatasets);

		for (String profile : datasets.keySet()) {
			Map<String, String> profileProperties = properties.get(profile);
			Map<String, Map<String, Map<String, Object>>> profileStructure = structures.get(profile);
			List<Map<String, Object>> dataset = datasets.get(profile);
			Connection connection = connections.get(profile);

			List<Map<String, Object>> statements = new ArrayList<>();
			statements.addAll(Delete.create(profileProperties, profileStructure, dataset));
			statements.addAll(Insert.create(profileProperties, profileStructure, dataset));

			Execute.update(connection, statements);
		}
		log.debug("Finished setup for files {}.", fileNames);
	}
}
