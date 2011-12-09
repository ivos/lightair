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
public class VariableTest extends CommonTestBase {

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a(id int primary key, a1 varchar(255))");
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a");
	}

	@Test
	public void test() {
		db.execute("delete from a");
		db.update("insert into a (id,a1) values (0,'value1')");
		db.update("insert into a (id,a1) values (1,'value1')");
	}

	@Test
	public void variablesAreClearedBetweenTests() {
		db.execute("delete from a");
		db.update("insert into a (id,a1) values (0,'value2')");
		db.update("insert into a (id,a1) values (1,'value2')");
	}

}
