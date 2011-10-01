package unit.support.properties;

import static org.junit.Assert.*;
import net.sf.lightair.support.properties.PropertiesProvider;

import org.junit.Test;

public class PropertiesProviderTest {

	@Test
	public void test() {
		PropertiesProvider p = new PropertiesProvider();
		assertEquals("1. property", "org.h2.Driver",
				p.getProperty("driverClassName"));
		assertEquals("2. property", "jdbc:h2:mem:test",
				p.getProperty("connectionUrl"));
		assertEquals("Empty property", "", p.getProperty("password"));
	}

}
