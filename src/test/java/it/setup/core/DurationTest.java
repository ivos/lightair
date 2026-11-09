package it.setup.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.support.ApiTestSupport;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(LightAir.class)
@Setup
public class DurationTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeAll
	public static void beforeClass() {
		db.execute("create table a (id int primary key, date1 date, time1 time, timestamp1 timestamp)");
		ApiTestSupport.reInitialize();
	}

	@AfterAll
	public static void afterClass() {
		db.execute("drop table a");
	}

	@Test
	public void test() {
		assertEquals(Integer.valueOf(5), db.queryForObject("select count(*) from a", Integer.class), "Count");
		values = db.queryForList("select * from a");
		verifyRow(0, LocalDate.of(2011, 10, 21), LocalTime.of(22, 49, 48),
			LocalDateTime.of(2011, 11, 21, 23, 59, 58, 123000000));
		// @date:
		verifyRow(1, LocalDate.of(2009, 8, 26), LocalTime.of(0, 0, 3),
			LocalDateTime.of(2009, 12, 28, 0, 5, 0, 0));
		// @time:
		verifyRow(2, LocalDate.of(1970, 3, 1), LocalTime.of(16, 49, 59),
			LocalDateTime.of(1974, 1, 6, 19, 56, 6));
		// @timestamp:
		verifyRow(3, LocalDate.of(2007, 5, 24), LocalTime.of(21, 53, 3),
			LocalDateTime.of(2007, 5, 24, 14, 43, 52, 987000000));
		verifyRow(4, LocalDate.of(2011, 10, 22), LocalTime.of(22, 49, 49),
			LocalDateTime.of(2011, 11, 22, 23, 59, 59, 124000000));
	}

	private void verifyRow(int id, LocalDate date1, LocalTime time1, LocalDateTime timestamp1) {
		assertEquals(id, values.get(id).get("id"), "id " + id);
		assertEquals(date1.toString(), values.get(id).get("date1").toString(), "date1 " + id);
		assertEquals(time1, ((Time) values.get(id).get("time1")).toLocalTime(), "time1 " + id);
		assertEquals(timestamp1, ((Timestamp) values.get(id).get("timestamp1")).toLocalDateTime(), "timestamp1 " + id);
	}
}
