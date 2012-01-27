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
public class ReplaceTest extends CommonTestBase {

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a(id int primary key, a1 varchar(255), "
				+ "date1 date, time1 time, timestamp1 timestamp)");
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
		db.update("insert into a (id,a1,date1,time1,timestamp1) values "
				+ "(0,'value0','2011-10-21','22:49:48','2011-11-21 23:59:58.123')");

		// @date:
		db.update("insert into a (id,a1,date1,time1,timestamp1) values "
				+ "(1,null,'2009-08-28','00:00:00','2009-08-28 00:00:00.000')");
		// @time:
		db.update("insert into a (id,a1,date1,time1,timestamp1) values "
				+ "(2,null,'1970-01-01','19:49:59','1970-01-01 19:49:59.000')");
		// @timestamp:
		db.update("insert into a (id,a1,date1,time1,timestamp1) values "
				+ "(3,null,'2009-08-28','19:49:59','2009-08-28 19:49:59.987')");

		db.update("insert into a (id,a1,date1,time1,timestamp1) values "
				+ "(4,'value2','2011-10-22','22:49:49','2011-11-22 23:59:59.124')");
	}

}
