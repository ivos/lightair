package net.sf.lightair.support;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import net.sf.lightair.annotation.Verify;
import net.sf.lightair.exception.DataSetNotFoundException;

import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

public class VerifyTestRule implements TestRule {

	private final TestClass testClass;
	private Verify verify;
	private final String classVerifyFileName, methodVerifyFileName;

	private final Properties properties;

	public VerifyTestRule(TestClass testClass, FrameworkMethod method) {
		this.testClass = testClass;
		verify = method.getAnnotation(Verify.class);
		if (null == verify) {
			verify = testClass.getJavaClass().getAnnotation(Verify.class);
		}
		classVerifyFileName = testClass.getJavaClass().getSimpleName()
				+ "-verify.xml";
		methodVerifyFileName = testClass.getJavaClass().getSimpleName() + '.'
				+ method.getName() + "-verify.xml";
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
				base.evaluate();
				verify();
			}
		};
	}

	private IDatabaseConnection connection;
	private IDataSet dataSet;

	protected void verify() throws ClassNotFoundException, SQLException,
			DatabaseUnitException, Exception {
		if (null != verify) {
			connection = createDbUnitConnection();
			dataSet = loadDataSet(methodVerifyFileName);
			if (null == dataSet) {
				dataSet = loadDataSet(classVerifyFileName);
			}
			if (null == dataSet) {
				throw new DataSetNotFoundException(classVerifyFileName,
						methodVerifyFileName);
			}
			if (null != dataSet) {
				try {
					Assertion.assertEquals(dataSet, connection.createDataSet());
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
