package it.baseurl;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;
import static org.junit.Assert.*;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.BaseUrl;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(LightAir.class)
@BaseUrl("http://overridden-class-base-url")
public class InheritedBaseUrlOverrideTest extends InheritedBaseUrlTestBaseClass {

	@Test
	public void classUrl() {
		assertEquals("http://overridden-class-base-url/", getTestContext()
				.getBaseUrl().toString());
	}

}
