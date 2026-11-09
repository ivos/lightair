package it.xsd;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.io.IOException;

public class GenerateXsdHsqlTest extends GenerateXsdTestBase {

	private static boolean replaceTemplates = false;

	private static final String DB = "hsql";
	private static final String PROPERTIES_FILE_NAME = "target/test-classes/it/xsd/light-air-" + DB + ".properties";

	static {
		SingleConnectionDataSource dataSource = new SingleConnectionDataSource(
			"jdbc:hsqldb:mem:test", "sa", "", true);
		db = new JdbcTemplate(dataSource);
	}

	public GenerateXsdHsqlTest() {
		super(replaceTemplates);
	}

	@BeforeAll
	public static void beforeClass() {
		createTables();
	}

	@AfterAll
	public static void afterClass() {
		dropTables();
	}

	@Test
	public void test() throws IOException {
		perform(DB, PROPERTIES_FILE_NAME);
	}
}
