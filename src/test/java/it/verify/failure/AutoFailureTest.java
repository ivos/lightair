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
		db.update("insert into a (id,a1,a2,a3) values (1042184901,'a1 1042127301','a2 1042103101','a3 1042136601')");
		db.update("insert into a (id,a1,a2,a3) values (1042184902,'a1 1042127302','a2 1042103102x','a3 1042136602')");
		db.update("insert into a (id,a1,a2,a3) values (1042184903,'a1 1042127303','a2 1042103103','a3 1042136603')");
	}

	public void testVerifyException(Throwable error) {
		String msg = "Differences found between the expected data set and actual database content.\n" +
				"Found differences for table a:\n" +
				"  Different row: {id=1042184902, a1=a1 1042127302, a2=a2 1042103102, a3=a3 1042136602}\n" +
				"   Best matching differences: \n" +
				"    a2: expected [a2 1042103102], but was [a2 1042103102x]\n";
		assertEquals(msg, error.getMessage());
	}

}
