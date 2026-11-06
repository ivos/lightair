package it.await.annotation;

import net.sf.lightair.LightAir;
import net.sf.lightair.LightAirNGListener;
import net.sf.lightair.annotation.Await;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testng.annotations.Listeners;

/**
 * Await custom file names.
 */
@ExtendWith(LightAir.class)
@Listeners(LightAirNGListener.class)
@Await({"custom-verify-1.xml", "custom-verify-2.xml", "custom-verify-3.xml"})
public class CustomAwaitTest extends AwaitTestBase {

	@Test
	@org.testng.annotations.Test
	public void classNameXml() {
		scheduleFillPersons(3);
	}
}
