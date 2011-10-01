package it.setup.annotation;

import net.sf.lightair.annotation.Setup;

import org.junit.Test;

public class DefaultMethodSetupTest extends SetupTestBase {

	@Test
	@Setup
	public void classNameXml() {
		verifyPersons(2);
	}

	@Test
	@Setup
	public void classNameMethodNameXml() {
		verifyPersons(3);
	}

}
