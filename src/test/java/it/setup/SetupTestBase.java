package it.setup;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import net.sf.lightair.LightAir;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

@RunWith(LightAir.class)
public class SetupTestBase {

	protected final static JdbcTemplate db;

	static {
		SingleConnectionDataSource dataSource = new SingleConnectionDataSource(
				"jdbc:h2:mem:test", "sa", "", true);
		db = new JdbcTemplate(dataSource);
	}

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
