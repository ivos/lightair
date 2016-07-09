package unit.internal;

import net.sf.lightair.internal.Connections;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ConnectionsTest {

    private Map<String, String> createProperties(
            String driverClassName, String url, String userName, String password) {
        Map<String, String> properties = new HashMap<>();
        properties.put(Connections.DATABASE_DRIVER_CLASS_NAME, driverClassName);
        properties.put(Connections.DATABASE_CONNECTION_URL, url);
        properties.put(Connections.DATABASE_USER_NAME, userName);
        properties.put(Connections.DATABASE_PASSWORD, password);
        return properties;
    }

    @Test
    public void test() throws SQLException {
        Map<String, Map<String, String>> properties = new HashMap<>();
        properties.put("profile1", createProperties("org.h2.Driver", "jdbc:h2:mem:test", "sa", ""));
        properties.put("profile2", createProperties("org.hsqldb.jdbc.JDBCDriver", "jdbc:hsqldb:mem:test", "sa", ""));

        Map<String, Connection> connections = Connections.open(properties);

        Connection h2 = connections.get("profile1");
        assertNotNull("H2 connected", h2);
        assertFalse("H2 connection open", h2.isClosed());

        Connection hsql = connections.get("profile2");
        assertNotNull("HSQL connected", hsql);
        assertFalse("HSQL connection open", hsql.isClosed());

        Connections.close(connections);

        assertTrue("H2 connection closed", h2.isClosed());
        assertTrue("HSQL connection closed", hsql.isClosed());
    }
}
