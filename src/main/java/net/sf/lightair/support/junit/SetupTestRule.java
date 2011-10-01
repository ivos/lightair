package net.sf.lightair.support.junit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import net.sf.lightair.annotation.Setup;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

public class SetupTestRule implements TestRule {

	private final TestClass testClass;
	private Setup setup;
	private final String classSetupFileName, methodSetupFileName;

	private final Properties properties;

	public SetupTestRule(TestClass testClass, FrameworkMethod method) {
		this.testClass = testClass;
		setup = method.getAnnotation(Setup.class);
		if (null == setup) {
			setup = testClass.getJavaClass().getAnnotation(Setup.class);
		}
		classSetupFileName = testClass.getJavaClass().getSimpleName() + ".xml";
		methodSetupFileName = testClass.getJavaClass().getSimpleName() + '.'
				+ method.getName() + ".xml";
		properties = new Properties();
		try {
			URLConnection urlConnection = getClass().getClassLoader()
					.getResource("light-air.properties").openConnection();
			urlConnection.setUseCaches(false);
			InputStream is = urlConnection.getInputStream();
			properties.load(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Statement apply(final Statement base, Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				setup();
				base.evaluate();
			}
		};
	}

	private IDatabaseConnection connection;
	private IDataSet dataSet;

	protected void setup() throws ClassNotFoundException, SQLException,
			DatabaseUnitException, Exception {
		if (null != setup) {
			connection = createDbUnitConnection();
			dataSet = loadDataSet(methodSetupFileName);
			if (null == dataSet) {
				dataSet = loadDataSet(classSetupFileName);
			}
			if (null != dataSet) {
				try {
					DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
				} finally {
					connection.close();
				}
			}
		}
	}

	private IDataSet loadDataSet(String fileName) {
		try {
			return new FlatXmlDataSetBuilder().build(testClass.getJavaClass()
					.getResourceAsStream(fileName));
		} catch (DataSetException e) {
			return null;
		}
	}

	private IDatabaseConnection createDbUnitConnection()
			throws ClassNotFoundException, SQLException, DatabaseUnitException {
		Class.forName(getProperty("driverClassName"));
		Connection connection = DriverManager.getConnection(
				getProperty("connectionUrl"), getProperty("userName"),
				getProperty("password"));
		return new DatabaseConnection(connection, getProperty("schema"));
	}

	private String getProperty(String key) {
		return properties.getProperty("light-air." + key).trim();
	}

}
