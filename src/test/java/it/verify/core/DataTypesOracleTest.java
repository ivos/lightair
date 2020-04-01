package it.verify.core;

import it.common.DataTypesTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.support.ApiTestSupport;
import test.support.ConfigSupport;

/**
 * Requires:
 * <ul>
 * <li>Running Oracle XE 18.4.0.0.0 (18c)</li>
 * <li>User with credentials: SYSTEM / password.</li>
 * <li>Driver ojdbc8.jar copied into jre/lib/ext directory in the JDK.</li>
 * </ul>
 */
@Ignore
@RunWith(LightAir.class)
@Verify
public class DataTypesOracleTest extends DataTypesTestBase {

	static {
		connect("jdbc:oracle:thin:@localhost:1521:xe", "SYSTEM", "password");
		ConfigSupport.replaceConfig("oracle");
	}

	@BeforeClass
	public static void beforeClass() {
		createTable();
	}

	public static void createTable() {
		db.execute("create table data_types (id number(10,0) primary key,"
				+ " char_type char(25), varchar_type varchar(50), varchar2_type varchar2(50),"
				+ " nchar_type nchar(20), nvarchar2_type nvarchar2(50),"
				+ " integer_type integer, bigint_type number(19),"
				+ " decimal_type decimal(20,2), number_type number(16, 8), float_type float(126),"
				+ " binary_float_type binary_float, binary_double_type binary_double,"
				+ " date_type date, time_type timestamp, timestamp_type timestamp,"
				+ " boolean_type number(1),"
				+ " clob_type clob, nclob_type nclob, blob_type blob, binary_type raw(15)"
				+ ")");
		ApiTestSupport.reInitialize();
	}

	@Test
	public void test() {
		db.execute("delete from data_types");
		// full
		db.update("insert into data_types values (0,"
				+ " 'efghijklmnopqrs', 'abcdefghijklmnopqrstuvxyz', '2abcdefghijklmnopqrstuvxyz',"
				+ " 'nefghijklmnopqrs', 'n2abcdefghijklmnopqrstuvxyz',"
				+ " 12345678, 9223372036854770000,"
				+ " 12345678901234.56, 12345678.90123456, 876.543,"
				+ " 765.432, 8765.4321,"
				+ " to_date('2999-12-31','yyyy-mm-dd'),"
				+ " to_timestamp('1970-01-01 23:59:58','yyyy-mm-dd hh24:mi:ss'),"
				+ " to_timestamp('2998-11-30 22:57:56.789','yyyy-mm-dd hh24:mi:ss.FF3'),"
				+ " 1,"
				+ " 'text1', 'ntext1', '1234567890abcde2', 'fedcba0987654322'"
				+ ")");
		// empty
		db.update("insert into data_types values (1,"
				+ " '', '', '',"
				+ " '', '',"
				+ " 0, 0,"
				+ " 0, 0, 0,"
				+ " 0, 0,"
				+ " to_date('2000-01-02','yyyy-mm-dd'),"
				+ " to_timestamp('1970-01-01 00:00:00','yyyy-mm-dd hh24:mi:ss'),"
				+ " to_timestamp('2000-01-02 03:04:05.678','yyyy-mm-dd hh24:mi:ss.FF3'),"
				+ " 0,"
				+ " '', '', '', ''"
				+ ")");
		// null
		db.update("insert into data_types values (2,"
				+ " null, null, null,"
				+ " null, null,"
				+ " null, null,"
				+ " null, null, null,"
				+ " null, null,"
				+ " null,"
				+ " null,"
				+ " null,"
				+ " null,"
				+ " null, null, null, null"
				+ ")");
		// auto
		db.update("insert into data_types values (3,"
				+ " 'char_type 1384656904', 'varchar_type 1384684104', 'varchar2_type 1384671904',"
				+ " 'nchar_type1384698804', 'nvarchar2_type 1384660104',"
				+ " 1384653604, 1384644904,"
				+ " 13846469.04, 13.84603704, 13846568,"
				+ " 13846789, 13846774.04,"
				+ " to_date('1976-12-12','yyyy-mm-dd'),"
				+ " to_timestamp('1935-01-22 08:26:44.804','yyyy-mm-dd hh24:mi:ss.FF3'),"
				+ " to_timestamp('1900-01-05 04:53:24.004','yyyy-mm-dd hh24:mi:ss.FF3'),"
				+ " 0,"
				+ " 'clob_type 1384603204', 'nclob_type 1384602404',"
				+ " '626c6f625f747970652031333834363735343034', '62696e617231333834363532333034'"
				+ ")");
	}
}
