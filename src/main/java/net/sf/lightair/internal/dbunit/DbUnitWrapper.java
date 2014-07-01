package net.sf.lightair.internal.dbunit;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.sf.lightair.internal.properties.PropertiesProvider;
import net.sf.lightair.internal.properties.PropertyKeys;

import org.dbunit.database.IDatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper around DbUnit.
 */
public class DbUnitWrapper implements PropertyKeys {

	private final Logger log = LoggerFactory.getLogger(DbUnitWrapper.class);

	private final Map<String, IDatabaseConnection> connectionCache = new HashMap<String, IDatabaseConnection>();

	/**
	 * Retrieve a DbUnit connection for a given schema.
	 * <p>
	 * Pass schema <code>null</code> to use the default schema from properties.
	 * 
	 * @param profile
	 *            Profile
	 * @param schemaName
	 *            Schema to connect to, or <code>null</code> to use default schema
	 * @return DbUnit connection
	 */
	public IDatabaseConnection getConnection(String profile, String schemaName) {
		log.debug("Retrieving connection for profile {}, schema {}.", profile, schemaName);

		if (null == schemaName) {
			schemaName = propertiesProvider.getProperty(profile, DEFAULT_SCHEMA);
			log.debug("Resolved unspecified schema as default {}.", schemaName);
		}

		if (null == profile) {
			profile = "";
		}
		String cacheKey = schemaName + "-" + profile;

		IDatabaseConnection connection = connectionCache.get(cacheKey);
		if (null == connection) {
			log.debug("Creating new connection for schema {}.", schemaName);
			connection = connectionFactory.createConnection(profile, schemaName);
			connectionCache.put(cacheKey, connection);
		} else {
			log.debug("Retrieved connection from cache for profile {}, schema {}.", profile, schemaName);
		}
		return connection;
	}

	/**
	 * Closes all db connections stored in cache, clears the cache afterwards.
	 */
	public void resetConnectionCache() {
		for (IDatabaseConnection databaseConnection : connectionCache.values()) {
			try {
				log.debug("Closing connection for schema {}.", databaseConnection.getSchema());
				databaseConnection.close();
			} catch (SQLException e) {
				log.debug("Can't close connection for schema {}.", databaseConnection.getSchema());
			}
		}
		connectionCache.clear();
	}

	// beans and their setters;

	private ConnectionFactory connectionFactory;

	/**
	 * Set connectionFactory.
	 * 
	 * @param connectionFactory
	 */
	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

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

}
