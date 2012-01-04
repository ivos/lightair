package it.verify.failure;

import static org.junit.Assert.*;
import it.common.CommonTestBase;
import net.sf.lightair.annotation.Verify;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.support.ExceptionVerifyingJUnitRunner;

@RunWith(ExceptionVerifyingJUnitRunner.class)
@Verify
public class SchemaValueFailureTest extends CommonTestBase {

	@BeforeClass
	public static void beforeClass() {
		db.execute("create schema s1 authorization sa");
		db.execute("create table s1.ts1 (id int primary key, a varchar(50))");
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table s1.ts1");
		db.execute("drop schema s1");
	}

	@Test
	public void test() {
		db.execute("delete from s1.ts1");
		db.update("insert into s1.ts1 (id,a) values (0,'a10')");
		db.update("insert into s1.ts1 (id,a) values (1,'a11')");
		db.update("insert into s1.ts1 (id,a) values (2,'a12')");
	}

	public void testVerifyException(AssertionError error) {
		String msg = "Assertion failed. "
				+ "Differences found between the expected data set and actual database content.\n"
				+ "Found differences for table s1.ts1:\n\n"
				+ "  Different row: \n  id, a\n  \"1\", \"jkl\"\n\n"
				+ "  Best matching differences:  \n"
				+ "  a: \"jkl\" <-> \"a11\"\n\n\n"
				+ "Actual database content:\n\ns1.TS1\n  ID, A\n"
				+ "  0, \"a10\"\n  1, \"a11\"\n  2, \"a12\"\n\n";
		assertEquals(msg, error.getMessage());
	}

}
