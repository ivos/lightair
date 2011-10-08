package net.sf.lightair.support.dbunit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.sf.lightair.support.properties.PropertiesProvider;
import net.sf.lightair.support.properties.PropertyKeys;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;

/**
 * Wrapper around DbUnit.
 */
public class DbUnitWrapper implements PropertyKeys {

	/**
	 * Create a DbUnit connection for a given schema.
	 * <p>
	 * Pass schema <code>null</code> to use the default schema from properties.
	 * 
	 * @param schemaName
	 *            Schema to connect to, or <code>null</code> to use default
	 *            schema
	 * @return DbUnit connection
	 * @throws ClassNotFoundException
	 *             When database driver class cannot be loaded
	 * @throws SQLException
	 *             When {@link DriverManager} cannot open connection to database
	 * @throws DatabaseUnitException
	 *             When DbUnit cannot establish itself on the database
	 *             connection, typically when schema does not exist
	 */
	public IDatabaseConnection createConnection(String schemaName)
			throws ClassNotFoundException, SQLException, DatabaseUnitException {
		Class.forName(getProperty(DRIVER_CLASS_NAME));
		Connection connection = DriverManager.getConnection(
				getProperty(CONNECTION_URL), getProperty(USER_NAME),
				getProperty(PASSWORD));
		if (null == schemaName) {
			schemaName = getProperty(DEFAULT_SCHEMA);
		}
		return new DatabaseConnection(connection, schemaName);
	}

	private String getProperty(String key) {
		return propertiesProvider.getProperty(key);
	}

	private PropertiesProvider propertiesProvider;

	public void setPropertiesProvider(PropertiesProvider propertiesProvider) {
		this.propertiesProvider = propertiesProvider;
	}

}
