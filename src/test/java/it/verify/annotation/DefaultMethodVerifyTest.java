package it.verify.annotation;

import net.sf.lightair.LightAir;
import net.sf.lightair.LightAirNGListener;
import net.sf.lightair.annotation.Verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.testng.annotations.Listeners;

/**
 * Test class is not annotated. Test methods are annotated @Verify.
 */
@RunWith(LightAir.class)
@Listeners(LightAirNGListener.class)
public class DefaultMethodVerifyTest extends VerifyTestBase {

	/**
	 * Verifies '&lt;test class name>-verify.xml'.
	 */
	@Test
	@org.testng.annotations.Test
	@Verify
	public void classNameXml() {
		fillPersons(2);
	}

	/**
	 * Verifies '&lt;test class name>.&lt;test method name>-verify.xml'.
	 */
	@Test
	@org.testng.annotations.Test
	@Verify
	public void classNameMethodNameXml() {
		fillPersons(3);
	}

	/**
	 * Verifies '&lt;test method name>-verify.xml'.
	 */
	@Test
	@org.testng.annotations.Test
	@Verify
	public void methodNameXml() {
		fillPersons(4);
	}

}
