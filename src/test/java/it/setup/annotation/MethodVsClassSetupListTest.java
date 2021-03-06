package it.setup.annotation;

import net.sf.lightair.LightAir;
import net.sf.lightair.LightAirNGListener;
import net.sf.lightair.annotation.Setup;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.testng.annotations.Listeners;

/**
 * Method annotation overrides Setup annotation on class.
 */
@RunWith(LightAir.class)
@Listeners(LightAirNGListener.class)
@Setup.List({ @Setup("ignored-1.xml"), @Setup("ignored-2.xml") })
public class MethodVsClassSetupListTest extends SetupTestBase {

	/**
	 * Method with Setup.
	 */
	@Test
	@org.testng.annotations.Test
	@Setup({ "custom-setup-1.xml", "custom-setup-2.xml" })
	public void methodSetup() {
		verifyPersons("Joe", "Jane", "Sue");
	}

	/**
	 * Method with Setup.List.
	 */
	@Test
	@org.testng.annotations.Test
	@Setup.List({ @Setup("custom-setup-1.xml"), @Setup("custom-setup-2.xml") })
	public void methodSetupList() {
		verifyPersons("Jane", "Sue");
	}

}
