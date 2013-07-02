package it.verify.core;

import it.common.CommonTestBase;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;
import net.sf.lightair.internal.factory.Factory;

import org.joda.time.DateTimeUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(LightAir.class)
@Verify
public class ReplaceNewTimestampTest extends CommonTestBase {
	// on each test method verification, a new current time is used

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a(id int primary key, time1 time, timestamp1 timestamp)");
		DateTimeUtils.setCurrentMillisSystem();
		Factory.getInstance().init();
		Factory.getInstance().setTimeDifferenceLimit(1000);
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a");
	}

	@Before
	public void before() {
	}

	@Test
	public void test() throws InterruptedException {
		Thread.sleep(1000l);
		db.execute("delete from a");
		db.update("insert into a (id,time1,timestamp1) values " + "(1,'"
				+ new Time(new Date().getTime()).toString() + "','"
				+ new Timestamp(new Date().getTime()).toString() + "')");
	}

}
