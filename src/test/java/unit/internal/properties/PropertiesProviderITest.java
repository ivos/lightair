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
	public void mandatory_Ok() {
		assertEquals("1. property", "org.h2.Driver",
				p.getProperty(DRIVER_CLASS_NAME));
		assertEquals("2. property", "jdbc:h2:mem:test",
				p.getProperty(CONNECTION_URL));
		assertEquals("Empty property", "", p.getProperty(PASSWORD));
	}

	@Test
	public void mandatory_Fail_MissingPropertyException() {
		try {
			p.getProperty("non-existent-property");
			fail("Should throw");
		} catch (MissingPropertyException e) {
			assertEquals(
					"Property 'non-existent-property' not found in the properties file.",
					e.getMessage());
		}
	}

	@Test
	public void optional() {
		assertEquals("Defined", 0, p.getProperty(TIME_DIFFERENCE_LIMIT, 123));
		assertEquals("Not defined", 123,
				p.getProperty("un-defined-property", 123));
	}

}
