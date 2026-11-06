package it.verify.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.support.ApiTestSupport;

/**
 * Unspecified column on a given row is ignored. (And not verified to be null.)
 */
@ExtendWith(LightAir.class)
@Verify
public class UnspecifiedColumnTest extends CommonTestBase {

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
		db.update("insert into a (id,expected,unspecified) values (1,'e1','u1')");
		db.update("insert into a (id,expected,unspecified) values (2,'e2','u2')");
	}
}
