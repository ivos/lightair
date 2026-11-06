package it.setup.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.support.ApiTestSupport;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(LightAir.class)
@Setup
public class ReplaceTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a (id int primary key, a1 varchar(50), "
			+ "date1 date, time1 time, timestamp1 timestamp)");
		ApiTestSupport.reInitialize();
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a");
	}

	@Test
	public void test() {
		assertEquals("Count", Integer.valueOf(5),
			db.queryForObject("select count(*) from a", Integer.class));
		values = db.queryForList("select * from a");
		verifyRow(0, "01", LocalDate.of(2011, 10, 21), LocalTime.of(22, 49, 48),
			LocalDateTime.of(2011, 11, 21, 23, 59, 58, 123000000));
		// @date:
		verifyRow(1, null, LocalDate.of(2009, 8, 28), LocalTime.of(0, 0, 0),
			LocalDateTime.of(2009, 8, 28, 0, 0, 0, 0));
		// @time:
		verifyRow(2, null, LocalDate.of(1970, 1, 1), LocalTime.of(19, 49, 59),
			LocalDateTime.of(1970, 1, 1, 19, 49, 59));
		// @timestamp:
		verifyRow(3, null, LocalDate.of(2009, 8, 28), LocalTime.of(19, 49, 59),
			LocalDateTime.of(2009, 8, 28, 19, 49, 59, 987000000));
		verifyRow(4, "21", LocalDate.of(2011, 10, 22), LocalTime.of(22, 49, 49),
			LocalDateTime.of(2011, 11, 22, 23, 59, 59, 124000000));
	}

	private void verifyRow(int id, String a1, LocalDate date1, LocalTime time1, LocalDateTime timestamp1) {
		assertEquals("id " + id, id, values.get(id).get("id"));
		assertEquals("a1 " + id, a1, values.get(id).get("a1"));
		assertEquals("date1 " + id, date1.toString(), values.get(id).get("date1").toString());
		assertEquals("time1 " + id, time1, ((Time) values.get(id).get("time1")).toLocalTime());
		assertEquals("timestamp1 " + id, Timestamp.valueOf(timestamp1), values.get(id).get("timestamp1"));
	}
}
