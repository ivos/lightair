package it.setup.core;

import static org.junit.Assert.*;
import it.common.ProfilesTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;

@RunWith(LightAir.class)
@Setup.List({
		@Setup("../../common/profiles-defaulth2.xml"),
		@Setup(value = "../../common/profiles-hsqldb.xml", profile = "hsqldb"),
		@Setup(value = { "../../common/profiles-derby1.xml",
				"../../common/profiles-derby2.xml" }, profile = "derby") })
public class ProfilesTest extends ProfilesTestBase {

	@Test
	public void profiles() {
		verifyProfile(dbDefaultH2, "defaultPerson", "defaultName", 1, 0, "Joe");
		verifyProfile(dbHsqldb, "hsqldbPerson", "hsqldbName", 1, 0, "Jane");
		verifyProfile(dbDerby, "derbyPerson", "derbyName", 2, 0, "Jake");
		verifyProfile(dbDerby, "derbyPerson", "derbyName", 2, 1, "Hank");
	}

	private void verifyProfile(JdbcTemplate db, String tableName,
			String columnName, int size, int index, String value) {
		values = db.queryForList("select * from " + tableName);
		assertEquals("Size of " + tableName, size, values.size());
		assertEquals("Value of " + columnName, value,
				values.get(index).get(columnName));
	}

}
