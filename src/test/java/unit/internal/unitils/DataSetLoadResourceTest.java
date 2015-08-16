package unit.internal.unitils;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import net.sf.lightair.exception.DataSetNotFoundException;
import net.sf.lightair.internal.factory.Factory;
import net.sf.lightair.internal.properties.PropertiesProvider;
import net.sf.lightair.internal.unitils.DataSetLoader;

import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.util.MultiSchemaDataSet;

public class DataSetLoadResourceTest {

	DataSetLoader l;
	Method testMethod;

	public void aMethod() {
	}

	@Before
	public void before() throws Exception {
		testMethod = DataSetLoadResourceTest.class.getDeclaredMethod("aMethod",
				(Class<?>[]) null);
		Factory factory = Factory.getInstance();
		factory.init();
		PropertiesProvider propertiesProvider = factory.getPropertiesProvider();
		propertiesProvider
				.setPropertiesFileName("light-air-profiles.properties");
		propertiesProvider.init();
		l = factory.getDataSetLoader();
	}

	@Test
	public void load_ResourcesPassed() {
		MultiSchemaDataSet actual = l.load("hsqldb", testMethod, "suffix",
				"/org/springframework/jdbc/config/spring-jdbc-3.0.xsd",
				"/org/springframework/jdbc/config/spring-jdbc-3.1.xsd",
				"/org/springframework/jdbc/support/sql-error-codes.xml");

		assertNotNull(actual);
		assertEquals(3, actual.getSchemaNames().size());
	}

	@Test(expected = DataSetNotFoundException.class)
	public void load_ResourcesFail() {
		l.load("profile1", testMethod, "suffix", "/non-existent-file.xml");

		assertFalse("Should not succeed.", true);
	}

}
