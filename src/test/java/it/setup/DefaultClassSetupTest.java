package it.setup;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(LightAir.class)
@Setup
public class DefaultClassSetupTest extends SetupTestBase {

	@Test
	public void test() {
		verifyPersons();
	}

}
