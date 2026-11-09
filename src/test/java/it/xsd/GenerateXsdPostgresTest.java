package it.xsd;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.testcontainers.postgresql.PostgreSQLContainer;
import test.support.PostgresUtils;

import java.io.IOException;

public class GenerateXsdPostgresTest extends GenerateXsdTestBase {

	private static final boolean replaceTemplates = false;

	private static final String DB = "postgres";
	private static final String PROPERTIES_FILE_NAME = "target/test-classes/it/xsd/light-air-" + DB + ".properties";

	private static PostgreSQLContainer postgres;

	static {
		SingleConnectionDataSource dataSource = new SingleConnectionDataSource(
			"jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres", true);
		db = new JdbcTemplate(dataSource);
	}

	public GenerateXsdPostgresTest() {
		super(replaceTemplates);
	}

	@BeforeAll
	public static void beforeClass() {
		postgres = PostgresUtils.initPostgres();
		createTables();
	}

	@AfterAll
	public static void afterClass() {
		dropTables();
		PostgresUtils.closePostgresIfOpen(postgres);
		postgres = null;
	}

	@Test
	public void test() throws IOException {
		perform(DB, PROPERTIES_FILE_NAME);
	}
}
