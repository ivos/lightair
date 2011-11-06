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
public class UnspecifiedTableTest extends CommonTestBase {

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table expected(id int primary key, a1 varchar(255))");
		db.execute("create table unspecified(id int primary key, b1 varchar(255))");
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table expected");
		db.execute("drop table unspecified");
	}

	@Test
	public void test() {
		db.execute("delete from expected");
		db.update("insert into expected (id,a1) values (0,'a01')");
		db.update("insert into expected (id,a1) values (1,'a11')");
		db.update("insert into expected (id,a1) values (2,'a21')");
		db.execute("delete from unspecified");
		db.update("insert into unspecified (id,b1) values (0,'b01')");
		db.update("insert into unspecified (id,b1) values (1,'b11')");
		db.update("insert into unspecified (id,b1) values (2,'b21')");
	}

}
