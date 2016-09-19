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
public class ValueFailureTest extends CommonTestBase {

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a(id int primary key, a1 varchar(255), "
				+ "a2 varchar(255), a3 varchar(255))");
		ApiTestSupport.reInitialize();
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a");
	}

	@Test
	public void test() {
		db.execute("delete from a");
		db.update("insert into a (id,a1,a2,a3) values (0,'01','02','03')");
		db.update("insert into a (id,a1,a2,a3) values (1,'11','aXc','13')");
		db.update("insert into a (id,a1,a2,a3) values (2,'21','22','23')");
	}

	public void testVerifyException(Throwable error) {
		String msg = "Differences found between the expected data set and actual database content.\n" +
				"Found differences for table a:\n" +
				"  Different row: {id=1, a1=11, a2=abc, a3=13}\n" +
				"   Best matching differences: \n" +
				"    a2: expected [abc], but was [aXc]\n";
		assertEquals(msg, error.getMessage());
	}

}
