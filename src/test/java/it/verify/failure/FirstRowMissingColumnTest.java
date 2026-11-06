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

/**
 * Each column specified on a given row is verified, even when it is not
 * specified on any other row (like e.g. the first row).
 */
@ExtendWith({ExceptionVerifyingExtension.class, LightAir.class})
@Verify
public class FirstRowMissingColumnTest extends CommonTestBase {

	@BeforeAll
	public static void beforeClass() {
		db.execute("create table a(id int primary key, expected varchar(255), "
			+ "unspecified varchar(255))");
		ApiTestSupport.reInitialize();
	}

	@AfterAll
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

	public void testVerifyException(Throwable error) {
		String msg = "Differences found between the expected data set and actual database content.\n" +
			"Found differences for table a:\n" +
			"  Different row: {id=1, expected=e1, unspecified=u1}\n" +
			"   Best matching differences: \n" +
			"    unspecified: expected [u1], but was [wrong]\n";
		assertEquals(msg, error.getMessage());
	}
}
