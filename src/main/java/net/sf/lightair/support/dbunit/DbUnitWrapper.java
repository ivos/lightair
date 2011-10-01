package net.sf.lightair.support.dbunit;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.sf.lightair.exception.IllegalDataSetContentException;
import net.sf.lightair.support.properties.PropertiesProvider;
import net.sf.lightair.support.properties.PropertyKeys;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;

/**
 * Wrapper around DbUnit.
 */
public class DbUnitWrapper implements PropertyKeys {

	/**
	 * Create a DbUnit connection.
	 * 
	 * @return DbUnit connection
	 * @throws ClassNotFoundException
	 *             When database driver class cannot be loaded
	 * @throws SQLException
	 *             When {@link DriverManager} cannot open connection to database
	 * @throws DatabaseUnitException
	 *             When DbUnit cannot establish itself on the database
	 *             connection, typically when schema does not exist
	 */
	public IDatabaseConnection createConnection()
			throws ClassNotFoundException, SQLException, DatabaseUnitException {
		Class.forName(getProperty(DRIVER_CLASS_NAME));
		Connection connection = DriverManager.getConnection(
				getProperty(CONNECTION_URL), getProperty(USER_NAME),
				getProperty(PASSWORD));
		return new DatabaseConnection(connection, getProperty(SCHEMA));
	}

	/**
	 * Load DbUnit data set from a file.
	 * <p>
	 * Return null iff file does not exist.
	 * 
	 * @param testClass
	 *            Test class gives package holding the data set
	 * @param fileName
	 *            Data set file name
	 * @return Loaded DbUnit data set or null iff it does not exist
	 * @throws IllegalDataSetContentException
	 *             When data set file is found but its content is illegal
	 */
	public IDataSet loadDataSetIfExists(Class<?> testClass, String fileName)
			throws IllegalDataSetContentException {
		InputStream stream = testClass.getResourceAsStream(fileName);
		if (null == stream) {
			return null;
		}
		try {
			return new FlatXmlDataSetBuilder().build(stream);
		} catch (DataSetException e) {
			throw new IllegalDataSetContentException(fileName, e);
		}
	}

	private String getProperty(String key) {
		return getProperties().getProperty(key);
	}

	private PropertiesProvider properties;

	public PropertiesProvider getProperties() {
		if (null == properties) {
			properties = new PropertiesProvider();
		}
		return properties;
	}

}
