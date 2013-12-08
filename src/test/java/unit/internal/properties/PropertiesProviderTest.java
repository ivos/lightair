package unit.internal.properties;

import static org.junit.Assert.*;

import java.util.Set;

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

	@Test
	public void getDbUnitFeatureNames() {
		PropertiesProvider p = new PropertiesProvider();
		p.setPropertiesFileName("light-air.provider.unit.test.properties");
		p.init();

		Set<String> names = p.getDbUnitFeatureNames(null);

		assertTrue(names.contains("dbunit.features.feature-name1"));
		assertTrue(names.contains("dbunit.features.feature-name2"));
		assertTrue(names.contains("dbunit.features.feature-name3"));
		assertEquals(3, names.size());
	}

	@Test
	public void getDbUnitPropertyNames() {
		PropertiesProvider p = new PropertiesProvider();
		p.setPropertiesFileName("light-air.provider.unit.test.properties");
		p.init();

		Set<String> names = p.getDbUnitPropertyNames(null);

		assertTrue(names.contains("dbunit.properties.property-name1"));
		assertTrue(names.contains("dbunit.properties.property-name2"));
		assertTrue(names.contains("dbunit.properties.property-name3"));
		assertTrue(names.contains("dbunit.properties.property-name4"));
		assertEquals(4, names.size());
	}

}
