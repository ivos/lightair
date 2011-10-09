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
		assertEquals("Count", 2,
				db.queryForInt("select count(*) from data_types"));
		values = db.queryForList("select * from data_types");
		verifyRow(0, 1, "abcdefghijklmnopqrstuvxyz", 12345678,
				new DateMidnight(2999, 12, 31), new LocalTime(23, 59, 58),
				new DateTime(2998, 11, 30, 22, 57, 56, 789));
		verifyRow(1, 2, "", 0, new DateMidnight(2000, 1, 2), new LocalTime(0,
				0, 0), new DateTime(2000, 1, 2, 3, 4, 5, 678));
	}

	private void verifyRow(int rowId, int id, String string_type,
			int integer_type, DateMidnight date_type, LocalTime time_type,
			DateTime timestamp_type) {
		assertEquals("id " + rowId, id, values.get(rowId).get("id"));
		assertEquals("string_type " + rowId, string_type, values.get(rowId)
				.get("string_type"));
		assertEquals("integer_type " + rowId, integer_type, values.get(rowId)
				.get("integer_type"));
		assertEquals("date_type " + rowId, date_type.toDate(), values
				.get(rowId).get("date_type"));
		assertEquals(
				"time_type " + rowId,
				time_type,
				LocalTime.fromDateFields((Date) values.get(rowId).get(
						"time_type")));
		assertEquals("timestamp_type " + rowId, timestamp_type.toDate(), values
				.get(rowId).get("timestamp_type"));
	}

}
