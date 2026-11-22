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
public class DurationTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeAll
	public static void beforeClass() {
		db.execute("create table a (id int primary key, date1 date, time1 time, timestamp1 timestamp)");
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
		verifyRow(0, "2011-10-21", "22:49:48", "2011-11-21 23:59:58.123");
		// @date:
		verifyRow(1, "2009-08-26", "00:00:03", "2009-12-28 00:05:00.0");
		// @time:
		verifyRow(2, "1970-03-01", "16:49:59", "1974-01-06 19:56:06.0");
		// @timestamp:
		verifyRow(3, "2007-05-24", "21:53:03", "2007-05-24 14:43:52.987");
		verifyRow(4, "2011-10-22", "22:49:49", "2011-11-22 23:59:59.124");
	}

	private void verifyRow(int id, String date1, String time1, String timestamp1) {
		assertEquals(id, values.get(id).get("id"), "id " + id);
		assertEquals(date1, values.get(id).get("date1").toString(), "date1 " + id);
		assertEquals(time1, values.get(id).get("time1").toString(), "time1 " + id);
		assertEquals(timestamp1, values.get(id).get("timestamp1").toString(), "timestamp1 " + id);
	}
}
