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
public class DurationTest extends CommonTestBase {

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a(id int primary key, "
				+ "date1 date, time1 time, timestamp1 timestamp)");
		ApiTestSupport.reInitialize();
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a");
	}

	@Test
	public void test() {
		db.execute("delete from a");
		db.update("insert into a (id,date1,time1,timestamp1) values "
				+ "(0,'2011-10-21','22:49:48','2011-11-21 23:59:58.123')");

		// @date:
		db.update("insert into a (id,date1,time1,timestamp1) values "
				+ "(1,'2009-08-26','00:00:03','2009-12-28 00:05:00.000')");
		// @time:
		db.update("insert into a (id,date1,time1,timestamp1) values "
				+ "(2,'1970-03-01','16:49:59','1974-01-06 19:56:06.000')");
		// @timestamp:
		db.update("insert into a (id,date1,time1,timestamp1) values "
				+ "(3,'2007-05-24','21:53:03','2007-05-24 14:43:52.987')");

		db.update("insert into a (id,date1,time1,timestamp1) values "
				+ "(4,'2011-10-22','22:49:49','2011-11-22 23:59:59.124')");
	}

}
