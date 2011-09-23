package it.setup;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(LightAir.class)
public class DefaultMethodSetupTest extends SetupTestBase {

	@Setup
	@Test
	public void test() {
		verifyPersons();
	}

}
