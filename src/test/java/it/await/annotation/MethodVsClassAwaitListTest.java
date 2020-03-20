package it.await.annotation;

import net.sf.lightair.LightAir;
import net.sf.lightair.LightAirNGListener;
import net.sf.lightair.annotation.Await;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testng.annotations.Listeners;

/**
 * Method annotation overrides Await.List annotation on class.
 */
@RunWith(LightAir.class)
@Listeners(LightAirNGListener.class)
@Await.List({@Await("ignored-1.xml"), @Await("ignored-2.xml")})
public class MethodVsClassAwaitListTest extends AwaitTestBase {

	/**
	 * Method with Await.
	 */
	@Test
	@org.testng.annotations.Test
	@Await({"custom-verify-1.xml", "custom-verify-2.xml"})
	public void classNameXml() {
		scheduleFillPersons(2);
	}

	/**
	 * Method with Await.List.
	 */
	@Test
	@org.testng.annotations.Test
	@Await.List({@Await({"custom-verify-1.xml", "custom-verify-2.xml"}),
			@Await({"custom-verify-1.xml", "custom-verify-2.xml"})})
	public void methodSetupList() {
		scheduleFillPersons(2);
	}
}
