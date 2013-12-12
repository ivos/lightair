package net.sf.lightair.internal.dbunit;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.lightair.exception.CreateDatabaseConnectionException;
import net.sf.lightair.exception.DatabaseAccessException;
import net.sf.lightair.internal.factory.Factory;
import net.sf.lightair.internal.properties.PropertiesProvider;
import net.sf.lightair.internal.properties.PropertyKeys;

import org.apache.commons.lang.time.StopWatch;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates DbUnit connections to database.
 */
public class ConnectionFactory implements PropertyKeys {

	private final Logger log = LoggerFactory.getLogger(ConnectionFactory.class);

	/**
	 * Create a DbUnit connection for a given schema.
	 * <p>
	 * Pass schema <code>null</code> to use the default schema from properties.
	 * 
	 * @param profile
	 *            Profile
	 * @param schemaName
	 *            Schema to connect to, or <code>null</code> to use default
	 *            schema
	 * @return DbUnit connection
	 * @throws CreateDatabaseConnectionException
	 *             When connection to database cannot be open
	 * @throws DatabaseAccessException
	 *             When DbUnit cannot establish itself on the database
	 *             connection, typically when schema does not exist
	 */
	public IDatabaseConnection createConnection(String profile,
			String schemaName) throws DatabaseAccessException,
			CreateDatabaseConnectionException {
		log.info("Creating database connection for schema {}.", schemaName);
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		IDatabaseConnection connection = factory.createDatabaseConnection(
				profile, schemaName);
		DatabaseConfig config = connection.getConfig();
		setDatabaseDialect(profile, config);
		setStatementFactory(config);
		setFeaturesAndProperties(profile, config);

		stopWatch.stop();
		log.debug("Created database connection for schema {} in {} ms.",
				schemaName, stopWatch.getTime());
		return connection;
	}

	private void setDatabaseDialect(String profile, DatabaseConfig config) {
		String dbUnitName = "http://www.dbunit.org/properties/datatypeFactory";
		String dialect = propertiesProvider.getProperty(profile,
				DATABASE_DIALECT);
		String className = DATATYPE_FACTORIES.get(dialect);
		Object value = convertPropertyValue(className);
		log.debug(
				"Setting database dialect to {}, DbUnit datatype factory will be {}.",
				dialect, className);
		config.setProperty(dbUnitName, value);
	}

	private void setStatementFactory(DatabaseConfig config) {
		config.setProperty(DatabaseConfig.PROPERTY_STATEMENT_FACTORY, Factory
				.getInstance().getStatementFactory());
	}

	private static final Map<String, String> DATATYPE_FACTORIES = new HashMap<String, String>();
	{
		DATATYPE_FACTORIES.put("h2", "org.dbunit.ext.h2.H2DataTypeFactory");
		DATATYPE_FACTORIES.put("oracle",
				"org.dbunit.ext.oracle.OracleDataTypeFactory");
		DATATYPE_FACTORIES.put("oracle9",
				"org.dbunit.ext.oracle.OracleDataTypeFactory");
		DATATYPE_FACTORIES.put("oracle10",
				"org.dbunit.ext.oracle.Oracle10DataTypeFactory");
		DATATYPE_FACTORIES.put("hsqldb",
				"org.dbunit.ext.hsqldb.HsqldbDataTypeFactory");
		DATATYPE_FACTORIES.put("mysql",
				"org.dbunit.ext.mysql.MySqlDataTypeFactory");
		DATATYPE_FACTORIES.put("db2", "org.dbunit.ext.db2.Db2DataTypeFactory");
		DATATYPE_FACTORIES.put("postgresql",
				"org.dbunit.dataset.datatype.DefaultDataTypeFactory");
		DATATYPE_FACTORIES.put("derby",
				"org.dbunit.dataset.datatype.DefaultDataTypeFactory");
		DATATYPE_FACTORIES.put("mssql",
				"org.dbunit.ext.mssql.MsSqlDataTypeFactory");
		DATATYPE_FACTORIES.put("informix",
				"org.dbunit.dataset.datatype.DefaultDataTypeFactory");
	}

	private void setFeaturesAndProperties(String profile, DatabaseConfig config) {
		Set<String> featureNames = propertiesProvider
				.getDbUnitFeatureNames(profile);
		for (String featureName : featureNames) {
			String dbUnitName = "http://www.dbunit.org/features/"
					+ featureName.substring(16);
			Boolean value = Boolean.valueOf(propertiesProvider.getProperty(
					profile, featureName));
			log.debug("Setting DbUnit feature {} to {}.", dbUnitName, value);
			config.setProperty(dbUnitName, value);
		}

		Set<String> propertyNames = propertiesProvider
				.getDbUnitPropertyNames(profile);
		for (String propertyName : propertyNames) {
			String dbUnitName = "http://www.dbunit.org/properties/"
					+ propertyName.substring(18);
			String string = propertiesProvider.getProperty(profile,
					propertyName);
			Object value = convertPropertyValue(string);
			log.debug("Setting DbUnit property {} to {}.", dbUnitName, value);
			config.setProperty(dbUnitName, value);
		}
	}

	private Object convertPropertyValue(String value) {
		try {
			return Class.forName(value).newInstance();
		} catch (Exception e) {
			return value;
		}
	}

	// beans and their setters

	private PropertiesProvider propertiesProvider;

	/**
	 * Set property provider.
	 * 
	 * @param propertiesProvider
	 *            Property provider
	 */
	public void setPropertiesProvider(PropertiesProvider propertiesProvider) {
		this.propertiesProvider = propertiesProvider;
	}

	private Factory factory;

	/**
	 * Set factory.
	 * 
	 * @param factory
	 *            Factory
	 */
	public void setFactory(Factory factory) {
		this.factory = factory;
	}

}
