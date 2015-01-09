package it.verify.annotation;

import net.sf.lightair.LightAir;
import net.sf.lightair.LightAirNGListener;
import net.sf.lightair.annotation.Verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.testng.annotations.Listeners;

/**
 * Method annotation overrides Verify annotation on class.
 */
@RunWith(LightAir.class)
@Listeners(LightAirNGListener.class)
@Verify({ "ignored-1.xml", "ignored-2.xml" })
public class MethodVsClassVerifyTest extends VerifyTestBase {

	/**
	 * Method with Verify.
	 */
	@Test
	@org.testng.annotations.Test
	@Verify({ "custom-verify-1.xml", "custom-verify-2.xml" })
	public void classNameXml() {
		fillPersons(2);
	}

	/**
	 * Method with Verify.List.
	 */
	@Test
	@org.testng.annotations.Test
	@Verify.List({ @Verify({ "custom-verify-1.xml", "custom-verify-2.xml" }),
			@Verify({ "custom-verify-1.xml", "custom-verify-2.xml" }) })
	public void methodSetupList() {
		fillPersons(2);
	}

}
