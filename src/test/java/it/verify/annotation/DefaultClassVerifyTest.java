package it.verify.annotation;

import net.sf.lightair.annotation.Verify;

import org.junit.Test;

@Verify
public class DefaultClassVerifyTest extends VerifyTestBase {

	@Test
	public void classNameXml() {
		fillPersons(2);
	}

	@Test
	public void classNameMethodNameXml() {
		fillPersons(3);
	}

}
