package it.xsd;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.io.IOException;

/**
 * There must be a custom user in Oracle to make this test pass.
 * The SYSTEM user is reading system tables that pollute the resulting XSD.
 * As soon as I figure out how to connect to a newly created user in Oracle 18.3, I will finish this test...
 */
@Ignore
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
