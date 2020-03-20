package net.sf.lightair;

import net.sf.lightair.internal.Awaiting;
import net.sf.lightair.internal.Compare;
import net.sf.lightair.internal.Connections;
import net.sf.lightair.internal.Convert;
import net.sf.lightair.internal.Keywords;
import net.sf.lightair.internal.Properties;
import net.sf.lightair.internal.Report;
import net.sf.lightair.internal.Xml;
import net.sf.lightair.internal.Xsd;
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

/**
 * Light Air's programmatic API.
 * <p>
 * The general contract is to {@link Api#initialize(String)} and {@link Api#shutdown()}
 * and in between perform either {@link Api#generateXsd()} call
 * or (a series of) {@link Api#setup(Map)} and {@link Api#verify(Map)} or {@link Api#await(Map)} calls.
 * <p>
 * To generate XSD files:
 * <pre>
 * Api.initialize(Api.getPropertiesFileName());
 * Api.generateXsd();
 * Api.shutdown();
 * </pre>
 * <p>
 * To setup and verify database:
 * <pre>
 * Map&lt;String, List&lt;String&gt;&gt; fileNames = new HashMap&lt;&gt;();
 * fileNames.put(Keywords.DEFAULT_PROFILE, Arrays.asList("my-dataset-1.xml", "my-dataset-2.xml"));
 *
 * Api.initialize(Api.getPropertiesFileName());
 * Api.setup(fileNames);
 * // ...execute function under test...
 * Api.verify(fileNames);
 * // ...or for asynchronous functionality:
 * Api.await(fileNames);
 * // ...possibly repeat setup and verify / await for other tests...
 * Api.shutdown();
 * </pre>
 */
public class Api implements Keywords {

	private static final Logger log = LoggerFactory.getLogger(Api.class);

	public static final String PROPERTIES_PROPERTY_NAME = "LIGHT_AIR_PROPERTIES";

	private static String propertiesFileName;
	private static Map<String, Map<String, String>> properties;
	private static Map<String, Connection> connections;
	private static Map<String, Map<String, Map<String, Map<String, Object>>>> structures;
	private static Map<String, String> index;

	/**
	 * Get the file name of the main properties file.
	 *
	 * @return file name
	 */
	public static String getPropertiesFileName() {
		String fileName = System.getProperty(PROPERTIES_PROPERTY_NAME);
		if (null == fileName) {
			fileName = DEFAULT_PROPERTIES_FILE_NAME;
		}
		return fileName;
	}

	/**
	 * Initialize Light Air.
	 * <p>
	 * NOTE: ALWAYS call {@link Api#shutdown()} after initialize() to properly release resources.
	 *
	 * @param propertiesFileName file name of the main Light Air properties
	 */
	public static void initialize(String propertiesFileName) {
		log.info("Initializing Light Air.");
		Api.propertiesFileName = propertiesFileName;
		performInitialization();
		log.debug("Light Air has initialized.");
	}

	/**
	 * Shutdown previously initialized Light Air to properly release resources, like DB connections.
	 */
	public static void shutdown() {
		log.debug("Shutting down Light Air.");
		performShutdown();
		log.info("Light Air has shut down.");
	}

	/**
	 * Re-initialize already initialized Light Air.
	 * <p>
	 * Reloads the properties file(s) and cleanly re-initializes Light Air.
	 */
	public static void reInitialize() {
		log.info("Re-initializing Light Air.");
		performShutdown();
		performInitialization();
		log.debug("Light Air has re-initialized.");
	}

	/**
	 * Initialize Light Air if it is not initialized already.
	 *
	 * @param propertiesFileName file name of the main Light Air properties
	 */
	public static void ensureInitialized(String propertiesFileName) {
		log.trace("Ensuring Light Air has been initialized.");
		if (null == index) {
			initialize(propertiesFileName);
		}
	}

	/**
	 * Shutdown Light Air to properly release resources, like DB connections, if it is not shutdown already.
	 */
	public static void ensureShutdown() {
		log.trace("Ensuring Light Air has been shutdown.");
		if (null != properties) {
			shutdown();
		}
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

	/**
	 * Generate XSD files from the database structure.
	 */
	public static void generateXsd() {
		Xsd.generate(properties, structures);
	}

	/**
	 * Setup database.
	 *
	 * @param fileNames map of profile names to lists of names of setup dataset files for the profile
	 */
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

	/**
	 * Verify database.
	 *
	 * @param fileNames map of profile names to lists of names of verification dataset files for the profile
	 */
	public static void verify(Map<String, List<String>> fileNames) {
		log.info("Performing verify for files {}.", fileNames);

		String report = generateReport(fileNames);

		if (!report.isEmpty()) {
			throw new AssertionError(report);
		}
		log.debug("Finished verify for files {}.", fileNames);
	}

	/**
	 * Verify database after an asynchronous test.
	 * <p>
	 * Verifies the database repeatedly until either the result is successful or timeout has expired.
	 *
	 * @param fileNames map of profile names to lists of names of verification dataset files for the profile
	 */
	public static void await(Map<String, List<String>> fileNames) {
		log.info("Performing await for files {}.", fileNames);
		Map<String, String> defaultProfileProperties = properties.get(DEFAULT_PROFILE);
		long defaultProfileLimit = Properties.getLimit(defaultProfileProperties);

		String report = Awaiting.awaitEmpty(defaultProfileLimit, () -> generateReport(fileNames));

		if (!report.isEmpty()) {
			throw new AssertionError(report);
		}
		log.debug("Finished await for files {}.", fileNames);
	}

	private static String generateReport(Map<String, List<String>> fileNames) {
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
		return Report.report(differences);
	}
}
