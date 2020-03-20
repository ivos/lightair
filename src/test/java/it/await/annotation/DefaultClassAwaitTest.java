package it.await.annotation;

import net.sf.lightair.LightAir;
import net.sf.lightair.LightAirNGListener;
import net.sf.lightair.annotation.Await;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testng.annotations.Listeners;

/**
 * Test class is annotated @Await.
 */
@RunWith(LightAir.class)
@Listeners(LightAirNGListener.class)
@Await
public class DefaultClassAwaitTest extends AwaitTestBase {

	/**
	 * Verifies '&lt;test class name>-verify.xml'.
	 */
	@Test
	@org.testng.annotations.Test
	public void classNameXml() {
		scheduleFillPersons(2);
	}

	/**
	 * Verifies '&lt;test class name>.&lt;test method name>-verify.xml'.
	 */
	@Test
	@org.testng.annotations.Test
	public void classNameMethodNameXml() {
		scheduleFillPersons(3);
	}

	/**
	 * Verifies '&lt;test method name>-verify.xml'.
	 */
	@Test
	@org.testng.annotations.Test
	public void methodNameXml() {
		scheduleFillPersons(4);
	}
}
