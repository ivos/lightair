package it.verify.core;

import it.common.DataTypesTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@Ignore
@RunWith(LightAir.class)
@Verify
public class DataTypesOracleTest extends DataTypesTestBase {

	static {
		connect("jdbc:oracle:thin:@localhost:1521:xe", "SYSTEM", "password");
		replaceConfig("oracle");
	}

	@BeforeClass
	public static void beforeClass() {
		createTable();
	}

	public static void createTable() {
		db.execute("create table data_types (id number(10,0) primary key, char_type char(20), "
				+ "varchar_type varchar2(50), integer_type integer, "
				+ "date_type date, time_type timestamp, timestamp_type timestamp, "
				+ "double_type binary_double, boolean_type number(1), bigint_type number(19), "
				+ "decimal_type decimal(20,2), clob_type clob, blob_type blob, binary_type raw(8))");
	}

	@Test
	public void test() {
		db.execute("delete from data_types");
		db.update("insert into data_types values (0, 'efghijklmnopqrs', "
				+ "'abcdefghijklmnopqrstuvxyz', 12345678, "
				+ "to_date('2999-12-31','yyyy-mm-dd'), "
				+ "to_date('1970-01-01 23:59:58','yyyy-mm-dd hh24:mi:ss'), "
				+ "to_date('2998-11-30 22:57:56','yyyy-mm-dd hh24:mi:ss'), "
				+ "8765.4321, 1, 9223372036854770000, "
				+ "12345678901234.56, 'text1', '1234567890abcde2', 'fedcba0987654322')");
		db.update("insert into data_types values (1, '', '', 0, "
				+ "to_date('2000-01-02','yyyy-mm-dd'), "
				+ "to_date('1970-01-01 00:00:00','yyyy-mm-dd hh24:mi:ss'), "
				+ "to_date('2000-01-02 03:04:05','yyyy-mm-dd hh24:mi:ss'), "
				+ "0, 0, 0, 0, '', '', '')");
		db.update("insert into data_types values (2, null, null, null, "
				+ "null, null, null, "
				+ "null, null, null, null, null, null, null)");
	}

}
