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

/**
 * Table expected empty has a row in database.
 */
@RunWith(ExceptionVerifyingJUnitRunner.class)
@Verify
public class ExpectedEmptyTableWithRowTest extends CommonTestBase {

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table filled(id int primary key, a1 varchar(255))");
		db.execute("create table empty_(id int primary key, b1 varchar(255))");
		ApiTestSupport.reInitialize();
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table filled");
		db.execute("drop table empty_");
	}

	@Test
	public void test() {
		db.execute("delete from filled");
		db.update("insert into filled (id,a1) values (0,'a01')");
		db.update("insert into filled (id,a1) values (1,'a11')");
		db.update("insert into filled (id,a1) values (2,'a21')");
		db.execute("delete from empty_");
		db.update("insert into empty_ (id,b1) values (0,'b01')");
	}

	public void testVerifyException(Throwable error) {
		String msg = "Assertion failed. "
				+ "Differences found between the expected data set and actual database content.\n"
				+ "Expected table to be empty but found rows for table PUBLIC.empty_\n\n\n"
				+ "Actual database content:\n\nPUBLIC.FILLED\n"
				+ "  ID, A1\n  0, \"a01\"\n  1, \"a11\"\n  2, \"a21\"\n\n"
				+ "PUBLIC.EMPTY_\n  ID, B1\n  0, \"b01\"\n\n";
		assertEquals(msg, error.getMessage());
	}

}
