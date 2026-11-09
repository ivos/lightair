package it.setup.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.support.ApiTestSupport;
import test.support.ExceptionVerifyingExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({ExceptionVerifyingExtension.class, LightAir.class})
@Setup
public class AnyTest extends CommonTestBase {

	@BeforeAll
	public static void beforeClass() {
		db.execute("create table a (id int primary key, a1 varchar(50))");
		ApiTestSupport.reInitialize();
	}

	@AfterAll
	public static void afterClass() {
		db.execute("drop table a");
	}

	@Test
	public void test() {
	}

	public void testVerifyException(Throwable error) {
		String expected = "Token @any found in setup dataset. This token is only allowed in verification datasets.";
		assertEquals(expected, error.getMessage());
	}
}
