package it.setup.core;

import net.sf.lightair.LightAir;
import net.sf.lightair.LightAirApi;
import net.sf.lightair.annotation.Setup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.support.ApiTestSupport;
import test.support.ConfigSupport;

import java.time.Clock;
import java.time.ZoneOffset;
import java.util.TimeZone;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(LightAir.class)
@Setup
public class DataTypesH2TZTest extends DataTypesSetupTestBase {

	static TimeZone originalSystemTimeZone = TimeZone.getDefault();

	// DB and app (test) zone: 5. Light Air zone 3.
	// App (test) 2 hours ahead of Light Air.
	static {
		TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.ofHours(3)));
		LightAirApi.setClock(Clock.system(ZoneOffset.ofHours(5)));

		connect("jdbc:h2:mem:test;TIME ZONE=+03:00", "sa", "");
		ConfigSupport.replaceConfig("tz");
	}

	@BeforeAll
	public static void beforeClass() {
		createTable();
	}

	@AfterAll
	public static void afterClass() {
		TimeZone.setDefault(originalSystemTimeZone);
		LightAirApi.resetClock();
	}

	public static void createTable() {
		db.execute("create table data_types (id int primary key, "
			+ "timestamp_type timestamp, tstz_type timestamp with time zone)");
		ApiTestSupport.reInitialize();
	}

	@Test
	public void test() {
		perform();
	}

	@Override
	protected void verify() {
		// values without time zones
		verifyRow(0, "2021-02-22 10:22:33.987", "2022-02-22T10:22:33.987+03:00");
		// empty
		verifyRow(1, "2001-01-02 03:04:05.678", "2002-01-02T03:04:05.678+03:00");
		// null
		verifyRow(2, null, null);
		// auto
		verifyRow(3, "1900-01-05 04:53:24.004", "1901-05-20T05:01:44.504+03:00");
		// values with time zones
		verifyRow(4, "2021-02-20 09:20:30.123", "2022-02-20T09:20:30.123+03:00"
		);
	}

	private void verifyRow(int id, String timestamp_type, String tstz_type) {
		assertEquals(id, values.get(id).get("id"), "id " + id);
		verifyField(id, "timestamp_type", timestamp_type, Function.identity(), Object::toString);
		verifyField(id, "tstz_type", tstz_type, Function.identity(), Object::toString);
	}
}
