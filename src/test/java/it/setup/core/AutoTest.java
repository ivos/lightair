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
@Setup({ "AutoTest.xml", "AutoTest2.xml" })
public class AutoTest extends CommonTestBase {

	List<Map<String, Object>> values;

	// TODO add additional sql types (smallint, ...)

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
		assertEquals("Count", new Integer(5),
				db.queryForObject("select count(*) from a1", Integer.class));
		values = db.queryForList("select * from a1");

		verifyRow(0, 2736700, "CHAR_TYPE 2734500", "VARCHAR_TYPE 2735200",
				2731500, new DateMidnight(1983, 3, 27), new LocalTime(15, 51,
						40), new DateTime(2002, 5, 26, 16, 40, 0, 400),
				27375.0, false, 2735300L, new BigDecimal("27310.00"),
				"CLOB_TYPE 2733900", "QkxPQl9UWVBFIDI3Mzc1MDA=",
				"QklOQVJZX1RZUEUgMjczMTgwMA==");
		verifyRow(1, 2736701, "CHAR_TYPE 2734501", "VARCHAR_TYPE 2735201",
				2731501, new DateMidnight(1983, 3, 28), new LocalTime(15, 51,
						41), new DateTime(2002, 5, 27, 16, 40, 1, 401),
				27375.01, true, 2735301L, new BigDecimal("27310.01"),
				"CLOB_TYPE 2733901", "QkxPQl9UWVBFIDI3Mzc1MDE=",
				"QklOQVJZX1RZUEUgMjczMTgwMQ==");
		verifyRow(2, 2736702, "CHAR_TYPE 2734502", "VARCHAR_TYPE 2735202",
				2731502, new DateMidnight(1983, 3, 29), new LocalTime(15, 51,
						42), new DateTime(2002, 5, 28, 16, 40, 2, 402),
				27375.02, false, 2735302L, new BigDecimal("27310.02"),
				"CLOB_TYPE 2733902", "QkxPQl9UWVBFIDI3Mzc1MDI=",
				"QklOQVJZX1RZUEUgMjczMTgwMg==");
		verifyRow(3, 2736703, "CHAR_TYPE 2734503", "VARCHAR_TYPE 2735203",
				2731503, new DateMidnight(1983, 3, 30), new LocalTime(15, 51,
						43), new DateTime(2002, 5, 29, 16, 40, 3, 403),
				27375.03, true, 2735303L, new BigDecimal("27310.03"),
				"CLOB_TYPE 2733903", "QkxPQl9UWVBFIDI3Mzc1MDM=",
				"QklOQVJZX1RZUEUgMjczMTgwMw==");

		assertEquals("Count", new Integer(4),
				db.queryForObject("select count(*) from a2", Integer.class));
		values = db.queryForList("select * from a2");

		verifyRow(0, 316700, "CHAR_TYPE 0314500", "VARCHAR_TYPE 0315200",
				311500, new DateMidnight(1953, 2, 12),
				new LocalTime(15, 38, 20), new DateTime(1972, 4, 13, 16, 26,
						40, 400), 3175.0, false, 315300L, new BigDecimal(
						"3110.00"), "CLOB_TYPE 0313900",
				"QkxPQl9UWVBFIDAzMTc1MDA=", "QklOQVJZX1RZUEUgMDMxMTgwMA==");
		verifyRow(1, 316701, "CHAR_TYPE 0314501", "VARCHAR_TYPE 0315201",
				311501, new DateMidnight(1953, 2, 13),
				new LocalTime(15, 38, 21), new DateTime(1972, 4, 14, 16, 26,
						41, 401), 3175.01, true, 315301L, new BigDecimal(
						"3110.01"), "CLOB_TYPE 0313901",
				"QkxPQl9UWVBFIDAzMTc1MDE=", "QklOQVJZX1RZUEUgMDMxMTgwMQ==");
		verifyRow(2, 316702, "CHAR_TYPE 0314502", "VARCHAR_TYPE 0315202",
				311502, new DateMidnight(1953, 2, 14),
				new LocalTime(15, 38, 22), new DateTime(1972, 4, 15, 16, 26,
						42, 402), 3175.02, false, 315302L, new BigDecimal(
						"3110.02"), "CLOB_TYPE 0313902",
				"QkxPQl9UWVBFIDAzMTc1MDI=", "QklOQVJZX1RZUEUgMDMxMTgwMg==");
		verifyRow(3, 316703, "CHAR_TYPE 0314503", "VARCHAR_TYPE 0315203",
				311503, new DateMidnight(1953, 2, 15),
				new LocalTime(15, 38, 23), new DateTime(1972, 4, 16, 16, 26,
						43, 403), 3175.03, true, 315303L, new BigDecimal(
						"3110.03"), "CLOB_TYPE 0313903",
				"QkxPQl9UWVBFIDAzMTc1MDM=", "QklOQVJZX1RZUEUgMDMxMTgwMw==");

		assertEquals("Count", new Integer(1),
				db.queryForObject("select count(*) from a3", Integer.class));
		values = db.queryForList("select * from a3");

		verifyRow(0, 3666700, "CHAR_TYPE 3664500", "VARCHAR_TYPE 3665200",
				3661500, new DateMidnight(1931, 3, 20), new LocalTime(10, 11,
						40), new DateTime(1950, 5, 19, 11, 0, 0, 400), 36675.0,
				false, 3665300L, new BigDecimal("36610.00"),
				"CLOB_TYPE 3663900", "QkxPQl9UWVBFIDM2Njc1MDA=",
				"QklOQVJZX1RZUEUgMzY2MTgwMA==");
	}

	@Test
	@Setup("AutoTest2.xml")
	public void rowIndexStartsFrom0InNewTest() {
		assertEquals("Count", new Integer(2),
				db.queryForObject("select count(*) from a1", Integer.class));
		values = db.queryForList("select * from a1");

		verifyRow(0, 2736700, "CHAR_TYPE 2734500", "VARCHAR_TYPE 2735200",
				2731500, new DateMidnight(1983, 3, 27), new LocalTime(15, 51,
						40), new DateTime(2002, 5, 26, 16, 40, 0, 400),
				27375.0, false, 2735300L, new BigDecimal("27310.00"),
				"CLOB_TYPE 2733900", "QkxPQl9UWVBFIDI3Mzc1MDA=",
				"QklOQVJZX1RZUEUgMjczMTgwMA==");
		verifyRow(1, 2736701, "CHAR_TYPE 2734501", "VARCHAR_TYPE 2735201",
				2731501, new DateMidnight(1983, 3, 28), new LocalTime(15, 51,
						41), new DateTime(2002, 5, 27, 16, 40, 1, 401),
				27375.01, true, 2735301L, new BigDecimal("27310.01"),
				"CLOB_TYPE 2733901", "QkxPQl9UWVBFIDI3Mzc1MDE=",
				"QklOQVJZX1RZUEUgMjczMTgwMQ==");
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
