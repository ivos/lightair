package it.setup.annotation;

import static org.junit.Assert.*;
import it.common.CommonTestBase;

import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public class SetupTestBase extends CommonTestBase {

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table person(name varchar(255))");
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table person");
	}

	protected void verifyPersons(int size) {
		assertEquals("Count", size,
				db.queryForInt("select count(*) from person"));
		List<Map<String, Object>> values = db
				.queryForList("select * from person");
		assertEquals("1. name", "Joe", values.get(0).get("name"));
		assertEquals("2. name", "Jane", values.get(1).get("name"));
		if (size > 2) {
			assertEquals("3. name", "Sue", values.get(2).get("name"));
		}
	}

}
