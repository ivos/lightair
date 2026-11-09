package it.verify.failure;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.support.ApiTestSupport;
import test.support.ExceptionVerifyingExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({ExceptionVerifyingExtension.class, LightAir.class})
@Verify
public class BinaryValueFailureTest extends CommonTestBase {

	@BeforeAll
	public static void beforeClass() {
		db.execute("create table a(id int primary key, a1 varbinary(100))");
		ApiTestSupport.reInitialize();
	}

	@AfterAll
	public static void afterClass() {
		db.execute("drop table a");
	}

	@Test
	public void test() {
		db.execute("delete from a");
		db.update("insert into a (id,a1) values (0,X'fedcba0987654321')");
		db.update("insert into a (id,a1) values (1,X'fedcba098765432f')");
		db.update("insert into a (id,a1) values (2,X'fedcba0987654323')");
	}

	public void testVerifyException(Throwable error) {
		String msg = "Differences found between the expected data set and actual database content.\n" +
			"Found differences for table a:\n" +
			"  Different row: {id=1, a1=/ty6CYdlQyI=}\n" +
			"   Best matching differences: \n" +
			"    a1: expected [/ty6CYdlQyI=], but was [/ty6CYdlQy8=]\n";
		assertEquals(msg, error.getMessage());
	}
}
