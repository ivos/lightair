package it.verify.core;

import it.common.ProfilesTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(LightAir.class)
@Verify.List({
	@Verify("../../common/profiles-defaulth2.xml"),
	@Verify(value = "../../common/profiles-hsqldb.xml", profile = "hsqldb"),
	@Verify(value = {"../../common/profiles-custom1.xml", "../../common/profiles-custom2.xml"}, profile = "custom")})
public class ProfilesTest extends ProfilesTestBase {

	@Test
	public void profiles() {
		fill("Hank");
	}
}
