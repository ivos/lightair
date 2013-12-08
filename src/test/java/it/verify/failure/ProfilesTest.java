package it.verify.failure;

import static org.junit.Assert.*;
import it.common.ProfilesTestBase;
import net.sf.lightair.annotation.Verify;

import org.junit.Test;
import org.junit.runner.RunWith;

import test.support.ExceptionVerifyingJUnitRunner;

@RunWith(ExceptionVerifyingJUnitRunner.class)
@Verify.List({
		@Verify("../../common/profiles-defaulth2.xml"),
		@Verify(value = "../../common/profiles-hsqldb.xml", profile = "hsqldb"),
		@Verify(value = { "../../common/profiles-derby1.xml",
				"../../common/profiles-derby2.xml" }, profile = "derby") })
public class ProfilesTest extends ProfilesTestBase {

	@Test
	public void profiles() {
		fill("Hank2");
	}

	public void profilesVerifyException(Throwable error) {
		String msg = "Assertion failed. "
				+ "Differences found between the expected data set and actual database content.\n"
				+ "Found differences for table root.derbyPerson:\n\n"
				+ "  Different row: \n  derbyName\n" + "  \"Hank\"\n\n"
				+ "  Best matching differences:  \n"
				+ "  derbyName: \"Hank\" <-> \"Hank2\"\n\n\n"
				+ "Actual database content:\n\nroot.DERBYPERSON\n"
				+ "  DERBYNAME\n  \"Jake\"\n" + "  \"Hank2\"\n\n";
		assertEquals(msg, error.getMessage());
	}

}
