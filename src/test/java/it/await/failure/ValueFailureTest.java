package it.await.failure;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Await;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.support.ConfigSupport;
import test.support.ExceptionVerifyingExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({ExceptionVerifyingExtension.class, LightAir.class})
@Await
public class ValueFailureTest extends CommonTestBase {

	@BeforeAll
	public static void beforeClass() {
		db.execute("create table a(id int primary key, a1 varchar(255), "
			+ "a2 varchar(255), a3 varchar(255))");
		ConfigSupport.replaceConfig("diff1");
	}

	@AfterAll
	public static void afterClass() {
		db.execute("drop table a");
		ConfigSupport.restoreConfig();
	}

	@Test
	public void test() {
		db.execute("delete from a");
		db.update("insert into a (id,a1,a2,a3) values (0,'01','02','03')");
		db.update("insert into a (id,a1,a2,a3) values (1,'11','aXc','13')");
		db.update("insert into a (id,a1,a2,a3) values (2,'21','22','23')");
	}

	public void testVerifyException(Throwable error) {
		String msg = "Differences found between the expected data set and actual database content.\n" +
			"Found differences for table a:\n" +
			"  Different row: {id=1, a1=11, a2=abc, a3=13}\n" +
			"   Best matching differences: \n" +
			"    a2: expected [abc], but was [aXc]\n";
		assertEquals(msg, error.getMessage());
	}
}
