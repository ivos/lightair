package it.setup.annotation;

import net.sf.lightair.LightAir;
import net.sf.lightair.LightAirNGListener;
import net.sf.lightair.annotation.Setup;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.testng.annotations.Listeners;

/**
 * Setup custom file names.
 */
@RunWith(LightAir.class)
@Listeners(LightAirNGListener.class)
@Setup({ "custom-setup-1.xml", "custom-setup-2.xml", "custom-setup-3.xml" })
public class CustomSetupTest extends SetupTestBase {

	@Test
	@org.testng.annotations.Test
	public void classNameXml() {
		verifyPersons("Joe", "Jane", "Sue", "Jake");
	}

}
