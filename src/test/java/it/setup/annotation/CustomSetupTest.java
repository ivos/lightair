package it.setup.annotation;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(LightAir.class)
@Setup({ "custom-setup-1.xml", "custom-setup-2.xml", "custom-setup-3.xml" })
public class CustomSetupTest extends SetupTestBase {

	@Test
	public void classNameXml() {
		verifyPersons(4);
	}

}
