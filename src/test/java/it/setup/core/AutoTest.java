package it.setup.core;

import static org.junit.Assert.*;
import it.common.CommonTestBase;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(LightAir.class)
@Setup
public class AutoTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a1 (id int primary key, char_type char(20), "
				+ "varchar_type varchar(50), integer_type integer, "
				+ "date_type date, time_type time, timestamp_type timestamp, "
				+ "double_type double, boolean_type boolean, bigint_type bigint, "
				+ "decimal_type decimal(20,2), clob_type clob, blob_type blob, binary_type binary(20))");
		db.execute("create table a2 (id int primary key, char_type char(20), "
				+ "varchar_type varchar(50), integer_type integer, "
				+ "date_type date, time_type time, timestamp_type timestamp, "
				+ "double_type double, boolean_type boolean, bigint_type bigint, "
				+ "decimal_type decimal(20,2), clob_type clob, blob_type blob, binary_type binary(20))");
		db.execute("create table a3 (id int primary key, char_type char(20), "
				+ "varchar_type varchar(50), integer_type integer, "
				+ "date_type date, time_type time, timestamp_type timestamp, "
				+ "double_type double, boolean_type boolean, bigint_type bigint, "
				+ "decimal_type decimal(20,2), clob_type clob, blob_type blob, binary_type binary(20))");
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a1");
		db.execute("drop table a2");
		db.execute("drop table a3");
	}

	@Test
	public void test() {
		assertEquals("Count", new Integer(3),
				db.queryForObject("select count(*) from a1", Integer.class));
		values = db.queryForList("select * from a1");

		verifyRow(0, 2736700, "CHAR_TYPE 2734500", "VARCHAR_TYPE 2735200",
				2731500, new DateMidnight(1983, 03, 27), new LocalTime(15, 51,
						40), new DateTime(2002, 05, 26, 16, 40, 00, 400),
				27375.0, false, 2735300L, new BigDecimal("27310.00"),
				"CLOB_TYPE 2733900", "QkxPQl9UWVBFIDI3Mzc1MDA=",
				"QklOQVJZX1RZUEUgMjczMTgwMA==");
		verifyRow(1, 2736701, "CHAR_TYPE 2734501", "VARCHAR_TYPE 2735201",
				2731501, new DateMidnight(1983, 03, 28), new LocalTime(15, 51,
						41), new DateTime(2002, 05, 27, 16, 40, 1, 401),
				27375.01, true, 2735301L, new BigDecimal("27310.01"),
				"CLOB_TYPE 2733901", "QkxPQl9UWVBFIDI3Mzc1MDE=",
				"QklOQVJZX1RZUEUgMjczMTgwMQ==");
		verifyRow(2, 2736702, "CHAR_TYPE 2734502", "VARCHAR_TYPE 2735202",
				2731502, new DateMidnight(1983, 03, 29), new LocalTime(15, 51,
						42), new DateTime(2002, 05, 28, 16, 40, 2, 402),
				27375.02, false, 2735302L, new BigDecimal("27310.02"),
				"CLOB_TYPE 2733902", "QkxPQl9UWVBFIDI3Mzc1MDI=",
				"QklOQVJZX1RZUEUgMjczMTgwMg==");
	}

	protected void verifyRow(int row, int id, String char_type,
			String varchar_type, Integer integer_type, DateMidnight date_type,
			LocalTime time_type, DateTime timestamp_type, Double double_type,
			Boolean boolean_type, Long bigint_type, BigDecimal decimal_type,
			String clob_type, String blob_type, String binary_type) {
		assertEquals("id " + row, id, values.get(row).get("id"));
		assertEquals("char_type " + row, char_type,
				values.get(row).get("char_type"));
		assertEquals("varchar_type " + row, varchar_type,
				values.get(row).get("varchar_type"));
		assertEquals("integer_type " + row, integer_type,
				values.get(row).get("integer_type"));
		if (null == date_type) {
			assertNull("date_type " + row, values.get(row).get("date_type"));
		} else {
			assertEquals("date_type " + row, date_type.toDate(), values
					.get(row).get("date_type"));
		}
		if (null == time_type) {
			assertNull("time_type " + row, values.get(row).get("time_type"));
		} else {
			assertEquals(
					"time_type " + row,
					time_type,
					LocalTime.fromDateFields((Date) values.get(row).get(
							"time_type")));
		}
		if (null == timestamp_type) {
			assertNull("timestamp_type " + row,
					values.get(row).get("timestamp_type"));
		} else {
			assertEquals("timestamp_type " + row, new Timestamp(timestamp_type
					.toDate().getTime()), values.get(row).get("timestamp_type"));
		}
		assertEquals("double_type type " + row, double_type, values.get(row)
				.get("double_type"));
		assertEquals("boolean_type type " + row, boolean_type, values.get(row)
				.get("boolean_type"));
		assertEquals("bigint_type type " + row, bigint_type, values.get(row)
				.get("bigint_type"));
		assertEquals("decimal_type type " + row, decimal_type, values.get(row)
				.get("decimal_type"));
		assertEquals("clob_type type " + row, clob_type,
				values.get(row).get("clob_type"));
		assertEquals("blob_type type " + row, blob_type,
				convertBytesToString(values.get(row).get("blob_type")));
		assertEquals("binary_type type " + row, binary_type,
				convertBytesToString(values.get(row).get("binary_type")));
	}

	protected String convertBytesToString(Object bytes) {
		final byte[] encodedBytes = Base64.encodeBase64(((byte[]) bytes));
		if (null == encodedBytes) {
			return null;
		}
		return new String(encodedBytes);
	}

}
