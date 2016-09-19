package it.verify.failure;

import it.common.ProfilesTestBase;
import net.sf.lightair.annotation.Verify;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.support.ExceptionVerifyingJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(ExceptionVerifyingJUnitRunner.class)
@Verify.List({
		@Verify("../../common/profiles-defaulth2.xml"),
		@Verify(value = "../../common/profiles-hsqldb.xml", profile = "hsqldb"),
		@Verify(value = { "../../common/profiles-custom1.xml", "../../common/profiles-custom2.xml" }, profile = "custom") })
public class ProfilesTest extends ProfilesTestBase {

	@Test
	public void profiles() {
		fill("Hank2");
	}

	public void profilesVerifyException(Throwable error) {
		String msg = "Differences found between the expected data set and actual database content.\n" +
				"Found differences for table [custom]/customperson:\n" +
				"  Different row: {customname=Hank}\n" +
				"   Best matching differences: \n" +
				"    customname: expected [Hank], but was [Hank2]\n";
		assertEquals(msg, error.getMessage());
	}
}
