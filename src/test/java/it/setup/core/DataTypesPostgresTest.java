package it.setup.core;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.support.ApiTestSupport;
import test.support.ConfigSupport;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Function;

import static org.junit.Assert.assertArrayEquals;
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
		// enum
		db.execute("drop cast if exists (varchar as enum_t);");
		db.execute("drop type if exists enum_t;");
		db.execute("create type enum_t as enum ('snake_value','camelValue','SCREAMING_SNAKE');");
		db.execute("create cast (varchar as enum_t) with inout as implicit;");

		db.execute("create table data_types ("
				+ " id int primary key,"
				+ " char_type char(25), varchar_type varchar(50), text_type text,"
				+ " smallint_type smallint, integer_type integer, bigint_type bigint,"
				+ " decimal_type decimal(20, 2), numeric_type numeric(16, 8),"
				+ " real_type real, double_type double precision,"
				+ " date_type date, time_type time, timestamp_type timestamp,"
				+ " boolean_type boolean,"
				+ " blob_type bytea,"
				+ " uuid_type uuid,"
				+ " enum_type enum_t,"
				+ " json_type json, jsonb_type jsonb,"
				+ " text_array_type text[], varchar_array_type varchar(25)[],"
				+ " integer_array_type integer[], bigint_array_type bigint[])");
		ApiTestSupport.reInitialize();
	}

	@Test
	public void test() {
		perform();
	}

	@Override
	protected void verify() {
		// full
		verifyRow(0,
				"efghijklmnopqrs          ", "abcdefghijklmnopqrstuvxyz",
				"abcdefghijklmnopqrstuvxyz1234567890",
				5678, 12345678, 9223372036854770000L,
				new BigDecimal("123456789012345678.91"), new BigDecimal("12345678.90123456"),
				876.543f, 8765.4321,
				LocalDate.parse("2999-12-31"), LocalTime.parse("23:59:58"),
				LocalDateTime.parse("2998-11-30T22:57:56.789"),
				true,
				"EjRWeJCrzeI=",
				"a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
				"camelValue",
				"{\"key1\":\"value1\"}",
				"{\"key1\": \"value1\", \"key2\": \"value2\", \"key3\": \"value3\"}",
				new String[]{"text 1", "text 2", "", " ", null, "text 3"},
				new String[]{"varchar 1", "varchar 2", "", " ", null, "varchar 3"},
				new Integer[]{2345, 3456, null, 4567},
				new Long[]{2345678901L, 2345678902L, null, 2345678903L}
		);
		// empty
		verifyRow(1,
				"                         ", "", "",
				0, 0, 0L,
				new BigDecimal("0.00"), new BigDecimal("0E-8"),
				0f, 0.,
				LocalDate.parse("2000-01-02"), LocalTime.parse("00:00:00"),
				LocalDateTime.parse("2000-01-02T03:04:05.678"),
				false,
				"",
				"00000000-0000-0000-0000-000000000000",
				"snake_value",
				"\"\"", "\"\"",
				new String[0], new String[0], new Integer[0], new Long[0]

		);
		// null
		verifyRow(2,
				null, null, null,
				null, null, null,
				null, null,
				null, null,
				null, null, null,
				null,
				null,
				null,
				null,
				null, null,
				null, null, null, null
		);
		// auto
		verifyRow(3,
				"char_type 1384656904     ", "varchar_type 1384684104", "text_type 1384616204",
				7904, 1384653604, 1384644904L,
				new BigDecimal("13846469.04"), new BigDecimal("13.84612704"),
				13846530f, 13846684.04,
				LocalDate.parse("1976-12-12"), LocalTime.parse("08:26:44"),
				LocalDateTime.parse("1900-01-05T04:53:24.004"),
				false,
				"YmxvYl90eXBlIDEzODQ2NzU0MDQ=",
				"988543c3-b42c-3ce1-8da5-9bad5175fd20",
				null,
				"{\"json_type\": 1384687704}", "{\"jsonb_type\": 1384669404}",
				new String[]{"text_array_type 13846759041", "text_array_type 13846759042", "text_array_type 13846759043"},
				new String[]{"varchar_array_13846786041", "varchar_array_13846786042", "varchar_array_13846786043"},
				new Integer[]{846767041, 846767042, 846767043},
				new Long[]{13846934041L, 13846934042L, 13846934043L}
		);
	}

	private void verifyRow(
			int id,
			String char_type, String varchar_type, String text_type,
			Integer smallint_type, Integer integer_type, Long bigint_type,
			BigDecimal decimal_type, BigDecimal numeric_type,
			Float real_type, Double double_type,
			LocalDate date_type, LocalTime time_type, LocalDateTime timestamp_type,
			Boolean boolean_type,
			String blob_type,
			String uuid_type,
			String enum_type,
			String json_type, String jsonb_type,
			Object[] text_array_type, Object[] varchar_array_type,
			Object[] integer_array_type, Object[] bigint_array_type
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
		verifyField(id, "date_type", date_type, Object::toString, Object::toString);
		verifyField(id, "time_type", time_type, v -> Time.valueOf(v).toString(), Object::toString);
		verifyField(id, "timestamp_type", timestamp_type, v -> Timestamp.valueOf(v).toString(), Object::toString);
		// boolean
		assertEquals("boolean_type type " + id, boolean_type, values.get(id).get("boolean_type"));
		// binary
		assertEquals("blob_type type " + id, blob_type, convertBytesToString(values.get(id).get("blob_type")));
		// uuid
		verifyField(id, "uuid_type", uuid_type, Function.identity(), Object::toString);
		// enum
		verifyField(id, "enum_type", enum_type, Function.identity(), Function.identity());
		// json
		verifyField(id, "json_type", json_type, Function.identity(), Object::toString);
		// jsonb
		verifyField(id, "jsonb_type", jsonb_type, Function.identity(), Object::toString);
		// arrays
		verifyArrayField(id, "text_array_type", text_array_type);
		verifyArrayField(id, "varchar_array_type", varchar_array_type);
		verifyArrayField(id, "integer_array_type", integer_array_type);
		verifyArrayField(id, "bigint_array_type", bigint_array_type);
	}

	private void verifyArrayField(int id, String name, Object[] expectedValue) {
		if (null == expectedValue) {
			assertNull(name + " " + id, values.get(id).get(name));
		} else {
			try {
				assertArrayEquals(name + " " + id, expectedValue,
						(Object[]) ((Array) values.get(id).get(name)).getArray());
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
