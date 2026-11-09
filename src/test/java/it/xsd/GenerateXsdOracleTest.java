package it.xsd;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.testcontainers.oracle.OracleContainer;
import test.support.OracleUtils;

import java.io.IOException;

public class GenerateXsdOracleTest extends GenerateXsdTestBase {

	private static final boolean replaceTemplates = false;

	private static final String DB = "oracle";
	private static final String PROPERTIES_FILE_NAME = "target/test-classes/it/xsd/light-air-" + DB + ".properties";

	private static OracleContainer oracle;

	static {
		SingleConnectionDataSource dataSource = new SingleConnectionDataSource(
			"jdbc:oracle:thin:@localhost:1521/freepdb1", "test", "test", true);
		db = new JdbcTemplate(dataSource);
	}

	public GenerateXsdOracleTest() {
		super(replaceTemplates);
	}

	@BeforeAll
	public static void beforeClass() {
		oracle = OracleUtils.initOracle();
		createTables();
	}

	@AfterAll
	public static void afterClass() {
		dropTables();
		OracleUtils.closeOracleIfOpen(oracle);
		oracle = null;
	}

	@Test
	public void test() throws IOException {
		perform(DB, PROPERTIES_FILE_NAME);
	}
}
