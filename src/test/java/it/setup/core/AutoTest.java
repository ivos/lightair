package it.setup.core;

import static org.junit.Assert.*;
import it.common.CommonTestBase;

import java.math.BigDecimal;
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
				+ "decimal_type decimal(20,2), clob_type clob, blob_type blob, binary_type binary(8))");
		db.execute("create table a2 (id int primary key, char_type char(20), "
				+ "varchar_type varchar(50), integer_type integer, "
				+ "date_type date, time_type time, timestamp_type timestamp, "
				+ "double_type double, boolean_type boolean, bigint_type bigint, "
				+ "decimal_type decimal(20,2), clob_type clob, blob_type blob, binary_type binary(8))");
		db.execute("create table a3 (id int primary key, char_type char(20), "
				+ "varchar_type varchar(50), integer_type integer, "
				+ "date_type date, time_type time, timestamp_type timestamp, "
				+ "double_type double, boolean_type boolean, bigint_type bigint, "
				+ "decimal_type decimal(20,2), clob_type clob, blob_type blob, binary_type binary(8))");
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a1");
		db.execute("drop table a2");
		db.execute("drop table a3");
	}

	@Test
	public void test() {
		verifyRow(0, "efghijklmnopqrs", "abcdefghijklmnopqrstuvxyz", 12345678,
				new DateMidnight(2999, 12, 31), new LocalTime(23, 59, 58),
				new DateTime(2998, 11, 30, 22, 57, 56, 789), 8765.4321, true,
				9223372036854770000L, new BigDecimal("12345678901234.56"),
				"text1", "EjRWeJCrzeI=", "/ty6CYdlQyI=");
		verifyRow(1, "", "", 0, new DateMidnight(2000, 1, 2), new LocalTime(0,
				0, 0), new DateTime(2000, 1, 2, 3, 4, 5, 678), 0., false, 0L,
				new BigDecimal("0.00"), "", "", "");
		verifyRow(2, null, null, null, null, null, null, null, null, null,
				null, null, null, null);
	}

	protected void verifyRow(int id, String char_type, String varchar_type,
			Integer integer_type, DateMidnight date_type, LocalTime time_type,
			DateTime timestamp_type, Double double_type, Boolean boolean_type,
			Long bigint_type, BigDecimal decimal_type, String clob_type,
			String blob_type, String binary_type) {
		assertEquals("id " + id, id, values.get(id).get("id"));
		assertEquals("char_type " + id, char_type,
				values.get(id).get("char_type"));
		assertEquals("varchar_type " + id, varchar_type,
				values.get(id).get("varchar_type"));
		assertEquals("integer_type " + id, integer_type,
				values.get(id).get("integer_type"));
		if (null == date_type) {
			assertNull("date_type " + id, values.get(id).get("date_type"));
		} else {
			assertEquals("date_type " + id, date_type.toDate(), values.get(id)
					.get("date_type"));
		}
		if (null == time_type) {
			assertNull("time_type " + id, values.get(id).get("time_type"));
		} else {
			assertEquals(
					"time_type " + id,
					time_type,
					LocalTime.fromDateFields((Date) values.get(id).get(
							"time_type")));
		}
		if (null == timestamp_type) {
			assertNull("timestamp_type " + id,
					values.get(id).get("timestamp_type"));
		} else {
			assertEquals("timestamp_type " + id, timestamp_type.toDate(),
					values.get(id).get("timestamp_type"));
		}
		assertEquals("double_type type " + id, double_type,
				values.get(id).get("double_type"));
		assertEquals("boolean_type type " + id, boolean_type, values.get(id)
				.get("boolean_type"));
		assertEquals("bigint_type type " + id, bigint_type,
				values.get(id).get("bigint_type"));
		assertEquals("decimal_type type " + id, decimal_type, values.get(id)
				.get("decimal_type"));
		assertEquals("clob_type type " + id, clob_type,
				values.get(id).get("clob_type"));
		assertEquals("blob_type type " + id, blob_type,
				convertBytesToString(values.get(id).get("blob_type")));
		assertEquals("binary_type type " + id, binary_type,
				convertBytesToString(values.get(id).get("binary_type")));
	}

	protected String convertBytesToString(Object bytes) {
		final byte[] encodedBytes = Base64.encodeBase64(((byte[]) bytes));
		if (null == encodedBytes) {
			return null;
		}
		return new String(encodedBytes);
	}

}
