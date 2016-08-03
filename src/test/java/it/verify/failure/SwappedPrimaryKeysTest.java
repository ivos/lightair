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
 * When two rows have exchanged primary keys, do not match rows by the primary
 * keys, but by the other matching columns, thus limiting the number of
 * differences to minimum.
 * <p>
 * In other words, primary keys are considered just as any other columns in row
 * matching, they are not given priority.
 */
@RunWith(ExceptionVerifyingJUnitRunner.class)
@Verify
public class SwappedPrimaryKeysTest extends CommonTestBase {

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
		db.update("insert into a (id,a1,a2,a3) values (0,'01','02','03')");
		db.update("insert into a (id,a1,a2,a3) values (1,'11','12','13')");
	}

	public void testVerifyException(Throwable error) {
		String msg = "Differences found between the expected data set and actual database content.\n" +
				"Found differences for table a:\n" +
				"  Different row: {id=1, a1=01, a2=02, a3=03}\n" +
				"   Best matching differences: \n" +
				"    id: expected [1], but was [0]\n" +
				"  Different row: {id=0, a1=11, a2=12, a3=13}\n" +
				"   Best matching differences: \n" +
				"    id: expected [0], but was [1]\n";
		assertEquals(msg, error.getMessage());
	}

}
