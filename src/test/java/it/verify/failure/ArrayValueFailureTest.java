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
public class ArrayValueFailureTest extends CommonTestBase {

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a(id int primary key, a1 array)");
		ApiTestSupport.reInitialize();
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a");
	}

	@Test
	public void test() {
		db.execute("delete from a");
		db.update("insert into a (id,a1) values (0,array['varchar 11','varchar 12','varchar 13'])");
		db.update("insert into a (id,a1) values (1,array['varchar 21','varchar 22a','',' ',null,'varchar 23'])");
		db.update("insert into a (id,a1) values (2,array['varchar 31','varchar 32','',' ',null,'varchar 33'])");
	}

	public void testVerifyException(Throwable error) {
		String msg = "Differences found between the expected data set and actual database content.\n" +
				"Found differences for table a:\n" +
				"  Different row: {id=1, a1=varchar 21,varchar 22b,, ,@null,varchar 23}\n" +
				"   Best matching differences: \n" +
				"    a1: expected [varchar 21,varchar 22b,, ,@null,varchar 23], but was [varchar 21,varchar 22a,, ,@null,varchar 23]\n";
		assertEquals(msg, error.getMessage());
	}
}
