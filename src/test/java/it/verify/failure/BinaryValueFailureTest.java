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
public class BinaryValueFailureTest extends CommonTestBase {

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a(id int primary key, a1 binary)");
		ApiTestSupport.reInitialize();
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a");
	}

	@Test
	public void test() {
		db.execute("delete from a");
		db.update("insert into a (id,a1) values (0,'fedcba0987654321')");
		db.update("insert into a (id,a1) values (1,'fedcba098765432f')");
		db.update("insert into a (id,a1) values (2,'fedcba0987654323')");
	}

	public void testVerifyException(Throwable error) {
		String msg = "Assertion failed. "
				+ "Differences found between the expected data set and actual database content.\n"
				+ "Found differences for table PUBLIC.a:\n\n"
				+ "  Different row: \n  id, a1\n"
				+ "  \"1\", \"/ty6CYdlQyI=\"\n\n"
				+ "  Best matching differences:  \n"
				+ "  a1: \"/ty6CYdlQyI=\" <-> \"/ty6CYdlQy8=\"\n\n\n"
				+ "Actual database content:\n\nPUBLIC.A\n  ID, A1\n"
				+ "  0, [-2, -36, -70, 9, -121, 101, 67, 33]\n"
				+ "  1, [-2, -36, -70, 9, -121, 101, 67, 47]\n"
				+ "  2, [-2, -36, -70, 9, -121, 101, 67, 35]\n\n";
		assertEquals(msg, error.getMessage());
	}

}
