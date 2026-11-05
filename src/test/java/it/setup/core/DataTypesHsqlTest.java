package it.setup.core;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.support.ApiTestSupport;
import test.support.ConfigSupport;
import test.support.TimeUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

@RunWith(LightAir.class)
@Setup
public class DataTypesHsqlTest extends DataTypesSetupTestBase {

	static {
		connect("jdbc:hsqldb:mem:test", "sa", "");
		ConfigSupport.replaceConfig("hsql");
	}

	@BeforeClass
	public static void beforeClass() {
		createTable();
	}

	public static void createTable() {
		db.execute("create table data_types (id int primary key, char_type char(25), "
				+ "varchar_type varchar(50), integer_type integer, "
				+ "date_type date, time_type time, timestamp_type timestamp, "
				+ "double_type double, boolean_type boolean, bigint_type bigint, "
				+ "decimal_type decimal(20,2), clob_type clob, blob_type blob, binary_type binary(8), "
				+ "uuid_type uuid)");
		ApiTestSupport.reInitialize();
	}

	@Test
	public void test() {
		perform();
	}

	@Override
	protected void verify() {
		// full
		verifyRow(0, "efghijklmnopqrs          ", "abcdefghijklmnopqrstuvxyz",
				12345678, LocalDate.of(2999, 12, 31),
				LocalTime.of(23, 59, 58),
				LocalDateTime.of(2998, 11, 30, 22, 57, 56, 789000000),
				8765.4321, true, 9223372036854770000L,
				new BigDecimal("12345678901234.56"),
				"text1", "EjRWeJCrzeI=", "/ty6CYdlQyI=",
				"a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11");
		// empty
		verifyRow(1, "                         ", "", 0,
				LocalDate.of(2000, 1, 2),
				LocalTime.of(0, 0, 0),
				LocalDateTime.of(2000, 1, 2, 3, 4, 5, 678000000),
				0., false, 0L,
				new BigDecimal("0.00"), "", "", "AAAAAAAAAAA=",
				"00000000-0000-0000-0000-000000000000");
		// null
		verifyRow(2, null, null, null, null, null,
				null, null, null, null,
				null, null, null, null, null);
		// auto
		verifyRow(3, "char_type 1384656904     ", "varchar_type 1384684104",
				1384653604, LocalDate.parse("1976-12-12"), LocalTime.parse("08:26:44"),
				LocalDateTime.parse("1900-01-05T04:53:24.004"), 13846684.04, false,
				1384644904L, new BigDecimal("13846469.04"), "clob_type 1384603204",
				"YmxvYl90eXBlIDEzODQ2NzU0MDQ=", "ODQ2NTIzMDQ=",
				"988543c3-b42c-3ce1-8da5-9bad5175fd20");
	}

	private void verifyRow(
			int id, String char_type, String varchar_type,
			Integer integer_type, LocalDate date_type, LocalTime time_type,
			LocalDateTime timestamp_type, Double double_type, Boolean boolean_type,
			Long bigint_type, BigDecimal decimal_type, String clob_type,
			String blob_type, String binary_type, String uuid_type) {
		assertEquals("id " + id, id, values.get(id).get("id"));
		assertEquals("char_type " + id, char_type, values.get(id).get("char_type"));
		assertEquals("varchar_type " + id, varchar_type, values.get(id).get("varchar_type"));
		assertEquals("integer_type " + id, integer_type, values.get(id).get("integer_type"));
		verifyField(id, "date_type", date_type, Function.identity(), TimeUtils::toLocalDate);
		verifyField(id, "time_type", time_type, Function.identity(), TimeUtils::toLocalTime);
		verifyField(id, "timestamp_type", timestamp_type, Function.identity(), TimeUtils::toLocalDateTime);
		assertEquals("double_type type " + id, double_type, values.get(id).get("double_type"));
		assertEquals("boolean_type type " + id, boolean_type, values.get(id).get("boolean_type"));
		assertEquals("bigint_type type " + id, bigint_type, values.get(id).get("bigint_type"));
		assertEquals("decimal_type type " + id, decimal_type, values.get(id).get("decimal_type"));
		assertEquals("clob_type type " + id, clob_type, values.get(id).get("clob_type"));
		assertEquals("blob_type type " + id, blob_type, convertBytesToString(values.get(id).get("blob_type")));
		assertEquals("binary_type type " + id, binary_type, convertBytesToString(values.get(id).get("binary_type")));
		verifyField(id, "uuid_type", uuid_type, Function.identity(), Object::toString);
	}
}
