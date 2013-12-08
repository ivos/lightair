package it.setup.core;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;

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
	protected String convertBytesToString(Object bytes) {
		return (String) bytes;
	}

}
