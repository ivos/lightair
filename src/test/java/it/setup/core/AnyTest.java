package it.setup.core;

import static org.junit.Assert.*;
import it.common.CommonTestBase;
import net.sf.lightair.annotation.Setup;
import net.sf.lightair.exception.TokenAnyInSetupException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.support.ExceptionVerifyingJUnitRunner;

@RunWith(ExceptionVerifyingJUnitRunner.class)
@Setup
public class AnyTest extends CommonTestBase {

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a (id int primary key, a1 varchar(50))");
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a");
	}

	@Test
	public void test() {
	}

	public void testVerifyException(Throwable error) {
		assertEquals(TokenAnyInSetupException.class, error.getClass());
	}

}
