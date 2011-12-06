package net.sf.lightair.internal.dbunit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.sf.lightair.exception.CreateDatabaseConnectionException;
import net.sf.lightair.exception.DatabaseAccessException;
import net.sf.lightair.exception.DatabaseDriverClassNotFoundException;
import net.sf.lightair.internal.factory.Factory;
import net.sf.lightair.internal.properties.PropertiesProvider;
import net.sf.lightair.internal.properties.PropertyKeys;

import org.apache.commons.lang.time.StopWatch;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper around DbUnit.
 */
public class DbUnitWrapper implements PropertyKeys {

	private final Logger log = LoggerFactory.getLogger(DbUnitWrapper.class);

	/**
	 * Create a DbUnit connection for a given schema.
	 * <p>
	 * Pass schema <code>null</code> to use the default schema from properties.
	 * 
	 * @param schemaName
	 *            Schema to connect to, or <code>null</code> to use default
	 *            schema
	 * @return DbUnit connection
	 * @throws DatabaseDriverClassNotFoundException
	 *             When database driver class cannot be loaded
	 * @throws CreateDatabaseConnectionException
	 *             When {@link DriverManager} cannot open connection to database
	 * @throws DatabaseAccessException
	 *             When DbUnit cannot establish itself on the database
	 *             connection, typically when schema does not exist
	 */
	public IDatabaseConnection createConnection(String schemaName)
			throws DatabaseDriverClassNotFoundException,
			CreateDatabaseConnectionException, DatabaseAccessException {
		if (null == schemaName) {
			schemaName = getProperty(DEFAULT_SCHEMA);
		}
		log.info("Creating database connection for schema {}", schemaName);
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String driverClassName = getProperty(DRIVER_CLASS_NAME);
		try {
			Class.forName(driverClassName);
			Connection connection = factory.getConnection(
					getProperty(CONNECTION_URL), getProperty(USER_NAME),
					getProperty(PASSWORD));
			IDatabaseConnection dbConnection = factory
					.createDatabaseConnection(connection, schemaName);
			stopWatch.stop();
			log.debug("Created database connection for schema {} in {} ms.",
					schemaName, stopWatch.getTime());
			return dbConnection;
		} catch (ClassNotFoundException e) {
			throw new DatabaseDriverClassNotFoundException(driverClassName, e);
		} catch (SQLException e) {
			throw new CreateDatabaseConnectionException(e);
		} catch (DatabaseUnitException e) {
			throw new DatabaseAccessException(e);
		}
	}

	private String getProperty(String key) {
		return propertiesProvider.getProperty(key);
	}

	// beans and their setters;

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
