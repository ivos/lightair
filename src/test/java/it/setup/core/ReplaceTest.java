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
public class ReplaceTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeAll
	public static void beforeClass() {
		db.execute("create table a (id int primary key, a1 varchar(50), "
			+ "date1 date, time1 time, timestamp1 timestamp)");
		ApiTestSupport.reInitialize();
	}

	@AfterAll
	public static void afterClass() {
		db.execute("drop table a");
	}

	@Test
	public void test() {
		assertEquals(Integer.valueOf(5), db.queryForObject("select count(*) from a", Integer.class), "Count");
		values = db.queryForList("select * from a");
		verifyRow(0, "01", "2011-10-21", "22:49:48", "2011-11-21 23:59:58.123");
		// @date:
		verifyRow(1, null, "2009-08-28", "00:00:00", "2009-08-28 00:00:00.0");
		// @time:
		verifyRow(2, null, "1970-01-01", "19:49:59", "1970-01-01 19:49:59.0");
		// @timestamp:
		verifyRow(3, null, "2009-08-28", "19:49:59", "2009-08-28 19:49:59.987");
		verifyRow(4, "21", "2011-10-22", "22:49:49", "2011-11-22 23:59:59.124");
	}

	private void verifyRow(int id, String a1, String date1, String time1, String timestamp1) {
		assertEquals(id, values.get(id).get("id"), "id " + id);
		assertEquals(a1, values.get(id).get("a1"), "a1 " + id);
		assertEquals(date1, values.get(id).get("date1").toString(), "date1 " + id);
		assertEquals(time1, values.get(id).get("time1").toString(), "time1 " + id);
		assertEquals(timestamp1, values.get(id).get("timestamp1").toString(), "timestamp1 " + id);
	}
}
