package it.verify.annotation;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class is not annotated. Test methods are annotated @Verify.
 */
@RunWith(LightAir.class)
public class DefaultMethodVerifyTest extends VerifyTestBase {

	/**
	 * Verifies '&lt;test class name>-verify.xml'.
	 */
	@Test
	@Verify
	public void classNameXml() {
		fillPersons(2);
	}

	/**
	 * Verifies '&lt;test class name>.&lt;test method name>-verify.xml'.
	 */
	@Test
	@Verify
	public void classNameMethodNameXml() {
		fillPersons(3);
	}

}
