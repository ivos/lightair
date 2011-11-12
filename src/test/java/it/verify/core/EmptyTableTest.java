package it.verify.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Table expected empty is empty in database.
 */
@RunWith(LightAir.class)
@Verify
public class EmptyTableTest extends CommonTestBase {

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table filled(id int primary key, a1 varchar(255))");
		db.execute("create table empty_(id int primary key, b1 varchar(255))");
	}

	@AfterClass
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
	}

}
