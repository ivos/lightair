package unit.internal.dbunit;

import static org.junit.Assert.*;
import net.sf.lightair.internal.dbunit.ConnectionFactory;
import net.sf.lightair.internal.dbunit.DbUnitWrapper;
import net.sf.lightair.internal.properties.PropertiesProvider;
import net.sf.lightair.internal.properties.PropertyKeys;
import net.sf.seaf.test.jmock.JMockSupport;

import org.dbunit.database.IDatabaseConnection;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

public class DbUnitWrapperTest extends JMockSupport {

	DbUnitWrapper w;
	ConnectionFactory connectionFactory;
	PropertiesProvider propertiesProvider;
	IDatabaseConnection connection1, connection2, connection3;

	@Before
	public void setup() {
		w = new DbUnitWrapper();
		connectionFactory = mock(ConnectionFactory.class);
		w.setConnectionFactory(connectionFactory);
		propertiesProvider = mock(PropertiesProvider.class);
		w.setPropertiesProvider(propertiesProvider);
		connection1 = mock(IDatabaseConnection.class, "connection1");
		connection2 = mock(IDatabaseConnection.class, "connection2");
		connection3 = mock(IDatabaseConnection.class, "connection3");
	}

	@Test
	public void getConnection_CreateNew_SchemaExplicit() {
		check(new Expectations() {
			{
				one(connectionFactory).createConnection("schemaName1");
				will(returnValue(connection1));
			}
		});

		final IDatabaseConnection actual = w.getConnection("schemaName1");

		assertSame(connection1, actual);
	}

	@Test
	public void getConnection_CreateNew_SchemaNull() {
		check(new Expectations() {
			{
				one(propertiesProvider)
						.getProperty(PropertyKeys.DEFAULT_SCHEMA);
				will(returnValue("schemaName2"));

				one(connectionFactory).createConnection("schemaName2");
				will(returnValue(connection1));
			}
		});

		final IDatabaseConnection actual = w.getConnection(null);

		assertSame(connection1, actual);
	}

	// @Test
	// unitils caching of dbunit connections does not work with multiple schemas
	public void getConnection_FromCache() {
		check(new Expectations() {
			{
				one(connectionFactory).createConnection("schemaName1");
				will(returnValue(connection1));
			}
		});

		IDatabaseConnection connection11 = w.getConnection("schemaName1");
		assertSame(connection1, connection11);

		IDatabaseConnection connection12 = w.getConnection("schemaName1");
		assertSame(connection1, connection12);

		IDatabaseConnection connection13 = w.getConnection("schemaName1");
		assertSame(connection1, connection13);
	}

	// @Test
	// unitils caching of dbunit connections does not work with multiple schemas
	public void getConnection_FromCache_SeparateBySchema() {
		check(new Expectations() {
			{
				one(connectionFactory).createConnection("schemaName1");
				will(returnValue(connection1));

				one(connectionFactory).createConnection("schemaName2");
				will(returnValue(connection2));

				one(connectionFactory).createConnection("schemaName3");
				will(returnValue(connection3));
			}
		});

		// schema 1

		IDatabaseConnection connection11 = w.getConnection("schemaName1");
		assertSame(connection1, connection11);

		IDatabaseConnection connection12 = w.getConnection("schemaName1");
		assertSame(connection1, connection12);

		IDatabaseConnection connection13 = w.getConnection("schemaName1");
		assertSame(connection1, connection13);

		// schema 2

		IDatabaseConnection connection21 = w.getConnection("schemaName2");
		assertSame(connection2, connection21);

		IDatabaseConnection connection22 = w.getConnection("schemaName2");
		assertSame(connection2, connection22);

		IDatabaseConnection connection23 = w.getConnection("schemaName2");
		assertSame(connection2, connection23);

		// schema 3

		IDatabaseConnection connection31 = w.getConnection("schemaName3");
		assertSame(connection3, connection31);

		IDatabaseConnection connection32 = w.getConnection("schemaName3");
		assertSame(connection3, connection32);

		IDatabaseConnection connection33 = w.getConnection("schemaName3");
		assertSame(connection3, connection33);
	}

}
