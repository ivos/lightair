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
public class DataTypesPostgresTest extends DataTypesTestBase {

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
		db.execute("delete from data_types");
		// full
		db.update("insert into data_types values (0,"
				+ " 'efghijklmnopqrs', 'abcdefghijklmnopqrstuvxyz', 'abcdefghijklmnopqrstuvxyz1234567890',"
				+ " 5678, 12345678, 9223372036854770000,"
				+ " 123456789012345678.91, 12345678.90123456,"
				+ " 876.543, 8765.4321,"
				+ " '2999-12-31', '23:59:58', '2998-11-30 22:57:56.789',"
				+ " true,"
				+ " '\\x31323334353637383930616263646532')"); // = hexa(1234567890abcde2)
		// empty
		db.update("insert into data_types values (1,"
				+ " '', '', '',"
				+ " 0, 0, 0,"
				+ " 0, 0,"
				+ " 0, 0,"
				+ " '2000-01-02', '00:00:00', '2000-01-02 03:04:05.678',"
				+ " false,"
				+ " '')");
		// null
		db.update("insert into data_types values (2,"
				+ " null, null, null,"
				+ " null, null, null,"
				+ " null, null,"
				+ " null, null,"
				+ " null, null, null,"
				+ " null,"
				+ " null)");
		// auto
		db.update("insert into data_types values (3,"
				+ " 'char_type 1384656904', 'varchar_type 1384684104', 'text_type 1384616204',"
				+ " 7904, 1384653604, 1384644904,"
				+ " 13846469.04, 13.84612704,"
				+ " 13846530, 13846684.04,"
				+ " '1976-12-12', '08:26:44', '1900-01-05T04:53:24.004',"
				+ " false,"
				+ " '\\x626c6f625f747970652031333834363735343034')"); // = hexa(blob_type 1384675404)
	}
}