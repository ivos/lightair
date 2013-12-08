package it.verify.core;

import it.common.ProfilesTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(LightAir.class)
@Verify.List({
		@Verify("../../common/profiles-defaulth2.xml"),
		@Verify(value = "../../common/profiles-hsqldb.xml", profile = "hsqldb"),
		@Verify(value = { "../../common/profiles-derby1.xml",
				"../../common/profiles-derby2.xml" }, profile = "derby") })
public class ProfilesTest extends ProfilesTestBase {

	@Test
	public void profiles() {
		fill("Hank");
	}

}
