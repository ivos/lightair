package it.xsd;

import net.sf.lightair.Api;
import net.sf.seaf.test.util.TemplatingTestBase;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.io.File;
import java.io.IOException;

public class GenerateXsdHsqlTest extends TemplatingTestBase {

	private static boolean replaceTemplates = false;

	private static final String DB = "hsql";
	private static final String GENERATED_DIR = "target/generated-xsd/light-air-xsd/";
	private static final String PROPERTIES_FILE_NAME = "target/test-classes/it/xsd/light-air-" + DB + ".properties";

	private final static JdbcTemplate db;

	static {
		SingleConnectionDataSource dataSource = new SingleConnectionDataSource(
				"jdbc:hsqldb:mem:test", "sa", "", true);
		db = new JdbcTemplate(dataSource);
	}

	public GenerateXsdHsqlTest() {
		super(replaceTemplates, "src/test/java/it/xsd/", GENERATED_DIR);
	}

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a1 (id int primary key, a1a varchar(20), a1b varchar(50) not null, a1c integer)");
		db.execute("create table a2 (id int primary key, a2a varchar(20), a2b varchar(50) not null, a2c integer)");
		db.execute("create table a3 (id int primary key, a3a varchar(20), a3b varchar(50) not null, a3c integer)");
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a1");
		db.execute("drop table a2");
		db.execute("drop table a3");
	}

	@Test
	public void test() throws IOException {
		FileUtils.deleteDirectory(new File(GENERATED_DIR));
		Api.initialize(PROPERTIES_FILE_NAME);
		try {
			Api.generateXsd();
		} finally {
			Api.shutdown();
		}

		performTest("dataset-" + DB + ".xsd", "dataset.xsd");
	}
}
