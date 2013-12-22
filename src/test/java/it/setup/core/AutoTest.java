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
				+ "decimal_type decimal(20,3), clob_type clob, blob_type blob, binary_type binary(20))");
		db.execute("create table a2 (id int primary key, char_type char(20), "
				+ "varchar_type varchar(50), integer_type integer, "
				+ "date_type date, time_type time, timestamp_type timestamp, "
				+ "double_type double, boolean_type boolean, bigint_type bigint, "
				+ "decimal_type decimal(20,3), clob_type clob, blob_type blob, binary_type binary(20))");
		db.execute("create table a3 (id int primary key, char_type char(20), "
				+ "varchar_type varchar(50), integer_type integer, "
				+ "date_type date, time_type time, timestamp_type timestamp, "
				+ "double_type double, boolean_type boolean, bigint_type bigint, "
				+ "decimal_type decimal(20,3), clob_type clob, blob_type blob, binary_type binary(20))");
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

		verifyRow(0, 2734900, "char_type 2736900", "varchar_type 2734100",
				2733600, DateMidnight.parse("2004-04-25"),
				LocalTime.parse("16:13:20"),
				DateTime.parse("1990-05-09T15:26:40"), 27384.0, false,
				2734900L, new BigDecimal("27369.000"), "clob_type 2733200",
				"YmxvYl90eXBlIDI3MzU0MDA=", "YmluYXJ5X3R5cGUgMjczMjMwMA==");
		verifyRow(1, 2734901, "char_type 2736901", "varchar_type 2734101",
				2733601, DateMidnight.parse("2004-04-26"),
				LocalTime.parse("16:13:21"),
				DateTime.parse("1990-05-10T15:26:41.001"), 27384.01, true,
				2734901L, new BigDecimal("27369.010"), "clob_type 2733201",
				"YmxvYl90eXBlIDI3MzU0MDE=", "YmluYXJ5X3R5cGUgMjczMjMwMQ==");
		verifyRow(2, 2734902, "char_type 2736902", "varchar_type 2734102",
				2733602, DateMidnight.parse("2004-04-27"),
				LocalTime.parse("16:13:22"),
				DateTime.parse("1990-05-11T15:26:42.002"), 27384.02, false,
				2734902L, new BigDecimal("27369.020"), "clob_type 2733202",
				"YmxvYl90eXBlIDI3MzU0MDI=", "YmluYXJ5X3R5cGUgMjczMjMwMg==");
		verifyRow(3, 2734903, "char_type 2736903", "varchar_type 2734103",
				2733603, DateMidnight.parse("2004-04-28"),
				LocalTime.parse("16:13:23"),
				DateTime.parse("1990-05-12T15:26:43.003"), 27384.03, true,
				2734903L, new BigDecimal("27369.030"), "clob_type 2733203",
				"YmxvYl90eXBlIDI3MzU0MDM=", "YmluYXJ5X3R5cGUgMjczMjMwMw==");

		assertEquals("Count", new Integer(4),
				db.queryForObject("select count(*) from a2", Integer.class));
		values = db.queryForList("select * from a2");

		verifyRow(0, 314900, "char_type 0316900", "varchar_type 0314100",
				313600, DateMidnight.parse("1974-03-14"),
				LocalTime.parse("16:00:00"),
				DateTime.parse("1960-03-27T15:13:20.000"), 3184.0, false,
				314900L, new BigDecimal("3169.000"), "clob_type 0313200",
				"YmxvYl90eXBlIDAzMTU0MDA=", "YmluYXJ5X3R5cGUgMDMxMjMwMA==");
		verifyRow(1, 314901, "char_type 0316901", "varchar_type 0314101",
				313601, DateMidnight.parse("1974-03-15"),
				LocalTime.parse("16:00:01"),
				DateTime.parse("1960-03-28T15:13:21.001"), 3184.01, true,
				314901L, new BigDecimal("3169.010"), "clob_type 0313201",
				"YmxvYl90eXBlIDAzMTU0MDE=", "YmluYXJ5X3R5cGUgMDMxMjMwMQ==");
		verifyRow(2, 314902, "char_type 0316902", "varchar_type 0314102",
				313602, DateMidnight.parse("1974-03-16"),
				LocalTime.parse("16:00:02"),
				DateTime.parse("1960-03-29T15:13:22.002"), 3184.02, false,
				314902L, new BigDecimal("3169.020"), "clob_type 0313202",
				"YmxvYl90eXBlIDAzMTU0MDI=", "YmluYXJ5X3R5cGUgMDMxMjMwMg==");
		verifyRow(3, 314903, "char_type 0316903", "varchar_type 0314103",
				313603, DateMidnight.parse("1974-03-17"),
				LocalTime.parse("16:00:03"),
				DateTime.parse("1960-03-30T15:13:23.003"), 3184.03, true,
				314903L, new BigDecimal("3169.030"), "clob_type 0313203",
				"YmxvYl90eXBlIDAzMTU0MDM=", "YmluYXJ5X3R5cGUgMDMxMjMwMw==");

		assertEquals("Count", new Integer(1),
				db.queryForObject("select count(*) from a3", Integer.class));
		values = db.queryForList("select * from a3");

		verifyRow(0, 3664900, "char_type 3666900", "varchar_type 3664100",
				3663600, DateMidnight.parse("1952-04-18"),
				LocalTime.parse("10:33:20"),
				DateTime.parse("1938-05-02T09:46:40.000"), 36684.0, false,
				3664900L, new BigDecimal("36669.000"), "clob_type 3663200",
				"YmxvYl90eXBlIDM2NjU0MDA=", "YmluYXJ5X3R5cGUgMzY2MjMwMA==");
	}

	@Test
	@Setup("AutoTest2.xml")
	public void rowIndexStartsFrom0InNewTest() {
		assertEquals("Count", new Integer(2),
				db.queryForObject("select count(*) from a1", Integer.class));
		values = db.queryForList("select * from a1");

		verifyRow(0, 2734900, "char_type 2736900", "varchar_type 2734100",
				2733600, DateMidnight.parse("2004-04-25"),
				LocalTime.parse("16:13:20"),
				DateTime.parse("1990-05-09T15:26:40"), 27384.0, false,
				2734900L, new BigDecimal("27369.000"), "clob_type 2733200",
				"YmxvYl90eXBlIDI3MzU0MDA=", "YmluYXJ5X3R5cGUgMjczMjMwMA==");
		verifyRow(1, 2734901, "char_type 2736901", "varchar_type 2734101",
				2733601, DateMidnight.parse("2004-04-26"),
				LocalTime.parse("16:13:21"),
				DateTime.parse("1990-05-10T15:26:41.001"), 27384.01, true,
				2734901L, new BigDecimal("27369.010"), "clob_type 2733201",
				"YmxvYl90eXBlIDI3MzU0MDE=", "YmluYXJ5X3R5cGUgMjczMjMwMQ==");
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
