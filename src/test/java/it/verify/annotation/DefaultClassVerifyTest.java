package it.verify.annotation;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class is annotated @Verify.
 */
@RunWith(LightAir.class)
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

	/**
	 * Verifies '&lt;test method name>-verify.xml'.
	 */
	@Test
	public void methodNameXml() {
		fillPersons(4);
	}

}
