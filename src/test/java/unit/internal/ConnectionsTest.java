package unit.internal;

import net.sf.lightair.internal.Connections;
import net.sf.lightair.internal.Keywords;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConnectionsTest implements Keywords {

	public static Map<String, String> createProperties(
		String driverClassName, String url, String userName, String password
	) {
		Map<String, String> properties = new HashMap<>();
		properties.put(DATABASE_DRIVER_CLASS_NAME, driverClassName);
		properties.put(DATABASE_CONNECTION_URL, url);
		properties.put(DATABASE_USER_NAME, userName);
		properties.put(DATABASE_PASSWORD, password);
		return properties;
	}

	@Test
	public void test() throws SQLException {
		Map<String, Map<String, String>> properties = new HashMap<>();
		properties.put("profile1", createProperties("org.h2.Driver", "jdbc:h2:mem:test", "sa", ""));
		properties.put("profile2", createProperties("org.hsqldb.jdbc.JDBCDriver", "jdbc:hsqldb:mem:test", "sa", ""));

		Map<String, Connection> connections = Connections.open(properties);

		Connection h2 = connections.get("profile1");
		assertNotNull(h2, "H2 connected");
		assertFalse(h2.isClosed(), "H2 connection open");

		Connection hsql = connections.get("profile2");
		assertNotNull(hsql, "HSQL connected");
		assertFalse(hsql.isClosed(), "HSQL connection open");

		Connections.close(connections);

		assertTrue(h2.isClosed(), "H2 connection closed");
		assertTrue(hsql.isClosed(), "HSQL connection closed");
	}
}
