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
				+ "'1990-05-09T15:26:40',27384.0,false,2735000,2737.0,'clob_type 2733200',"
				+ "'626c6f625f747970652032373335343030','62696e6172795f747970652032373332333030')");
		db.update("insert into a1 (id,char_type,varchar_type,integer_type,date_type,time_type,timestamp_type,"
				+ "double_type,boolean_type,bigint_type,decimal_type,clob_type,blob_type,binary_type) values "
				+ "(2734901,'char_type 2736901','varchar_type 2734101',2733601,'2004-04-26','16:13:21',"
				+ "'1990-05-10T15:26:41.001',27384.01,true,2735001,2737.001,'clob_type 2733201',"
				+ "'626c6f625f747970652032373335343031','62696e6172795f747970652032373332333031')");
		db.update("insert into a1 (id,char_type,varchar_type,integer_type,date_type,time_type,timestamp_type,"
				+ "double_type,boolean_type,bigint_type,decimal_type,clob_type,blob_type,binary_type) values "
				+ "(2734902,'char_type 2736902','varchar_type 2734102',2733602,'2004-04-27','16:13:22',"
				+ "'1990-05-11T15:26:42.002',27384.02,false,2735002,2737.002,'clob_type 2733202',"
				+ "'626c6f625f747970652032373335343032','62696e6172795f747970652032373332333032')");

		db.execute("delete from a2");
		db.update("insert into a2 (id,char_type,varchar_type,integer_type,date_type,time_type,timestamp_type,"
				+ "double_type,boolean_type,bigint_type,decimal_type,clob_type,blob_type,binary_type) values "
				+ "(0314900,'char_type 0316900','varchar_type 0314100',313600,'1974-03-14','16:00:00',"
				+ "'1960-03-27T15:13:20',3184.0,false,315000,317.0,'clob_type 0313200',"
				+ "'626c6f625f747970652030333135343030','62696e6172795f747970652030333132333030')");
		db.update("insert into a2 (id,char_type,varchar_type,integer_type,date_type,time_type,timestamp_type,"
				+ "double_type,boolean_type,bigint_type,decimal_type,clob_type,blob_type,binary_type) values "
				+ "(0314901,'char_type 0316901','varchar_type 0314101',313601,'1974-03-15','16:00:01',"
				+ "'1960-03-28T15:13:21.001',3184.010,true,315001,317.001,'clob_type 0313201',"
				+ "'626c6f625f747970652030333135343031','62696e6172795f747970652030333132333031')");
	}

}
