package it.verify.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.support.ApiTestSupport;

@RunWith(LightAir.class)
@Verify
public class MultiSchemaTest extends CommonTestBase {

	@BeforeClass
	public static void beforeClass() {
		db.execute("create schema s1 authorization sa");
		db.execute("create schema s2 authorization sa");
		db.execute("create schema s3 authorization sa");
		db.execute("create table s1.ts1 (id int primary key, a varchar(50))");
		db.execute("create table s2.ts2 (id int primary key, a varchar(50))");
		db.execute("create table s3.ts3 (id int primary key, a varchar(50))");
		db.execute("create table tds (id int primary key, a varchar(50))");
		ApiTestSupport.reInitialize();
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table s1.ts1");
		db.execute("drop table s2.ts2");
		db.execute("drop table s3.ts3");
		db.execute("drop table tds");
		db.execute("drop schema s1");
		db.execute("drop schema s2");
		db.execute("drop schema s3");
	}

	@Test
	public void test() {
		db.execute("delete from s1.ts1");
		db.execute("delete from s2.ts2");
		db.execute("delete from s3.ts3");
		db.execute("delete from tds");

		db.update("insert into s1.ts1 (id,a) values (0,'a10')");
		db.update("insert into s1.ts1 (id,a) values (1,'a11')");
		db.update("insert into s1.ts1 (id,a) values (2,'a12')");

		db.update("insert into s2.ts2 (id,a) values (0,'a20')");
		db.update("insert into s2.ts2 (id,a) values (1,'a21')");
		db.update("insert into s2.ts2 (id,a) values (2,'a22')");

		db.update("insert into s3.ts3 (id,a) values (0,'a30')");
		db.update("insert into s3.ts3 (id,a) values (1,'a31')");
		db.update("insert into s3.ts3 (id,a) values (2,'a32')");

		db.update("insert into tds (id,a) values (0,'ad0')");
		db.update("insert into tds (id,a) values (1,'ad1')");
		db.update("insert into tds (id,a) values (2,'ad2')");
	}

}
