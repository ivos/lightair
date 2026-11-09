package it.verify.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.LightAirApi;
import net.sf.lightair.annotation.Verify;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.support.ConfigSupport;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

@ExtendWith(LightAir.class)
@Verify
public class ReplaceNewTimestampTest extends CommonTestBase {
	// on each test method verification, a new current time is used

	@BeforeAll
	public static void beforeClass() {
		db.execute("create table a(id int primary key, time1 time, timestamp1 timestamp)");
		LightAirApi.resetClock();
		ConfigSupport.replaceConfig("diff1");
	}

	@AfterAll
	public static void afterClass() {
		db.execute("drop table a");
		ConfigSupport.restoreConfig();
	}

	@Test
	public void test() throws InterruptedException {
		Thread.sleep(1_000L);
		db.execute("delete from a");
		db.update("insert into a (id,time1,timestamp1) values " + "(1,'"
			+ new Time(new Date().getTime()) + "','"
			+ new Timestamp(new Date().getTime()) + "')");
	}
}
