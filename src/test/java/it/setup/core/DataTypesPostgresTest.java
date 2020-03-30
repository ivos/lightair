package it.setup.core;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.support.ApiTestSupport;
import test.support.ConfigSupport;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(LightAir.class)
@Setup
public class DataTypesPostgresTest extends DataTypesSetupTestBase {

	static {
		initPostgres();
		connect("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
		ConfigSupport.replaceConfig("postgres");
	}

	@BeforeClass
	public static void beforeClass() {
		createTable();
	}

	public static void createTable() {
		db.execute("drop table if exists data_types;");
		db.execute("create table data_types ("
				+ " id int primary key,"
				+ " char_type char(25), varchar_type varchar(50), text_type text,"
				+ " smallint_type smallint, integer_type integer, bigint_type bigint,"
				+ " decimal_type decimal(20, 2), numeric_type numeric(16, 8),"
				+ " real_type real, double_type double precision,"
				+ " date_type date, time_type time, timestamp_type timestamp,"
				+ " boolean_type boolean,"
				+ " blob_type bytea)");
		ApiTestSupport.reInitialize();
	}

	@Test
	public void test() {
		perform();
	}

	@Override
	protected void verify() {
		// full
		verifyRowPostgres(0,
				"efghijklmnopqrs          ", "abcdefghijklmnopqrstuvxyz",
				"abcdefghijklmnopqrstuvxyz1234567890",
				5678, 12345678, 9223372036854770000L,
				new BigDecimal("123456789012345678.91"), new BigDecimal("12345678.90123456"),
				876.543f, 8765.4321,
				LocalDate.parse("2999-12-31"), LocalTime.parse("23:59:58"),
				LocalDateTime.parse("2998-11-30T22:57:56.789"),
				true,
				"EjRWeJCrzeI=");
		// empty
		verifyRowPostgres(1,
				"                         ", "", "",
				0, 0, 0L,
				new BigDecimal("0.00"), new BigDecimal("0E-8"),
				0f, 0.,
				LocalDate.parse("2000-01-02"), LocalTime.parse("00:00:00"),
				LocalDateTime.parse("2000-01-02T03:04:05.678"),
				false,
				"");
		// null
		verifyRowPostgres(2,
				null, null, null,
				null, null, null,
				null, null,
				null, null,
				null, null, null,
				null,
				null);
		// auto
		verifyRowPostgres(3,
				"char_type 1384656904     ", "varchar_type 1384684104", "text_type 1384616204",
				7904, 1384653604, 1384644904L,
				new BigDecimal("13846469.04"), new BigDecimal("13.84612704"),
				13846530f, 13846684.04,
				LocalDate.parse("1976-12-12"), LocalTime.parse("08:26:44"),
				LocalDateTime.parse("1900-01-05T04:53:24.004"),
				false,
				"YmxvYl90eXBlIDEzODQ2NzU0MDQ=");
	}

	private void verifyRowPostgres(
			int id,
			String char_type, String varchar_type, String text_type,
			Integer smallint_type, Integer integer_type, Long bigint_type,
			BigDecimal decimal_type, BigDecimal numeric_type,
			Float real_type, Double double_type,
			LocalDate date_type, LocalTime time_type, LocalDateTime timestamp_type,
			Boolean boolean_type,
			String blob_type
	) {
		assertEquals("id " + id, id, values.get(id).get("id"));
		// char
		assertEquals("char_type " + id, char_type, values.get(id).get("char_type"));
		assertEquals("varchar_type " + id, varchar_type, values.get(id).get("varchar_type"));
		assertEquals("text_type " + id, text_type, values.get(id).get("text_type"));
		// integer
		assertEquals("smallint_type " + id, smallint_type, values.get(id).get("smallint_type"));
		assertEquals("integer_type " + id, integer_type, values.get(id).get("integer_type"));
		assertEquals("bigint_type type " + id, bigint_type, values.get(id).get("bigint_type"));
		// numeric
		assertEquals("decimal_type type " + id, decimal_type, values.get(id).get("decimal_type"));
		assertEquals("numeric_type type " + id, numeric_type, values.get(id).get("numeric_type"));
		assertEquals("real_type type " + id, real_type, values.get(id).get("real_type"));
		assertEquals("double_type type " + id, double_type, values.get(id).get("double_type"));
		// date/time
		if (null == date_type) {
			assertNull("date_type " + id, values.get(id).get("date_type"));
		} else {
			assertEquals("date_type " + id, date_type.toString(), values.get(id).get("date_type").toString());
		}
		if (null == time_type) {
			assertNull("time_type " + id, values.get(id).get("time_type"));
		} else {
			assertEquals("time_type " + id, Time.valueOf(time_type).toString(),
					values.get(id).get("time_type").toString());
		}
		if (null == timestamp_type) {
			assertNull("timestamp_type " + id, values.get(id).get("timestamp_type"));
		} else {
			assertEquals("timestamp_type " + id, Timestamp.valueOf(timestamp_type).toString(),
					values.get(id).get("timestamp_type").toString());
		}
		// boolean
		assertEquals("boolean_type type " + id, boolean_type, values.get(id).get("boolean_type"));
		// binary
		assertEquals("blob_type type " + id, blob_type, convertBytesToString(values.get(id).get("blob_type")));
	}
}
