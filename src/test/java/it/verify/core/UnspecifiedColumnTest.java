package it.verify.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(LightAir.class)
@Verify
public class UnspecifiedColumnTest extends CommonTestBase {

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a(id int primary key, expected varchar(255), "
				+ "unspecified varchar(255))");
	}

	@AfterClass
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
