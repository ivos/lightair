package it.verify.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.support.ApiTestSupport;

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
		ApiTestSupport.reInitialize();
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
				+ "(1327384901,'char_type 1327356901','varchar_type 1327384101',1327353601,'1990-08-18','03:46:41',"
				+ "'1913-09-11T00:13:21.001',13273684.01,true,1327344901,1327346.901,'clob_type 1327303201',"
				+ "'626c6f625f747970652031333237333735343031','62696e6172795f74797031333237333532333031')");
		db.update("insert into a1 (id,char_type,varchar_type,integer_type,date_type,time_type,timestamp_type,"
				+ "double_type,boolean_type,bigint_type,decimal_type,clob_type,blob_type,binary_type) values "
				+ "(1327384902,'char_type 1327356902','varchar_type 1327384102',1327353602,'1990-08-19','03:46:42',"
				+ "'1913-09-12T00:13:22.002',13273684.02,false,1327344902,1327346.902,'clob_type 1327303202',"
				+ "'626c6f625f747970652031333237333735343032','62696e6172795f74797031333237333532333032')");
		db.update("insert into a1 (id,char_type,varchar_type,integer_type,date_type,time_type,timestamp_type,"
				+ "double_type,boolean_type,bigint_type,decimal_type,clob_type,blob_type,binary_type) values "
				+ "(1327384903,'char_type 1327356903','varchar_type 1327384103',1327353603,'1990-08-20','03:46:43',"
				+ "'1913-09-13T00:13:23.003',13273684.03,true,1327344903,1327346.903,'clob_type 1327303203',"
				+ "'626c6f625f747970652031333237333735343033','62696e6172795f74797031333237333532333033')");

		db.execute("delete from a2");
		db.update("insert into a2 (id,char_type,varchar_type,integer_type,date_type,time_type,timestamp_type,"
				+ "double_type,boolean_type,bigint_type,decimal_type,clob_type,blob_type,binary_type) values "
				+ "(1603184901,'char_type 1603156901','varchar_type 1603184101',1603153601,'2007-01-21','06:53:21',"
				+ "'1930-02-14T03:20:01.001',16031684.01,true,1603144901,1603146.901,'clob_type 1603103201',"
				+ "'626c6f625f747970652031363033313735343031','62696e6172795f74797031363033313532333031')");
		db.update("insert into a2 (id,char_type,varchar_type,integer_type,date_type,time_type,timestamp_type,"
				+ "double_type,boolean_type,bigint_type,decimal_type,clob_type,blob_type,binary_type) values "
				+ "(1603184902,'char_type 1603156902','varchar_type 1603184102',1603153602,'2007-01-22','06:53:22',"
				+ "'1930-02-15T03:20:02.002',16031684.02,false,1603144902,1603146.902,'clob_type 1603103202',"
				+ "'626c6f625f747970652031363033313735343032','62696e6172795f74797031363033313532333032')");
	}

}
