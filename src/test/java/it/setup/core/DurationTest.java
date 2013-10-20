package it.setup.core;

import static org.junit.Assert.*;
import it.common.CommonTestBase;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(LightAir.class)
@Setup
public class DurationTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a (id int primary key, "
				+ "date1 date, time1 time, timestamp1 timestamp)");
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a");
	}

	@Test
	public void test() {
		assertEquals("Count", new Integer(5),
				db.queryForObject("select count(*) from a", Integer.class));
		values = db.queryForList("select * from a");
		verifyRow(0, new LocalDate(2011, 10, 21), new LocalTime(22, 49, 48),
				new DateTime(2011, 11, 21, 23, 59, 58, 123));
		// @date:
		verifyRow(1, new LocalDate(2009, 8, 26), new LocalTime(0, 0, 3),
				new DateTime(2009, 12, 28, 0, 5, 0, 0));
		// @time:
		verifyRow(2, new LocalDate(1970, 3, 1), new LocalTime(16, 49, 59),
				new DateTime(1974, 1, 6, 19, 56, 6));
		// @timestamp:
		verifyRow(3, new LocalDate(2007, 5, 24), new LocalTime(21, 53, 3, 987),
				new DateTime(2007, 5, 24, 14, 43, 52, 987));
		verifyRow(4, new LocalDate(2011, 10, 22), new LocalTime(22, 49, 49),
				new DateTime(2011, 11, 22, 23, 59, 59, 124));
	}

	private void verifyRow(int id, LocalDate date1, LocalTime time1,
			DateTime timestamp1) {
		assertEquals("id " + id, id, values.get(id).get("id"));
		assertEquals("date1 " + id, date1.toString(),
				values.get(id).get("date1").toString());
		assertEquals("time1 " + id, time1,
				new LocalTime(values.get(id).get("time1")));
		assertEquals("timestamp1 " + id, new Timestamp(timestamp1.getMillis()),
				values.get(id).get("timestamp1"));
	}

}
