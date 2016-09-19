package it.verify.annotation;

import it.common.CommonTestBase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import test.support.ApiTestSupport;

public class VerifyTestBase extends CommonTestBase {

	@BeforeClass
	@org.testng.annotations.BeforeClass
	public static void beforeClass() {
		db.execute("create table person(id int primary key, name varchar(255))");
		ApiTestSupport.reInitialize();
	}

	@AfterClass
	@org.testng.annotations.AfterClass
	public static void afterClass() {
		db.execute("drop table person");
	}

	protected void fillPersons(int size) {
		db.execute("delete from person");
		for (int i = 0; i < size; i++) {
			db.update("insert into person (id,name) values (" + i + ",'name"
					+ i + "')");
		}
	}

}
