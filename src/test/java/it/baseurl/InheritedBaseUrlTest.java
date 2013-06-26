package it.baseurl;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;
import static org.junit.Assert.*;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.BaseUrl;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(LightAir.class)
public class InheritedBaseUrlTest extends InheritedBaseUrlTestBaseClass {

	@Test
	public void classUrl() {
		assertEquals("http://base-class-base-url/", getTestContext()
				.getBaseUrl().toString());
	}

	@Test
	@BaseUrl("http://method-base-url")
	public void methodOverride() {
		assertEquals("http://method-base-url/", getTestContext().getBaseUrl()
				.toString());
	}

}
