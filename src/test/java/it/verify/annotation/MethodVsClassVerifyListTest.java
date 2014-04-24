package it.verify.annotation;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Method annotation overrides Verify.List annotation on class.
 */
@RunWith(LightAir.class)
@Verify.List({ @Verify("ignored-1.xml"), @Verify("ignored-2.xml") })
public class MethodVsClassVerifyListTest extends VerifyTestBase {

	/**
	 * Method with Verify.
	 */
	@Test
	@Verify({ "custom-verify-1.xml", "custom-verify-2.xml" })
	public void classNameXml() {
		fillPersons(2);
	}

	/**
	 * Method with Verify.List.
	 */
	@Test
	@Verify.List({ @Verify({ "custom-verify-1.xml", "custom-verify-2.xml" }),
			@Verify({ "custom-verify-1.xml", "custom-verify-2.xml" }) })
	public void methodSetupList() {
		fillPersons(2);
	}

}
