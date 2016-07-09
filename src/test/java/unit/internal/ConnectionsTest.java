package unit.internal;

import net.sf.lightair.internal.Connections;
import org.junit.Test;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class ConnectionsTest {

    @Test
    public void test() {
        Map<String, String> properties = new HashMap<>();
        properties.put(Connections.DATABASE_DRIVER_CLASS_NAME, "org.h2.Driver");
        properties.put(Connections.DATABASE_CONNECTION_URL, "jdbc:h2:mem:test");
        properties.put(Connections.DATABASE_USER_NAME, "sa");
        properties.put(Connections.DATABASE_PASSWORD, "");

        Connection connection = Connections.open("profile1", properties);

        assertNotNull(connection);

        Connections.close("profile1", connection);
    }
}
