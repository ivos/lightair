package unit.internal;

import net.sf.lightair.internal.Properties;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PropertiesTest {

	@Test
	public void test() {
		Map<String, Map<String, String>> properties =
			Properties.load("target/test-classes/light-air-profiles.properties");

		Map<String, String> def = properties.get("");
		assertNotNull(def, "Profile default loaded");

		Map<String, String> hsqldb = properties.get("hsqldb");
		assertNotNull(hsqldb, "Profile hsqldb loaded");

		assertEquals("org.h2.Driver", def.get("database.driverClassName"), "Default driver");
		assertEquals("jdbc:h2:mem:test", def.get("database.connectionUrl"), "Default url");
		assertEquals("0", def.get("time.difference.limit.millis"), "Default time diff");

		assertEquals("org.hsqldb.jdbc.JDBCDriver", hsqldb.get("database.driverClassName"), "Hsqldb driver");
		assertEquals("jdbc:hsqldb:mem:test", hsqldb.get("database.connectionUrl"), "Hsqldb url");

		assertEquals(3, properties.size(), "Profiles loaded");
	}
}
