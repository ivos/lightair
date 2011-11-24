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
public class ValueFailureTest extends CommonTestBase {

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a(id int primary key, a1 varchar(255), "
				+ "a2 varchar(255), a3 varchar(255))");
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

	public void testVerifyException(AssertionError error) {
		String msg = "Assertion failed. "
				+ "Differences found between the expected data set and actual database content.\n"
				+ "Found differences for table PUBLIC.a:\n\n"
				+ "  Different row: \n  id, a1, a2, a3\n"
				+ "  \"1\", \"11\", \"abc\", \"13\"\n\n"
				+ "  Best matching differences:  \n"
				+ "  a2: \"abc\" <-> \"aXc\"\n\n\n"
				+ "Actual database content:\n\nPUBLIC.A\n  ID, A1, A2, A3\n"
				+ "  0, \"01\", \"02\", \"03\"\n  1, \"11\", \"aXc\", \"13\"\n"
				+ "  2, \"21\", \"22\", \"23\"\n\n";
		assertEquals(msg, error.getMessage());
	}

}
