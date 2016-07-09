package unit.internal;

import net.sf.lightair.internal.Properties;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PropertiesTest {

	@Test
	public void test() {
		Map<String, Map<String, String>> properties = Properties.load("light-air-profiles.properties");

		Map<String, String> def = properties.get("");
		assertNotNull("Profile default loaded", def);

		Map<String, String> hsqldb = properties.get("hsqldb");
		assertNotNull("Profile hsqldb loaded", hsqldb);

		assertEquals("Default driver", "org.h2.Driver", def.get("database.driverClassName"));
		assertEquals("Default url", "jdbc:h2:mem:test", def.get("database.connectionUrl"));
		assertEquals("Default time diff", "0", def.get("time.difference.limit.millis"));

		assertEquals("Hsqldb driver", "org.hsqldb.jdbc.JDBCDriver", hsqldb.get("database.driverClassName"));
		assertEquals("Hsqldb url", "jdbc:hsqldb:mem:test", hsqldb.get("database.connectionUrl"));

		assertEquals("Profiles loaded", 3, properties.keySet().size());
	}
}
