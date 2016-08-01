package it.verify.failure;

import it.common.CommonTestBase;
import net.sf.lightair.annotation.Verify;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.support.ApiTestSupport;
import test.support.ExceptionVerifyingJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(ExceptionVerifyingJUnitRunner.class)
@Verify
public class DurationFailureTest extends CommonTestBase {

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
		db.update("insert into a (id,date1,time1,timestamp1) values "
				+ "(1,'2009-08-27','19:49:58','2009-08-28 19:49:59.986')");
		db.update("insert into a (id,date1,time1,timestamp1) values "
				+ "(2,'2011-10-22','22:49:49','2011-11-22 23:59:59.124')");
	}

	public void testVerifyException(Throwable error) {
		String msg = "Assertion failed. "
				+ "Differences found between the expected data set and actual database content.\n"
				+ "Found differences for table PUBLIC.a:\n\n"
				+ "  Different row: \n  id, date1, time1, timestamp1\n"
				+ "  \"1\", 2007-05-24 00:00:00.0, 1970-01-01 21:53:03.0, 2007-05-24 14:43:52.987\n\n"
				+ "  Best matching differences:  \n"
				+ "  date1: 2007-05-24 00:00:00.0 <-> 2009-08-27\n"
				+ "  time1: 1970-01-01 21:53:03.0 <-> 19:49:58\n"
				+ "  timestamp1: 2007-05-24 14:43:52.987 <-> 2009-08-28 19:49:59.986\n\n\n"
				+ "Actual database content:\n\nPUBLIC.A\n  ID, DATE1, TIME1, TIMESTAMP1\n"
				+ "  0, 2011-10-21, 22:49:48, 2011-11-21 23:59:58.123\n"
				+ "  1, 2009-08-27, 19:49:58, 2009-08-28 19:49:59.986\n"
				+ "  2, 2011-10-22, 22:49:49, 2011-11-22 23:59:59.124\n\n";
		assertEquals(msg, error.getMessage());
	}

}
