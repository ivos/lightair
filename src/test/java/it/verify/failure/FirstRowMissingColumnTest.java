package it.verify.failure;

import static org.junit.Assert.*;
import it.common.CommonTestBase;
import net.sf.lightair.annotation.Verify;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.support.ExceptionVerifyingJUnitRunner;

/**
 * Each column specified on a given row is verified, even when it is not
 * specified on any other row (like e.g. the first row).
 */
@RunWith(ExceptionVerifyingJUnitRunner.class)
@Verify
public class FirstRowMissingColumnTest extends CommonTestBase {

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a(id int primary key, expected varchar(255), "
				+ "unspecified varchar(255))");
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a");
	}

	@Test
	public void test() {
		db.execute("delete from a");
		db.update("insert into a (id,expected,unspecified) values (0,'e0','u0')");
		db.update("insert into a (id,expected,unspecified) values (1,'e1','wrong')");
		db.update("insert into a (id,expected,unspecified) values (2,'e2','u2')");
	}

	public void testVerifyException(AssertionError error) {
		String msg = "Assertion failed. "
				+ "Differences found between the expected data set and actual database content.\n"
				+ "Found differences for table PUBLIC.a:\n\n"
				+ "  Different row: \n  id, expected, unspecified\n"
				+ "  \"1\", \"e1\", \"u1\"\n\n"
				+ "  Best matching differences:  \n"
				+ "  unspecified: \"u1\" <-> \"wrong\"\n\n\n"
				+ "Actual database content:\n\nPUBLIC.A\n"
				+ "  ID, EXPECTED, UNSPECIFIED\n  0, \"e0\", \"u0\"\n"
				+ "  1, \"e1\", \"wrong\"\n  2, \"e2\", \"u2\"\n\n";
		assertEquals(msg, error.getMessage());
	}

}
