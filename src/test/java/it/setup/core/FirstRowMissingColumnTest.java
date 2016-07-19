package it.setup.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;
import net.sf.lightair.internal.Api;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(LightAir.class)
@Setup
public class FirstRowMissingColumnTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table first_row_missing_column (id int primary key, "
				+ "first_has_value varchar(50), first_missing varchar(50), "
				+ "third_only varchar(50))");
		Api.reInitialize();
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table first_row_missing_column");
	}

	@Test
	public void test() {
		assertEquals("Count", new Integer(3), db.queryForObject(
				"select count(*) from first_row_missing_column", Integer.class));
		values = db.queryForList("select * from first_row_missing_column");
		verifyRow(0, "0h", null, null);
		verifyRow(1, "1h", "1m", null);
		verifyRow(2, null, "2m", "2t");
	}

	private void verifyRow(int id, String first_has_value,
			String first_missing, String third_only) {
		assertEquals("id " + id, id, values.get(id).get("id"));
		assertEquals("first_has_value " + id, first_has_value, values.get(id)
				.get("first_has_value"));
		assertEquals("first_missing " + id, first_missing,
				values.get(id).get("first_missing"));
		assertEquals("third_only " + id, third_only,
				values.get(id).get("third_only"));
	}

}
