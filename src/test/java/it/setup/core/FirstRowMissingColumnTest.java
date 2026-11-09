package it.setup.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.support.ApiTestSupport;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(LightAir.class)
@Setup
public class FirstRowMissingColumnTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeAll
	public static void beforeClass() {
		db.execute("create table first_row_missing_column (id int primary key, "
			+ "first_has_value varchar(50), first_missing varchar(50), "
			+ "third_only varchar(50))");
		ApiTestSupport.reInitialize();
	}

	@AfterAll
	public static void afterClass() {
		db.execute("drop table first_row_missing_column");
	}

	@Test
	public void test() {
		assertEquals(Integer.valueOf(3),
			db.queryForObject("select count(*) from first_row_missing_column", Integer.class),
			"Count");
		values = db.queryForList("select * from first_row_missing_column");
		verifyRow(0, "0h", null, null);
		verifyRow(1, "1h", "1m", null);
		verifyRow(2, null, "2m", "2t");
	}

	private void verifyRow(int id, String first_has_value,
		String first_missing, String third_only) {
		assertEquals(id, values.get(id).get("id"), "id " + id);
		assertEquals(first_has_value, values.get(id).get("first_has_value"), "first_has_value " + id);
		assertEquals(first_missing, values.get(id).get("first_missing"), "first_missing " + id);
		assertEquals(third_only, values.get(id).get("third_only"), "third_only " + id);
	}
}
