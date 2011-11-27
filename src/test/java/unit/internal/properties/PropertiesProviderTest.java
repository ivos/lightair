package unit.internal.properties;

import static org.junit.Assert.*;
import net.sf.lightair.exception.PropertiesNotFoundException;
import net.sf.lightair.internal.properties.PropertiesProvider;

import org.junit.Test;

public class PropertiesProviderTest {

	@Test
	public void init_PropertiesNotFound() {
		PropertiesProvider p = new PropertiesProvider();
		p.setPropertiesFileName("non-existent-file.properties");

		try {
			p.init();
			fail("Should throw");
		} catch (PropertiesNotFoundException e) {
			assertEquals(
					"Light air properties file 'non-existent-file.properties' not found.",
					e.getMessage());
		}
	}
}
