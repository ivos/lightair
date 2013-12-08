package net.sf.lightair.internal.dbunit;

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

	// private final Map<String, IDatabaseConnection> connectionCache = new
	// HashMap<String, IDatabaseConnection>();

	// unitils caching of dbunit connections does not work with multiple
	// schemas, why?

	/**
	 * Retrieve a DbUnit connection for a given schema.
	 * <p>
	 * Pass schema <code>null</code> to use the default schema from properties.
	 * 
	 * @param profile
	 *            Profile
	 * @param schemaName
	 *            Schema to connect to, or <code>null</code> to use default
	 *            schema
	 * @return DbUnit connection
	 */
	public IDatabaseConnection getConnection(String profile, String schemaName) {
		log.debug("Retrieving connection for schema {}.", schemaName);

		if (null == schemaName) {
			schemaName = propertiesProvider
					.getProperty(profile, DEFAULT_SCHEMA);
			log.debug("Resolved unspecified schema as default {}.", schemaName);
		}

		IDatabaseConnection connection;// = connectionCache.get(schemaName);
		// if (null == connection) {
		log.debug("Creating new connection for schema {}.", schemaName);
		connection = connectionFactory.createConnection(profile, schemaName);
		// connectionCache.put(schemaName, connection);
		// } else {
		// log.debug("Retrieved connection from cache for schema {}.",
		// schemaName);
		// }
		return connection;
	}

	public void resetConnectionCache() {
		// connectionCache.clear();
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
