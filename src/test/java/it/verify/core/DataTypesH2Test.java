package it.verify.core;

import it.common.DataTypesTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.support.ApiTestSupport;
import test.support.ConfigSupport;

@RunWith(LightAir.class)
@Verify
public class DataTypesH2Test extends DataTypesTestBase {

	static {
		connect("jdbc:h2:mem:test", "sa", "");
		ConfigSupport.restoreConfig();
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
				+ "uuid_type uuid, varchar_array_type array)");
		ApiTestSupport.reInitialize();
	}

	@Test
	public void test() {
		db.execute("delete from data_types");
		// full
		db.update("insert into data_types values (0, 'efghijklmnopqrs', "
				+ "'abcdefghijklmnopqrstuvxyz', 12345678, "
				+ "'2999-12-31', '23:59:58', '2998-11-30 22:57:56.789', "
				+ "8765.4321, true, 9223372036854770000, "
				+ "12345678901234.56, 'text1', '1234567890abcde2', 'fedcba0987654322', "
				+ "'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', "
				+ "array['varchar 1','varchar 2','',' ',null,'varchar 3'])");
		// empty
		db.update("insert into data_types values (1, '', '', 0, "
				+ "'2000-01-02', '00:00:00', '2000-01-02 03:04:05.678', "
				+ "0, false, 0, 0, '', '', '', '00000000-0000-0000-0000-000000000000', array[])");
		// null
		db.update("insert into data_types values (2, null, null, null, "
				+ "null, null, null, "
				+ "null, null, null, null, null, null, null, null, null)");
		// auto
		db.update("insert into data_types values (3, 'unsupported', "
				+ "'varchar_type 1384684104', 1384653604, "
				+ "'1976-12-12', '08:26:44', '1900-01-05 04:53:24.004', "
				+ "13846684.04, false, 1384644904, "
				+ "13846469.04, 'clob_type 1384603204', '626c6f625f747970652031333834363735343034', "
				+ "'3834363532333034', '988543c3-b42c-3ce1-8da5-9bad5175fd20', "
				+ "array['varchar_array_type 13846786041','varchar_array_type 13846786042','varchar_array_type 13846786043'])");

		// H2 does not support proper CHAR data type, instead of right-padding with spaces,
		// it truncates all trailing spaces (!).
		// See https://groups.google.com/forum/#!topic/h2-database/oB3Wrv0obEQ
		// The @auto token on CHAR types however generates value right-padded with spaces
		// to enable proper match when comparing the values in @Verify.
		// Therefore @auto on CHAR types in H2 cannot be used in @Verify.
	}
}
