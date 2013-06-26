package unit.internal.dbunit;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import net.sf.lightair.exception.CreateDatabaseConnectionException;
import net.sf.lightair.exception.DatabaseAccessException;
import net.sf.lightair.exception.DatabaseDriverClassNotFoundException;
import net.sf.lightair.internal.dbunit.DbUnitWrapper;
import net.sf.lightair.internal.factory.Factory;
import net.sf.lightair.internal.properties.PropertiesProvider;
import net.sf.lightair.internal.properties.PropertyKeys;
import net.sf.seaf.test.jmock.JMockSupport;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.statement.PreparedStatementFactory;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

public class DbUnitWrapperTest extends JMockSupport implements PropertyKeys {

	DbUnitWrapper w;
	PropertiesProvider propertiesProvider;
	Connection connection;
	Factory factory;
	IDatabaseConnection dbc;
	DatabaseConfig config;
	Set<String> featureNames;
	Set<String> propertyNames;

	@Before
	public void before() {
		connection = mock(Connection.class);
		w = new DbUnitWrapper();
		propertiesProvider = mock(PropertiesProvider.class);
		w.setPropertiesProvider(propertiesProvider);
		factory = mock(Factory.class);
		w.setFactory(factory);
		dbc = mock(IDatabaseConnection.class);
		config = mock(DatabaseConfig.class);
		featureNames = new HashSet<String>();
		featureNames.add("dbunit.features.feature-name1");
		featureNames.add("dbunit.features.feature-name2");
		featureNames.add("dbunit.features.feature-name3");
		propertyNames = new HashSet<String>();
		propertyNames.add("dbunit.properties.property-name1");
		propertyNames.add("dbunit.properties.property-name2");
		propertyNames.add("dbunit.properties.property-name3");
	}

	@Test
	public void createConnection_SchemaPassed() throws DatabaseUnitException,
			SQLException {
		checkCommons();
		check(new Expectations() {
			{
				one(factory).createDatabaseConnection(connection, "schema1");
				will(returnValue(dbc));
			}
		});
		checkSetFeaturesAndProperties();

		assertSame(dbc, w.createConnection("schema1"));
	}

	@Test
	public void createConnection_SchemaNull() throws DatabaseUnitException,
			SQLException {
		checkCommons();
		check(new Expectations() {
			{
				one(propertiesProvider).getProperty(DEFAULT_SCHEMA);
				will(returnValue("def_sch1"));

				one(factory).createDatabaseConnection(connection, "def_sch1");
				will(returnValue(dbc));
			}
		});
		checkSetFeaturesAndProperties();

		assertSame(dbc, w.createConnection(null));
	}

	private void checkCommons() throws DatabaseUnitException, SQLException {
		check(new Expectations() {
			{
				one(propertiesProvider).getProperty(DRIVER_CLASS_NAME);
				will(returnValue("java.lang.String"));

				one(propertiesProvider).getProperty(CONNECTION_URL);
				will(returnValue("url1"));

				one(propertiesProvider).getProperty(USER_NAME);
				will(returnValue("user1"));

				one(propertiesProvider).getProperty(PASSWORD);
				will(returnValue("pass1"));

				one(factory).getConnection("url1", "user1", "pass1");
				will(returnValue(connection));
			}
		});
	}

	private void checkSetFeaturesAndProperties() throws DatabaseUnitException,
			SQLException {
		check(new Expectations() {
			{
				one(dbc).getConfig();
				will(returnValue(config));

				one(propertiesProvider).getDbUnitFeatureNames();
				will(returnValue(featureNames));

				one(propertiesProvider).getProperty(
						"dbunit.features.feature-name1");
				will(returnValue("true"));

				one(config).setProperty(
						"http://www.dbunit.org/features/feature-name1", true);

				one(propertiesProvider).getProperty(
						"dbunit.features.feature-name2");
				will(returnValue("false"));

				one(config).setProperty(
						"http://www.dbunit.org/features/feature-name2", false);

				one(propertiesProvider).getProperty(
						"dbunit.features.feature-name3");
				will(returnValue("true"));

				one(config).setProperty(
						"http://www.dbunit.org/features/feature-name3", true);

				one(propertiesProvider).getDbUnitPropertyNames();
				will(returnValue(propertyNames));

				one(propertiesProvider).getProperty(
						"dbunit.properties.property-name1");
				will(returnValue("property-value1"));

				one(config).setProperty(
						"http://www.dbunit.org/properties/property-name1",
						"property-value1");

				one(propertiesProvider).getProperty(
						"dbunit.properties.property-name2");
				will(returnValue("org.dbunit.ext.h2.H2DataTypeFactory"));

				one(config)
						.setProperty(
								with(equal("http://www.dbunit.org/properties/property-name2")),
								with(any(H2DataTypeFactory.class)));

				one(propertiesProvider).getProperty(
						"dbunit.properties.property-name3");
				will(returnValue("org.dbunit.database.statement.PreparedStatementFactory"));

				one(config)
						.setProperty(
								with(equal("http://www.dbunit.org/properties/property-name3")),
								with(any(PreparedStatementFactory.class)));
			}
		});
	}

	@Test
	public void createConnection_ClassNotFoundException()
			throws DatabaseUnitException {
		check(new Expectations() {
			{
				one(propertiesProvider).getProperty(DRIVER_CLASS_NAME);
				will(returnValue("java.lang.NonExistentClass"));
			}
		});

		try {
			w.createConnection("schema1");
			fail("Should throw");
		} catch (DatabaseDriverClassNotFoundException e) {
			assertEquals("Message",
					"Driver class was not found: java.lang.NonExistentClass",
					e.getMessage());
			assertEquals("Cause class", ClassNotFoundException.class, e
					.getCause().getClass());
		}
	}

	@Test
	public void createConnection_SQLException() throws SQLException,
			DatabaseUnitException {
		final SQLException cause = new SQLException();
		check(new Expectations() {
			{
				one(propertiesProvider).getProperty(DRIVER_CLASS_NAME);
				will(returnValue("java.lang.String"));

				one(propertiesProvider).getProperty(CONNECTION_URL);
				will(returnValue("url1"));

				one(propertiesProvider).getProperty(USER_NAME);
				will(returnValue("user1"));

				one(propertiesProvider).getProperty(PASSWORD);
				will(returnValue("pass1"));

				one(factory).getConnection("url1", "user1", "pass1");
				will(throwException(cause));
			}
		});

		try {
			w.createConnection("schema1");
			fail("Should throw");
		} catch (CreateDatabaseConnectionException e) {
			assertEquals("Message", "Cannot connect to database.",
					e.getMessage());
			assertSame("Cause", cause, e.getCause());
		}
	}

	@Test
	public void createConnection_DatabaseUnitException() throws SQLException,
			DatabaseUnitException {
		final DatabaseUnitException cause = new DatabaseUnitException();
		checkCommons();
		check(new Expectations() {
			{
				one(factory).createDatabaseConnection(connection, "schema1");
				will(throwException(cause));
			}
		});

		try {
			w.createConnection("schema1");
			fail("Should throw");
		} catch (DatabaseAccessException e) {
			assertEquals("Message", "Error accessing database.", e.getMessage());
			assertSame("Cause", cause, e.getCause());
		}
	}
}
