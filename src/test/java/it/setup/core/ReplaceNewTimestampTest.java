package it.setup.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.LightAirApi;
import net.sf.lightair.annotation.Setup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.support.ApiTestSupport;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(LightAir.class)
@Setup
public class ReplaceNewTimestampTest extends CommonTestBase {
	// on each test method setup, a new current time is used

	static List<Map<String, Object>> bothValues;

	@BeforeAll
	public static void beforeClass() {
		bothValues = new ArrayList<>();
		db.execute("create table a (id int primary key, timestamp1 timestamp)");
		ApiTestSupport.reInitialize();
		LightAirApi.setClock(Clock.systemDefaultZone());
	}

	@AfterAll
	public static void afterClass() {
		db.execute("drop table a");
	}

	@Test
	public void testPassA() throws InterruptedException {
		Thread.sleep(10L);
		performTest();
	}

	@Test
	public void testPassB() throws InterruptedException {
		Thread.sleep(10L);
		performTest();
	}

	private void performTest() {
		List<Map<String, Object>> values = db.queryForList("select * from a");
		bothValues.addAll(values);
		if (bothValues.size() > 1) {
			verifyColumn();
		}
	}

	private void verifyColumn() {
		long timeA = ((Date) bothValues.get(0).get("timestamp1")).getTime();
		long timeB = ((Date) bothValues.get(1).get("timestamp1")).getTime();
		assertTrue(timeB - timeA > 0, "Timestamp increased on second run");
	}
}
