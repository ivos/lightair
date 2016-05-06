package net.sf.lightair.internal.functional;

import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import org.unitils.core.dbsupport.DbSupportFactory;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import org.unitils.core.dbsupport.SQLHandler;

import net.sf.lightair.exception.CreateDatabaseConnectionException;
import net.sf.lightair.exception.DatabaseDriverClassNotFoundException;
import net.sf.lightair.exception.PropertiesNotFoundException;
import net.sf.lightair.internal.dbmaintainer.XsdDataSetStructureGenerator;
import net.sf.lightair.internal.factory.Factory;
import net.sf.lightair.internal.properties.PropertiesProvider;
import net.sf.lightair.internal.properties.PropertyKeys;

public class XsdGenerator implements PropertyKeys {

	private final Logger log = LoggerFactory.getLogger(Factory.class);

	public void generate(File lightAirProperties, File xsdDir) {
		log.info("Generating DbUnit flat dataset XSD files...");

		PropertiesProvider propertiesProvider = getLightairPropertiesProvider(lightAirProperties);

		for (String profile : propertiesProvider.getProfileNames()) {
			processProfile(xsdDir, propertiesProvider, profile);
		}
		generateLightAirTypes(xsdDir);

		log.info("Finished generating DbUnit flat dataset XSD files.");
	}

	private PropertiesProvider getLightairPropertiesProvider(File lightAirProperties) {
		FileSystemPropertiesProvider propertiesProvider = new FileSystemPropertiesProvider(lightAirProperties);
		propertiesProvider.initFromFileSystem();
		return propertiesProvider;
	}

	private void processProfile(File xsdDir, PropertiesProvider propertiesProvider, String profile) {
		log.debug("Generating XSD for profile [" + profile + "]");

		Properties configuration = getUnitilsConfiguration(xsdDir, propertiesProvider, profile);

		DataSource dataSource = createDataSource(propertiesProvider, profile);
		SQLHandler sqlHandler = new DefaultSQLHandler(dataSource, false);

		XsdDataSetStructureGenerator generator = new XsdDataSetStructureGenerator(profile);
		generator.init(configuration, sqlHandler);
		generator.generateDataSetStructure();
	}

	/**
	 * Create Unitils configuration.
	 * <p>
	 * Load Unitils default values. Add support for H2 database. Finally, set
	 * our own configuration: database dialect, schema names and output
	 * directory.
	 * 
	 * @return
	 */
	private Properties getUnitilsConfiguration(File xsdDir, PropertiesProvider propertiesProvider, String profile) {
		Properties configuration = new Properties();
		loadUnitilsDefaultValues(configuration);
		addSupportForH2Database(configuration);
		addSupportForInformix(configuration);

		configuration.put(DbSupportFactory.PROPKEY_DATABASE_DIALECT,
				propertiesProvider.getProperty(profile, DATABASE_DIALECT));
		configuration.put(DbSupportFactory.PROPKEY_DATABASE_SCHEMA_NAMES,
				propertiesProvider.getProperty(profile, SCHEMA_NAMES));
		configuration.put(XsdDataSetStructureGenerator.PROPKEY_XSD_DIR_NAME, getXsdDirCanonical(xsdDir));

		return configuration;
	}

	/**
	 * Load default Unitils values.
	 * 
	 * @param configuration
	 */
	private void loadUnitilsDefaultValues(Properties configuration) {
		new ConfigurationLoader() {
			@Override
			public void loadDefaultConfiguration(Properties properties) {
				super.loadDefaultConfiguration(properties);
			}
		}.loadDefaultConfiguration(configuration);
	}

	/**
	 * Add custom support for H2 database into Unitils configuration, as Unitils
	 * does not support it out-of-the-box.
	 * 
	 * @param configuration
	 */
	private void addSupportForH2Database(Properties configuration) {
		configuration.put("org.unitils.core.dbsupport.DbSupport.implClassName.h2",
				"org.unitils.core.dbsupport.H2DbSupport");
		configuration.put("org.dbunit.dataset.datatype.IDataTypeFactory.implClassName",
				"org.dbunit.ext.h2.H2DataTypeFactory");
		configuration.put("database.identifierQuoteString.h2", "auto");
		configuration.put("database.storedIndentifierCase.h2", "auto");
	}

	/**
	 * Add custom support for Informix into Unitils configuration, as Unitils
	 * does not support it out-of-the-box.
	 * 
	 * @param configuration
	 */
	private void addSupportForInformix(Properties configuration) {
		configuration.put("org.unitils.core.dbsupport.DbSupport.implClassName.informix",
				"org.unitils.core.dbsupport.InformixDbSupport");
		configuration.put("org.dbunit.dataset.datatype.IDataTypeFactory.implClassName",
				"org.dbunit.dataset.datatype.DefaultDataTypeFactory");
		configuration.put("database.identifierQuoteString.informix", "auto");
		configuration.put("database.storedIndentifierCase.informix", "auto");
	}

	/**
	 * Convert XSD output directory into canonical file name.
	 * 
	 * @return
	 */
	private String getXsdDirCanonical(File xsdDir) {
		try {
			final String canonicalPath = xsdDir.getCanonicalPath();
			log.debug("Expanded XSD directory specification [" + xsdDir.getPath() + "] as [" + canonicalPath + "].");
			return canonicalPath;
		} catch (IOException e) {
			throw new PropertiesNotFoundException(xsdDir.getPath());
		}
	}

	/**
	 * Create a datasource to connect to the database.
	 * 
	 * @param profile
	 * @return
	 */
	private DataSource createDataSource(PropertiesProvider propertiesProvider, String profile) {
		SingleConnectionDataSource dataSource;
		log.info("Creating database connection for profile [" + profile + "]...");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String driverClassName = getProperty(propertiesProvider, profile, DRIVER_CLASS_NAME);
		try {
			Class.forName(driverClassName);
			final String url = getProperty(propertiesProvider, profile, CONNECTION_URL);
			final String username = getProperty(propertiesProvider, profile, USER_NAME);
			final String password = getProperty(propertiesProvider, profile, PASSWORD);
			Connection connection = DriverManager.getConnection(url, username, password);
			dataSource = new SingleConnectionDataSource(connection, true);
			stopWatch.stop();
			log.debug("Connection driver=[" + driverClassName + "], url=[" + url + "], username=[" + username
					+ "], password=[" + password + "].");
			log.debug("Created database connection for profile [" + profile + "] in " + stopWatch.getTime() + " ms.");
		} catch (ClassNotFoundException e) {
			throw new DatabaseDriverClassNotFoundException(driverClassName, e);
		} catch (SQLException e) {
			throw new CreateDatabaseConnectionException(e);
		}
		return dataSource;
	}

	private String getProperty(PropertiesProvider propertiesProvider, String profile, String key) {
		return propertiesProvider.getProperty(profile, key);
	}

	private void generateLightAirTypes(File xsdDir) {
		Writer writer = null;
		final File file = new File(xsdDir, "light-air-types.xsd");
		try {
			writer = new BufferedWriter(new FileWriter(file));
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("light-air-types.xsd");
			IOUtils.copy(inputStream, writer);
		} catch (Exception e) {
			throw new UnitilsException("Error generating xsd file: " + file, e);
		} finally {
			closeQuietly(writer);
		}
	}

}
