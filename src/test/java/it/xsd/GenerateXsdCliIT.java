package it.xsd;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import test.support.PostgresUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GenerateXsdCliIT extends GenerateXsdTestBase {

	private static final boolean replaceTemplates = false;

	private static final String DB = "postgres";
	private static final String PROPERTIES_FILE_NAME = "target/test-classes/it/xsd/light-air-" + DB + ".properties";

	private static EmbeddedPostgres postgres;

	static {
		SingleConnectionDataSource dataSource = new SingleConnectionDataSource(
				"jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres", true);
		db = new JdbcTemplate(dataSource);
	}

	public GenerateXsdCliIT() {
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
	public void test() throws IOException, InterruptedException {
		FileUtils.deleteDirectory(new File(GENERATED_DIR));

		String fatJarPath = Paths.get("target", "light-air-fat.jar").toAbsolutePath().toString();
		assertTrue("The fat JAR was not created or found.", new File(fatJarPath).exists());
		ProcessBuilder processBuilder = new ProcessBuilder(
				"java", "-jar", fatJarPath,
				"generateXsd", "-p", PROPERTIES_FILE_NAME
		);
		Process process = processBuilder.start();
		int exitCode = process.waitFor();
		if (exitCode != 0) {
			String output = new BufferedReader(new InputStreamReader(process.getInputStream()))
					.lines()
					.collect(Collectors.joining("\n"));
			System.out.println(output);
			String errorOutput = new BufferedReader(new InputStreamReader(process.getErrorStream()))
					.lines()
					.collect(Collectors.joining("\n"));
			System.err.println(errorOutput);
		}
		assertEquals("The process did not exit successfully.", 0, exitCode);

		performTest("dataset-" + DB + ".xsd", "dataset.xsd");
	}
}
