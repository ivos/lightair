package it.setup.core;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Date;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;

@RunWith(LightAir.class)
@Setup
public class DataTypesOracleTest extends DataTypesTestBase {

	static {
		db = connect("jdbc:oracle:thin:@localhost:1521:xe", "SYSTEM",
				"password");
		replaceConfig("oracle");
	}

	@BeforeClass
	public static void beforeClass() {
		createTable(db);
	}

	public static void createTable(JdbcTemplate db) {
		db.execute("create table data_types (id number(10,0) primary key, char_type char(20), "
				+ "varchar_type varchar2(50), integer_type integer, "
				+ "date_type date, time_type timestamp, timestamp_type timestamp, "
				+ "double_type binary_double, boolean_type number(1), bigint_type number(19), "
				+ "decimal_type decimal(20,2), clob_type clob, blob_type blob, binary_type raw(8))");
	}

	@Test
	// requires Oracle XE 11.2.0
	// installed locally at default path /u01/app/oracle/product/11.2.0/xe
	// with user SYSTEM / password
	// @Ignore
	public void test() {
		perform();
	}

	@Override
	protected void verify() {
		verifyRow(0, "efghijklmnopqrs     ", "abcdefghijklmnopqrstuvxyz",
				12345678, new DateMidnight(2999, 12, 31), new LocalTime(23, 59,
						58), new DateTime(2998, 11, 30, 22, 57, 56, 789),
				8765.4321, true, 9223372036854770000L, new BigDecimal(
						"12345678901234.56"), "text1", "EjRWeJCrzeI=",
				"/ty6CYdlQyI=");
		verifyRow(1, null, null, 0, new DateMidnight(2000, 1, 2),
				new LocalTime(0, 0, 0), new DateTime(2000, 1, 2, 3, 4, 5, 678),
				0, false, 0L, new BigDecimal("0"), null, null, null);
	}

	@Override
	protected void verifyRow(int id, String char_type, String varchar_type,
			int integer_type, DateMidnight date_type, LocalTime time_type,
			DateTime timestamp_type, double double_type, boolean boolean_type,
			long bigint_type, BigDecimal decimal_type, String clob_type,
			String blob_type, String binary_type) {
		assertEquals("id " + id, id,
				((BigDecimal) values.get(id).get("id")).intValue());
		assertEquals("char_type " + id, char_type,
				values.get(id).get("char_type"));
		assertEquals("varchar_type " + id, varchar_type,
				values.get(id).get("varchar_type"));
		assertEquals("integer_type " + id, integer_type, ((BigDecimal) values
				.get(id).get("integer_type")).intValue());
		assertEquals("date_type " + id, date_type.toDate(),
				values.get(id).get("date_type"));
		assertEquals("time_type " + id, time_type,
				LocalTime
						.fromDateFields((Date) values.get(id).get("time_type")));
		assertEquals("timestamp_type " + id, timestamp_type.toDate(), values
				.get(id).get("timestamp_type"));
		assertEquals("double_type type " + id, double_type,
				values.get(id).get("double_type"));
		assertEquals("boolean_type type " + id, boolean_type,
				1 == ((BigDecimal) values.get(id).get("boolean_type"))
						.intValue());
		assertEquals("bigint_type type " + id, bigint_type,
				((BigDecimal) values.get(id).get("bigint_type")).longValue());
		assertEquals("decimal_type type " + id, decimal_type, values.get(id)
				.get("decimal_type"));
		assertEquals("clob_type type " + id, clob_type,
				values.get(id).get("clob_type"));
		assertEquals("blob_type type " + id, blob_type,
				convertBytesToString(values.get(id).get("blob_type")));
		assertEquals("binary_type type " + id, binary_type,
				convertBytesToString(values.get(id).get("binary_type")));
	}

}
