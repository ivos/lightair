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
public class AutoFailureTest extends CommonTestBase {

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
		db.update("insert into a (id,a1,a2,a3) values (4214900,'a1 4217300','a2 4213100','a3 4216600')");
		db.update("insert into a (id,a1,a2,a3) values (4214901,'a1 4217301','a2 4213101x','a3 4216601')");
		db.update("insert into a (id,a1,a2,a3) values (4214902,'a1 4217302','a2 4213102','a3 4216602')");
	}

	public void testVerifyException(Throwable error) {
		String msg = "Assertion failed. "
				+ "Differences found between the expected data set and actual database content.\n"
				+ "Found differences for table PUBLIC.a:\n\n"
				+ "  Different row: \n  id, a1, a2, a3\n"
				+ "  \"4214901\", \"a1 4217301\", \"a2 4213101\", \"a3 4216601\"\n\n"
				+ "  Best matching differences:  \n"
				+ "  a2: \"a2 4213101\" <-> \"a2 4213101x\"\n\n\n"
				+ "Actual database content:\n\nPUBLIC.A\n  ID, A1, A2, A3\n"
				+ "  4214900, \"a1 4217300\", \"a2 4213100\", \"a3 4216600\"\n"
				+ "  4214901, \"a1 4217301\", \"a2 4213101x\", \"a3 4216601\"\n"
				+ "  4214902, \"a1 4217302\", \"a2 4213102\", \"a3 4216602\"\n\n";
		assertEquals(msg, error.getMessage());
	}

}
