package it.xsd;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
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

	@BeforeClass
	public static void beforeClass() {
		createTables();
	}

	@AfterClass
	public static void afterClass() {
		dropTables();
	}

	@Test
	public void test() throws IOException {
		perform(DB, PROPERTIES_FILE_NAME);
	}
}
