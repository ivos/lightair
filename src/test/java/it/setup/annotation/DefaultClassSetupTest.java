package it.setup.annotation;

import net.sf.lightair.LightAir;
import net.sf.lightair.LightAirNGListener;
import net.sf.lightair.annotation.Setup;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.testng.annotations.Listeners;

/**
 * Test class is annotated @Setup.
 */
@RunWith(LightAir.class)
@Listeners(LightAirNGListener.class)
@Setup
public class DefaultClassSetupTest extends SetupTestBase {

	/**
	 * Loads setup '&lt;test class name>.xml'.
	 */
	@Test
	@org.testng.annotations.Test
	public void classNameXml() {
		verifyPersons("Joe", "Jane");
	}

	/**
	 * Loads setup '&lt;test class name>.&lt;test method name>.xml'.
	 */
	@Test
	@org.testng.annotations.Test
	public void classNameMethodNameXml() {
		verifyPersons("Joe", "Jane", "Sue");
	}

	/**
	 * Loads setup '&lt;test method name>.xml'.
	 */
	@Test
	@org.testng.annotations.Test
	public void methodNameXml() {
		verifyPersons("Method", "Name", "Only");
	}

}
