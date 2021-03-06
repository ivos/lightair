package it.setup.core;

import it.common.CommonTestBase;
import net.sf.lightair.annotation.Setup;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.support.ApiTestSupport;
import test.support.ExceptionVerifyingJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(ExceptionVerifyingJUnitRunner.class)
@Setup
public class AnyTest extends CommonTestBase {

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a (id int primary key, a1 varchar(50))");
		ApiTestSupport.reInitialize();
	}

	@AfterClass
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
