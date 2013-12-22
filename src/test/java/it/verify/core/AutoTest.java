package it.verify.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(LightAir.class)
@Verify
public class AutoTest extends CommonTestBase {

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a1 (id int primary key, char_type char(20), "
				+ "varchar_type varchar(50), integer_type integer, "
				+ "date_type date, time_type time, timestamp_type timestamp, "
				+ "double_type double, boolean_type boolean, bigint_type bigint, "
				+ "decimal_type decimal(20,3), clob_type clob, blob_type blob, binary_type binary(20))");
		db.execute("create table a2 (id int primary key, char_type char(20), "
				+ "varchar_type varchar(50), integer_type integer, "
				+ "date_type date, time_type time, timestamp_type timestamp, "
				+ "double_type double, boolean_type boolean, bigint_type bigint, "
				+ "decimal_type decimal(20,3), clob_type clob, blob_type blob, binary_type binary(20))");
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a1");
		db.execute("drop table a2");
	}

	@Test
	public void test() {
		db.execute("delete from a1");
		db.update("insert into a1 (id,char_type,varchar_type,integer_type,date_type,time_type,timestamp_type,"
				+ "double_type,boolean_type,bigint_type,decimal_type,clob_type,blob_type,binary_type) values "
				+ "(2734900,'char_type 2736900','varchar_type 2734100',2733600,'2004-04-25','16:13:20',"
				+ "'1990-05-09T15:26:40',27384.0,false,2734900,27369.0,'clob_type 2733200',"
				+ "'626c6f625f747970652032373335343030','62696e6172795f747970652032373332333030')");
	}

}
