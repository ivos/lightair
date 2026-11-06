package it.xsd;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.io.IOException;

/**
 * There must be a custom user in Oracle to make this test pass.
 * The SYSTEM user is reading system tables that pollute the resulting XSD.
 * As soon as I figure out how to connect to a newly created user in Oracle 18.3, I will finish this test...
 */
@Disabled
public class GenerateXsdOracleTest extends GenerateXsdTestBase {

	private static boolean replaceTemplates = false;

	private static final String DB = "oracle";
	private static final String PROPERTIES_FILE_NAME = "target/test-classes/it/xsd/light-air-" + DB + ".properties";

	static {
		SingleConnectionDataSource dataSource = new SingleConnectionDataSource(
			"jdbc:oracle:thin:@localhost:1521:xe", "SYSTEM", "password", true);
		db = new JdbcTemplate(dataSource);
	}

	public GenerateXsdOracleTest() {
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
