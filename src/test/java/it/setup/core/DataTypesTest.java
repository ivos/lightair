package it.setup.core;

import static org.junit.Assert.*;
import it.common.CommonTestBase;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(LightAir.class)
@Setup
public class DataTypesTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table data_types (id int primary key, "
				+ "string_type varchar(50), integer_type integer, "
				+ "date_type date, time_type time, timestamp_type timestamp)");
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table data_types");
	}

	@Test
	public void test() {
		assertEquals("Count", new Integer(2), db.queryForObject(
				"select count(*) from data_types", Integer.class));
		values = db.queryForList("select * from data_types");
		verifyRow(0, "abcdefghijklmnopqrstuvxyz", 12345678, new DateMidnight(
				2999, 12, 31), new LocalTime(23, 59, 58), new DateTime(2998,
				11, 30, 22, 57, 56, 789));
		verifyRow(1, "", 0, new DateMidnight(2000, 1, 2),
				new LocalTime(0, 0, 0), new DateTime(2000, 1, 2, 3, 4, 5, 678));
	}

	private void verifyRow(int id, String string_type, int integer_type,
			DateMidnight date_type, LocalTime time_type, DateTime timestamp_type) {
		assertEquals("id " + id, id, values.get(id).get("id"));
		assertEquals("string_type " + id, string_type,
				values.get(id).get("string_type"));
		assertEquals("integer_type " + id, integer_type,
				values.get(id).get("integer_type"));
		assertEquals("date_type " + id, date_type.toDate(),
				values.get(id).get("date_type"));
		assertEquals("time_type " + id, time_type,
				LocalTime
						.fromDateFields((Date) values.get(id).get("time_type")));
		assertEquals("timestamp_type " + id, timestamp_type.toDate(), values
				.get(id).get("timestamp_type"));
	}

}
