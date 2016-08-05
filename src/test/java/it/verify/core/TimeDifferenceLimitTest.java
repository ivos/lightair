package it.verify.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.support.ConfigSupport;

@RunWith(LightAir.class)
@Verify
public class TimeDifferenceLimitTest extends CommonTestBase {

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a(id int primary key, "
				+ "date1 date, time1 time, timestamp1 timestamp)");
		ConfigSupport.replaceConfig("diff60");
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a");
		ConfigSupport.restoreConfig();
	}

	@Test
	public void test() {
		db.execute("delete from a");
		db.update("insert into a (id,date1,time1,timestamp1) values "
				+ "(0,'2011-10-21','22:49:48','2011-11-21 23:59:58.123')");

		// @date:
		db.update("insert into a (id,date1,time1,timestamp1) values "
				+ "(1,'2009-08-28','00:00:00','2009-08-28 00:00:00.000')");
		// @time:
		db.update("insert into a (id,date1,time1,timestamp1) values "
				+ "(2,'1970-01-01','19:49:00','1970-01-01 19:50:58.000')");
		// @timestamp:
		db.update("insert into a (id,date1,time1,timestamp1) values "
				+ "(3,'2009-08-28','19:50:58','2009-08-28 19:48:59.988')");

		db.update("insert into a (id,date1,time1,timestamp1) values "
				+ "(4,'2011-10-22','22:48:50','2011-11-23 00:00:01.123')");
	}

}
