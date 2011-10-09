package it.verify.annotation;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Verify custom file names.
 */
@RunWith(LightAir.class)
@Verify({ "custom-verify-1.xml", "custom-verify-2.xml", "custom-verify-3.xml" })
public class CustomVerifyTest extends VerifyTestBase {

	@Test
	public void classNameXml() {
		fillPersons(3);
	}

}
