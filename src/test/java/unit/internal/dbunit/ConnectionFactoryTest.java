package unit.internal.dbunit;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import net.sf.lightair.internal.dbunit.AutoPreparedStatementFactory;
import net.sf.lightair.internal.dbunit.ConnectionFactory;
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

public class ConnectionFactoryTest extends JMockSupport implements PropertyKeys {

	ConnectionFactory cf;
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
		cf = new ConnectionFactory();
		propertiesProvider = mock(PropertiesProvider.class);
		cf.setPropertiesProvider(propertiesProvider);
		factory = mock(Factory.class);
		cf.setFactory(factory);
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
	public void createConnection_Ok() throws DatabaseUnitException,
			SQLException {
		check(new Expectations() {
			{
				one(factory).createDatabaseConnection("profile1", "schema1");
				will(returnValue(dbc));
			}
		});
		checkSetDatabaseDialect();
		checkSetFeaturesAndProperties();

		assertSame(dbc, cf.createConnection("profile1", "schema1"));
	}

	private void checkSetDatabaseDialect() throws DatabaseUnitException,
			SQLException {
		check(new Expectations() {
			{
				one(dbc).getConfig();
				will(returnValue(config));

				one(propertiesProvider).getProperty("profile1",
						DATABASE_DIALECT);
				will(returnValue("h2"));

				one(config)
						.setProperty(
								with(equal("http://www.dbunit.org/properties/datatypeFactory")),
								with(any(H2DataTypeFactory.class)));

				one(config)
						.setProperty(
								with(equal("http://www.dbunit.org/properties/statementFactory")),
								with(any(AutoPreparedStatementFactory.class)));

				one(config).setProperty(
						"http://www.dbunit.org/properties/tableType",
						new String[] { "TABLE", "VIEW" });
			}
		});
	}

	private void checkSetFeaturesAndProperties() throws DatabaseUnitException,
			SQLException {
		check(new Expectations() {
			{
				one(propertiesProvider).getDbUnitFeatureNames("profile1");
				will(returnValue(featureNames));

				one(propertiesProvider).getProperty("profile1",
						"dbunit.features.feature-name1");
				will(returnValue("true"));

				one(config).setProperty(
						"http://www.dbunit.org/features/feature-name1", true);

				one(propertiesProvider).getProperty("profile1",
						"dbunit.features.feature-name2");
				will(returnValue("false"));

				one(config).setProperty(
						"http://www.dbunit.org/features/feature-name2", false);

				one(propertiesProvider).getProperty("profile1",
						"dbunit.features.feature-name3");
				will(returnValue("true"));

				one(config).setProperty(
						"http://www.dbunit.org/features/feature-name3", true);

				one(propertiesProvider).getDbUnitPropertyNames("profile1");
				will(returnValue(propertyNames));

				one(propertiesProvider).getProperty("profile1",
						"dbunit.properties.property-name1");
				will(returnValue("property-value1"));

				one(config).setProperty(
						"http://www.dbunit.org/properties/property-name1",
						"property-value1");

				one(propertiesProvider).getProperty("profile1",
						"dbunit.properties.property-name2");
				will(returnValue("org.dbunit.ext.h2.H2DataTypeFactory"));

				one(config)
						.setProperty(
								with(equal("http://www.dbunit.org/properties/property-name2")),
								with(any(H2DataTypeFactory.class)));

				one(propertiesProvider).getProperty("profile1",
						"dbunit.properties.property-name3");
				will(returnValue("org.dbunit.database.statement.PreparedStatementFactory"));

				one(config)
						.setProperty(
								with(equal("http://www.dbunit.org/properties/property-name3")),
								with(any(PreparedStatementFactory.class)));
			}
		});
	}

}
