package unit.internal.dbunit;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import net.sf.lightair.exception.DatabaseAccessException;
import net.sf.lightair.exception.CreateDatabaseConnectionException;
import net.sf.lightair.exception.DatabaseDriverClassNotFoundException;
import net.sf.lightair.internal.dbunit.DbUnitWrapper;
import net.sf.lightair.internal.factory.Factory;
import net.sf.lightair.internal.properties.PropertiesProvider;
import net.sf.lightair.internal.properties.PropertyKeys;
import net.sf.seaf.test.jmock.JMockSupport;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

public class DbUnitWrapperTest extends JMockSupport implements PropertyKeys {

	DbUnitWrapper w;
	PropertiesProvider propertiesProvider;
	Connection connection;
	Factory factory;
	IDatabaseConnection dbc;

	@Before
	public void before() {
		connection = mock(Connection.class);
		w = new DbUnitWrapper();
		propertiesProvider = mock(PropertiesProvider.class);
		w.setPropertiesProvider(propertiesProvider);
		factory = mock(Factory.class);
		w.setFactory(factory);
		dbc = mock(IDatabaseConnection.class);
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
