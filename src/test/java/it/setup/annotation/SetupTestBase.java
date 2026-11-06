package it.setup.annotation;

import it.common.CommonTestBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import test.support.ApiTestSupport;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SetupTestBase extends CommonTestBase {

	@BeforeAll
	@org.testng.annotations.BeforeClass
	public static void beforeClass() {
		db.execute("create table person(name varchar(255))");
		ApiTestSupport.reInitialize();
	}

	@AfterAll
	@org.testng.annotations.AfterClass
	public static void afterClass() {
		db.execute("drop table person");
	}

	protected void verifyPersons(String... names) {
		assertEquals(names.length,
			db.queryForObject("select count(*) from person", Integer.class).intValue(), "Count");
		List<Map<String, Object>> values = db
			.queryForList("select * from person");
		for (int i = 0; i < names.length; i++) {
			assertEquals(names[i], values.get(i).get("name"), i + ". name");
		}
	}
}
