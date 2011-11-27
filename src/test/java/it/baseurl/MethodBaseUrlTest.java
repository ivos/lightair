package it.baseurl;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;
import static org.junit.Assert.*;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.BaseUrl;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(LightAir.class)
public class MethodBaseUrlTest {

	@Test
	@BaseUrl("http://method-base-url-1")
	public void methodUrl1() {
		assertEquals("http://method-base-url-1/", getTestContext().getBaseUrl()
				.toString());
	}

	@Test
	@BaseUrl("http://method-base-url-2")
	public void methodUrl2() {
		assertEquals("http://method-base-url-2/", getTestContext().getBaseUrl()
				.toString());
	}

}
