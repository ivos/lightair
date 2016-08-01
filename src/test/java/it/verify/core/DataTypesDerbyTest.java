package it.verify.core;

import it.common.DataTypesTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;

import org.apache.commons.codec.binary.Hex;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.support.ApiTestSupport;
import test.support.ConfigSupport;

@RunWith(LightAir.class)
@Verify
public class DataTypesDerbyTest extends DataTypesTestBase {

	static {
		connect("jdbc:derby:memory:test;create=true", "root", "root");
		ConfigSupport.replaceConfig("derby");
	}

	@BeforeClass
	public static void beforeClass() {
		createTable();
	}

	public static void createTable() {
		db.execute("create table data_types (id int primary key, char_type char(20), "
				+ "varchar_type varchar(50), integer_type integer, "
				+ "date_type date, time_type time, timestamp_type timestamp, "
				+ "double_type double, boolean_type boolean, bigint_type bigint, "
				+ "decimal_type decimal(20,2), clob_type clob, blob_type blob, binary_type blob)");
		ApiTestSupport.reInitialize();
	}

	@Test
	public void test() throws Exception {
		db.execute("delete from data_types");
		db.update("insert into data_types values (0, 'efghijklmnopqrs', "
				+ "'abcdefghijklmnopqrstuvxyz', 12345678, "
				+ "'2999-12-31', '23:59:58', '2998-11-30 22:57:56.789', "
				+ "8765.4321, true, 9223372036854770000, "
				+ "12345678901234.56, 'text1', ?, ?)",
				Hex.decodeHex("1234567890abcde2".toCharArray()),
				Hex.decodeHex("fedcba0987654322".toCharArray()));
		db.update("insert into data_types values (1, '', '', 0, "
				+ "'2000-01-02', '00:00:00', '2000-01-02 03:04:05.678', "
				+ "0, false, 0, 0, '', ?, ?)", new byte[0], new byte[0]);
		db.update("insert into data_types values (2, null, null, null, "
				+ "null, null, null, "
				+ "null, null, null, null, null, null, null)");
	}

}
