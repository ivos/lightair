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
@BaseUrl("http://class-base-url")
public class ClassBaseUrlTest {

	@Test
	@org.testng.annotations.Test
	public void classUrl() {
		assertEquals("http://class-base-url/", getTestContext().getBaseUrl()
				.toString());
	}

	@Test
	@org.testng.annotations.Test
	@BaseUrl("http://method-base-url")
	public void methodOverride() {
		assertEquals("http://method-base-url/", getTestContext().getBaseUrl()
				.toString());
	}

}
