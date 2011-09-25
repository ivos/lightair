package it.verify;

import net.sf.lightair.annotation.Verify;

import org.junit.Test;

public class DefaultMethodVerifyTest extends VerifyTestBase {

	@Test
	@Verify
	public void classNameXml() {
		fillPersons(2);
	}

	@Test
	@Verify
	public void classNameMethodNameXml() {
		fillPersons(3);
	}

}
