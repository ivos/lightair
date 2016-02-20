package it.baseurl;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;
import static org.junit.Assert.*;
import net.sf.lightair.LightAir;
import net.sf.lightair.LightAirNGListener;
import net.sf.lightair.annotation.BaseUrl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.testng.annotations.Listeners;

@RunWith(LightAir.class)
@Listeners(LightAirNGListener.class)
@BaseUrl("http://overridden-class-base-url")
public class InheritedBaseUrlOverrideTest extends InheritedBaseUrlTestBaseClass {

	@Test
	@org.testng.annotations.Test
	public void classUrl() {
		assertEquals("http://overridden-class-base-url/", getTestContext()
				.getBaseUrl().toString());
	}

}
