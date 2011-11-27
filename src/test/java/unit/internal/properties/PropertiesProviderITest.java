package unit.internal.properties;

import static org.junit.Assert.*;
import net.sf.lightair.exception.MissingPropertyException;
import net.sf.lightair.internal.factory.Factory;
import net.sf.lightair.internal.properties.PropertiesProvider;
import net.sf.lightair.internal.properties.PropertyKeys;

import org.junit.Before;
import org.junit.Test;

public class PropertiesProviderITest implements PropertyKeys {

	PropertiesProvider p;

	@Before
	public void before() {
		p = Factory.getInstance().getPropertiesProvider();
	}

	@Test
	public void ok() {
		assertEquals("1. property", "org.h2.Driver",
				p.getProperty(DRIVER_CLASS_NAME));
		assertEquals("2. property", "jdbc:h2:mem:test",
				p.getProperty(CONNECTION_URL));
		assertEquals("Empty property", "", p.getProperty(PASSWORD));
	}

	@Test
	public void fail_MissingPropertyException() {
		try {
			p.getProperty("non-existent-property");
			fail("Should throw");
		} catch (MissingPropertyException e) {
			assertEquals(
					"Property 'non-existent-property' not found in the properties file.",
					e.getMessage());
		}
	}
}
