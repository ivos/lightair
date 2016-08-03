package net.sf.lightair.internal;

import net.sf.lightair.internal.auto.Index;
import net.sf.lightair.internal.db.Delete;
import net.sf.lightair.internal.db.ExecuteQuery;
import net.sf.lightair.internal.db.ExecuteUpdate;
import net.sf.lightair.internal.db.Insert;
import net.sf.lightair.internal.db.Select;
import net.sf.lightair.internal.db.Structure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Api implements Keywords {

	private static final Logger log = LoggerFactory.getLogger(Api.class);

	private static String propertiesFileName;
	private static Map<String, Map<String, String>> properties;
	private static Map<String, Connection> connections;
	private static Map<String, Map<String, Map<String, Map<String, Object>>>> structures;
	private static Map<String, String> index;

	public static void initialize(String propertiesFileName) {
		log.info("Initializing Light Air.");
		Api.propertiesFileName = propertiesFileName;
		performInitialization();
		log.debug("Light Air has initialized.");
	}

	public static void shutdown() {
		log.debug("Shutting down Light Air.");
		performShutdown();
		log.info("Light Air has shut down.");
	}

	public static void reInitialize() {
		log.info("Re-initializing Light Air.");
		performShutdown();
		performInitialization();
		log.debug("Light Air has re-initialized.");
	}

	private static void performInitialization() {
		properties = Properties.load(propertiesFileName);
		connections = Connections.open(properties);
		structures = Structure.loadAll(properties, connections);
		index = Index.readAndUpdate(properties, structures);
	}

	private static void performShutdown() {
		Connections.close(connections);
		index = null;
		structures = null;
		connections = null;
		properties = null;
	}

	public static void generateXsd(String propertiesFileName) {
		Xsd.generate(properties, structures);
	}

	public static void setup(Map<String, List<String>> fileNames) {
		log.info("Performing setup for files {}.", fileNames);
		Map<String, List<Map<String, Object>>> xmlDatasets = Xml.read(fileNames);
		Map<String, List<Map<String, Object>>> datasets = Convert.convert(structures, index, xmlDatasets);

		for (String profile : datasets.keySet()) {
			Map<String, String> profileProperties = properties.get(profile);
			Map<String, Map<String, Map<String, Object>>> profileStructure = structures.get(profile);
			List<Map<String, Object>> dataset = datasets.get(profile);
			Connection connection = connections.get(profile);

			List<Map<String, Object>> statements = new ArrayList<>();
			statements.addAll(Delete.create(profileProperties, profileStructure, dataset));
			statements.addAll(Insert.create(profileProperties, profileStructure, dataset));

			ExecuteUpdate.run(connection, statements);
		}
		log.debug("Finished setup for files {}.", fileNames);
	}

	public static void verify(Map<String, List<String>> fileNames) {
		log.info("Performing verify for files {}.", fileNames);
		Map<String, List<Map<String, Object>>> xmlDatasets = Xml.read(fileNames);
		Map<String, List<Map<String, Object>>> expectedDatasets = Convert.convert(structures, index, xmlDatasets);

		Map<String, Map<String, List<Map<String, Object>>>> actualDatasets = new LinkedHashMap<>();
		for (String profile : expectedDatasets.keySet()) {
			Map<String, String> profileProperties = properties.get(profile);
			Map<String, Map<String, Map<String, Object>>> profileStructure = structures.get(profile);
			List<Map<String, Object>> expectedDataset = expectedDatasets.get(profile);
			Connection connection = connections.get(profile);

			List<Map<String, Object>> statements = Select.create(profileProperties, profileStructure, expectedDataset);
			actualDatasets.put(profile, ExecuteQuery.run(connection, statements));
		}
		actualDatasets = Collections.unmodifiableMap(actualDatasets);

		Map<String, String> defaultProfileProperties = properties.get(DEFAULT_PROFILE);
		Map<String, Map<String, Map<String, List<?>>>> differences =
				Compare.compare(defaultProfileProperties, expectedDatasets, actualDatasets);
		String report = Report.report(differences);
		if (!report.isEmpty()) {
			throw new AssertionError(report);
		}

		log.debug("Finished verify for files {}.", fileNames);
	}
}
