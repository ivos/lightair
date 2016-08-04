package unit.internal.dbunit;

import net.sf.lightair.internal.dbunit.DbUnitWrapper;
import net.sf.lightair.internal.factory.Factory;
import org.dbunit.database.IDatabaseConnection;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Ignore
public class DbUnitWrapperITest {

	DbUnitWrapper w = Factory.getInstance().getDbUnitWrapper();

	@Test
	public void createConnection_SchemaPassed() throws Exception {
		IDatabaseConnection connection = w.getConnection(null, "schema-name");
		assertNotNull("Created", connection);
		assertEquals("Schema name matches", "schema-name", connection
				.getSchema().toLowerCase());
	}

	@Test
	public void createConnection_SchemaNull() throws Exception {
		IDatabaseConnection connection = w.getConnection(null, null);
		assertNotNull("Created", connection);
		String schema = connection.getSchema();
		assertNotNull("Schema not null", schema);
		assertEquals("Schema name matches", "public", schema.toLowerCase());
	}

}
