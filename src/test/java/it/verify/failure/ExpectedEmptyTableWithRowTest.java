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
 * Table expected empty has a row in database.
 */
@ExtendWith({ExceptionVerifyingExtension.class, LightAir.class})
@Verify
public class ExpectedEmptyTableWithRowTest extends CommonTestBase {

	@BeforeAll
	public static void beforeClass() {
		db.execute("create table filled(id int primary key, a1 varchar(255))");
		db.execute("create table empty_(id int primary key, b1 varchar(255))");
		ApiTestSupport.reInitialize();
	}

	@AfterAll
	public static void afterClass() {
		db.execute("drop table filled");
		db.execute("drop table empty_");
	}

	@Test
	public void test() {
		db.execute("delete from filled");
		db.update("insert into filled (id,a1) values (0,'a01')");
		db.update("insert into filled (id,a1) values (1,'a11')");
		db.update("insert into filled (id,a1) values (2,'a21')");
		db.execute("delete from empty_");
		db.update("insert into empty_ (id,b1) values (0,'b01')");
	}

	public void testVerifyException(Throwable error) {
		String msg = "Differences found between the expected data set and actual database content.\n" +
			"Found differences for table empty_:\n" +
			"  Unexpected row: {id=0, b1=b01}\n";
		assertEquals(msg, error.getMessage());
	}
}
