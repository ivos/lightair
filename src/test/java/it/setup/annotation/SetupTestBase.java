package it.setup.annotation;

import it.common.CommonTestBase;
import net.sf.lightair.internal.Api;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SetupTestBase extends CommonTestBase {

	@BeforeClass
	@org.testng.annotations.BeforeClass
	public static void beforeClass() {
		db.execute("create table person(name varchar(255))");
		Api.reInitialize();
	}

	@AfterClass
	@org.testng.annotations.AfterClass
	public static void afterClass() {
		db.execute("drop table person");
	}

	protected void verifyPersons(String... names) {
		assertEquals("Count", names.length,
				db.queryForObject("select count(*) from person", Integer.class)
						.intValue());
		List<Map<String, Object>> values = db
				.queryForList("select * from person");
		for (int i = 0; i < names.length; i++) {
			assertEquals("" + i + ". name", names[i], values.get(i).get("name"));
		}
	}

}
