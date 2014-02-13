package it.setup.core;

import java.math.BigDecimal;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.support.ConfigSupport;

@RunWith(LightAir.class)
@Setup("DataTypesTest.xml")
public class DataTypesDerbyTest extends DataTypesSetupTestBase {

	static {
		connect("jdbc:derby:memory:test;create=true", "root", "root");
		ConfigSupport.replaceConfig("derby");
	}

	@BeforeClass
	public static void beforeClass() {
		createTable();
	}

	public static void createTable() {
		// Bug in Spring JdbcTemplate: LOB does not work
		db.execute("create table data_types (id int primary key, char_type char(20), "
				+ "varchar_type varchar(50), integer_type integer, "
				+ "date_type date, time_type time, timestamp_type timestamp, "
				+ "double_type double, boolean_type boolean, bigint_type bigint, "
				+ "decimal_type decimal(20,2), clob_type varchar(20), blob_type varchar(20), binary_type varchar(20))");
	}

	@Test
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
		verifyRow(1, "                    ", "", 0,
				new DateMidnight(2000, 1, 2), new LocalTime(0, 0, 0),
				new DateTime(2000, 1, 2, 3, 4, 5, 678), 0., false, 0L,
				new BigDecimal("0.00"), "", "", "");
		verifyRow(2, null, null, null, null, null, null, null, null, null,
				null, null, null, null);
		verifyRow(3, "char_type 8466903   ", "varchar_type 8464103", 8463603,
				DateMidnight.parse("1903-01-09"), LocalTime.parse("23:53:23"),
				DateTime.parse("2088-12-03T23:06:43.003"), 84684.03, true,
				8464903L, new BigDecimal("84670.03"), "clob_type 8463203",
				"blob_type 8465403", "binary_type 8462303");
	}

	@Override
	protected String convertBytesToString(Object bytes) {
		return (String) bytes;
	}

}
