package it.verify.failure;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.support.ApiTestSupport;
import test.support.ConfigSupport;
import test.support.ExceptionVerifyingExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({ExceptionVerifyingExtension.class, LightAir.class})
@Verify
public class TimeDifferenceLimitFailureTest extends CommonTestBase {

	@BeforeAll
	public static void beforeClass() {
		db.execute("create table a(id int primary key, "
			+ "date1 date, time1 time, timestamp1 timestamp)");
		ApiTestSupport.reInitialize();
	}

	@AfterAll
	public static void afterClass() {
		db.execute("drop table a");
		ConfigSupport.restoreConfig();
	}

	@BeforeEach
	public void before() {
		ConfigSupport.replaceConfig("diff60");
	}

	@Test
	public void test() {
		db.execute("delete from a");
		db.update("insert into a (id,date1,time1,timestamp1) values "
			+ "(0,'2011-10-21','22:49:48','2011-11-21 23:59:58.123')");
		db.update("insert into a (id,date1,time1,timestamp1) values "
			+ "(1,'2009-08-27','19:48:58','2009-08-28 19:50:59.988')");
		db.update("insert into a (id,date1,time1,timestamp1) values "
			+ "(2,'2011-10-22','22:49:49','2011-11-22 23:59:59.124')");
		db.update("insert into a (id) values (3)");
	}

	public void testVerifyException(Throwable error) {
		String msg = "Differences found between the expected data set and actual database content.\n" +
			"Found differences for table a:\n" +
			"  Different row: {id=1, date1=2009-08-28, time1=19:49:59, timestamp1=2009-08-28T19:49:59.987}\n" +
			"   Best matching differences: \n" +
			"    date1: expected [2009-08-28], but was [2009-08-27]\n" +
			"    time1: expected [19:49:59], but was [19:48:58]\n" +
			"    timestamp1: expected [2009-08-28T19:49:59.987], but was [2009-08-28T19:50:59.988]\n" +
			"  Different row: {id=3, date1=2009-08-28, time1=19:49:59, timestamp1=2009-08-28T19:49:59.987}\n" +
			"   Best matching differences: \n" +
			"    date1: expected [2009-08-28], but was [null]\n" +
			"    time1: expected [19:49:59], but was [null]\n" +
			"    timestamp1: expected [2009-08-28T19:49:59.987], but was [null]\n";
		assertEquals(msg, error.getMessage());
	}
}
