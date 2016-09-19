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
public class ReplaceFailureTest extends CommonTestBase {

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a(id int primary key, a1 varchar(255), "
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
		db.update("insert into a (id,a1,date1,time1,timestamp1) values "
				+ "(0,'value0','2011-10-21','22:49:48','2011-11-21 23:59:58.123')");
		db.update("insert into a (id,a1,date1,time1,timestamp1) values "
				+ "(1,'value1','2009-08-27','19:49:58','2009-08-28 19:49:59.986')");
		db.update("insert into a (id,a1,date1,time1,timestamp1) values "
				+ "(2,'value2','2011-10-22','22:49:49','2011-11-22 23:59:59.124')");
	}

	public void testVerifyException(Throwable error) {
		String msg = "Differences found between the expected data set and actual database content.\n" +
				"Found differences for table a:\n" +
				"  Different row: {id=1, a1=null, date1=2009-08-28, time1=19:49:59, timestamp1=2009-08-28T19:49:59.987}\n" +
				"   Best matching differences: \n" +
				"    a1: expected [null], but was [value1]\n" +
				"    date1: expected [2009-08-28], but was [2009-08-27]\n" +
				"    time1: expected [19:49:59], but was [19:49:58]\n" +
				"    timestamp1: expected [2009-08-28T19:49:59.987], but was [2009-08-28T19:49:59.986]\n";
		assertEquals(msg, error.getMessage());
	}

}
