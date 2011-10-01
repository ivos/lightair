package it.verify.annotation;

import net.sf.lightair.annotation.Verify;

import org.junit.Test;

/**
 * Test class is annotated @Verify.
 */
@Verify
public class DefaultClassVerifyTest extends VerifyTestBase {

	/**
	 * Verifies '&lt;test class name>-verify.xml'.
	 */
	@Test
	public void classNameXml() {
		fillPersons(2);
	}

	/**
	 * Verifies '&lt;test class name>.&lt;test method name>-verify.xml'.
	 */
	@Test
	public void classNameMethodNameXml() {
		fillPersons(3);
	}

}
