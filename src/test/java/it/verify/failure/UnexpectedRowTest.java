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
public class UnexpectedRowTest extends CommonTestBase {

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a(id int primary key, a1 varchar(255))");
		ApiTestSupport.reInitialize();
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a");
	}

	/**
	 * Single difference - the unexpected row.
	 */
	@Test
	public void onlyUnexpected() {
		db.execute("delete from a");
		db.update("insert into a (id,a1) values (0,'01')");
		db.update("insert into a (id,a1) values (1,'11')");
		db.update("insert into a (id,a1) values (2,'21')");
	}

	public void onlyUnexpectedVerifyException(Throwable error) {
		String msg = "Differences found between the expected data set and actual database content.\n" +
				"Found differences for table a:\n" +
				"  Unexpected row: {id=1, a1=11}\n";
		assertEquals(msg, error.getMessage());
	}

	/**
	 * Unexpected row and other difference.
	 */
	@Test
	public void unexpectedAndOtherDif() {
		db.execute("delete from a");
		db.update("insert into a (id,a1) values (0,'01')");
		db.update("insert into a (id,a1) values (1,'11')");
		db.update("insert into a (id,a1) values (2,'21w')");
	}

	public void unexpectedAndOtherDifVerifyException(Throwable error) {
		String msg = "Differences found between the expected data set and actual database content.\n" +
				"Found differences for table a:\n" +
				"  Different row: {id=2, a1=21}\n" +
				"   Best matching differences: \n" +
				"    a1: expected [21], but was [21w]\n" +
				"  Unexpected row: {id=1, a1=11}\n";
		assertEquals(msg, error.getMessage());
	}

}
