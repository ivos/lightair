package it.verify.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Rows in verification dataset not ordered by primary key neither by insert
 * sequence.
 */
@RunWith(LightAir.class)
@Verify
public class RowOrderTest extends CommonTestBase {

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
		db.update("insert into a (id,a1) values (2,'21')");
		db.update("insert into a (id,a1) values (0,'01')");
		db.update("insert into a (id,a1) values (1,'11')");
	}

}
