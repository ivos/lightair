package it.setup.annotation;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class is annotated @Setup.
 */
@RunWith(LightAir.class)
@Setup
public class DefaultClassSetupTest extends SetupTestBase {

	/**
	 * Loads setup '&lt;test class name>.xml'.
	 */
	@Test
	public void classNameXml() {
		verifyPersons(2);
	}

	/**
	 * Loads setup '&lt;test class name>.&lt;test method name>.xml'.
	 */
	@Test
	public void classNameMethodNameXml() {
		verifyPersons(3);
	}

}
