package it.verify.annotation;

import net.sf.lightair.LightAir;
import net.sf.lightair.LightAirNGListener;
import net.sf.lightair.annotation.Verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.testng.annotations.Listeners;

/**
 * Verify custom file names.
 */
@RunWith(LightAir.class)
@Listeners(LightAirNGListener.class)
@Verify({ "custom-verify-1.xml", "custom-verify-2.xml", "custom-verify-3.xml" })
public class CustomVerifyTest extends VerifyTestBase {

	@Test
	@org.testng.annotations.Test
	public void classNameXml() {
		fillPersons(3);
	}

}
