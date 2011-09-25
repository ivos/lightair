package it.setup;

import net.sf.lightair.annotation.Setup;

import org.junit.Test;

@Setup
public class DefaultClassSetupTest extends SetupTestBase {

	@Test
	public void classNameXml() {
		verifyPersons(2);
	}

	@Test
	public void classNameMethodNameXml() {
		verifyPersons(3);
	}

}
