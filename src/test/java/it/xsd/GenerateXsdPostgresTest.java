package it.xsd;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import test.support.PostgresUtils;

import java.io.IOException;

public class GenerateXsdPostgresTest extends GenerateXsdTestBase {

	private static boolean replaceTemplates = false;

	private static final String DB = "postgres";
	private static final String PROPERTIES_FILE_NAME = "target/test-classes/it/xsd/light-air-" + DB + ".properties";

	private static EmbeddedPostgres postgres;

	static {
		SingleConnectionDataSource dataSource = new SingleConnectionDataSource(
				"jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres", true);
		db = new JdbcTemplate(dataSource);
	}

	public GenerateXsdPostgresTest() {
		super(replaceTemplates);
	}

	@BeforeClass
	public static void beforeClass() {
		postgres = PostgresUtils.initPostgres();
		createTables();
	}

	@AfterClass
	public static void afterClass() {
		dropTables();
		PostgresUtils.closePostgresIfOpen(postgres);
	}

	@Test
	public void test() throws IOException {
		perform(DB, PROPERTIES_FILE_NAME);
	}
}
