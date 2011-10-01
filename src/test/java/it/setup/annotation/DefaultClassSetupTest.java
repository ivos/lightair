package it.setup.annotation;

import net.sf.lightair.annotation.Setup;

import org.junit.Test;

/**
 * Test class is annotated @Setup.
 */
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
