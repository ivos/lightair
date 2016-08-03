package it.verify.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;
import org.joda.time.DateTimeUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.support.ConfigSupport;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

@RunWith(LightAir.class)
@Verify
public class ReplaceNewTimestampTest extends CommonTestBase {
	// on each test method verification, a new current time is used

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a(id int primary key, time1 time, timestamp1 timestamp)");
		DateTimeUtils.setCurrentMillisSystem();
		ConfigSupport.init();
		ConfigSupport.replaceConfig("diff1");
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a");
		ConfigSupport.restoreConfig();
	}

	@Test
	public void test() throws InterruptedException {
		Thread.sleep(1_000L);
		db.execute("delete from a");
		db.update("insert into a (id,time1,timestamp1) values " + "(1,'"
				+ new Time(new Date().getTime()).toString() + "','"
				+ new Timestamp(new Date().getTime()).toString() + "')");
	}

}
