package it.verify.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(LightAir.class)
@Verify
public class AnyTest extends CommonTestBase {

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a(id int primary key, a1 varchar(255), int1 int, "
				+ "double1 double, date1 date, time1 time, timestamp1 timestamp)");
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a");
	}

	@Before
	public void before() {
	}

	@Test
	public void test() {
		db.execute("delete from a");
		db.update("insert into a (id,a1,int1,double1,date1,time1,timestamp1) values "
				+ "(0,'value0',123,567.89,'2011-10-21','22:49:48','2011-11-21 23:59:58.123')");

		// @any
		db.update("insert into a (id,a1,int1,double1,date1,time1,timestamp1) values "
				+ "(1,'value1',321,987.65,'2010-12-24','09:45:32','2010-12-24 09:45:32.654')");
		db.update("insert into a (id,a1,int1,double1,date1,time1,timestamp1) values "
				+ "(2,null,null,null,null,null,null)");

		db.update("insert into a (id,a1,int1,double1,date1,time1,timestamp1) values "
				+ "(3,'value2',124,568.90,'2011-10-22','22:49:49','2011-11-22 23:59:59.124')");
	}

}
