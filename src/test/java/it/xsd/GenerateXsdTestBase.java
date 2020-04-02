package it.xsd;

import net.sf.lightair.Api;
import net.sf.seaf.test.util.TemplatingTestBase;
import org.apache.commons.io.FileUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.IOException;

public class GenerateXsdTestBase extends TemplatingTestBase {

	private static final String GENERATED_DIR = "target/generated-xsd/light-air-xsd/";

	protected static JdbcTemplate db;

	public GenerateXsdTestBase(boolean replaceTemplates) {
		super(replaceTemplates, "src/test/java/it/xsd/", GENERATED_DIR);
	}

	public static void createTables() {
		db.execute("create table a1 (id int primary key, a1a varchar(20), a1b varchar(50) not null, a1c integer)");
		db.execute("create table a2 (id int primary key, a2a varchar(20), a2b varchar(50) not null, a2c integer)");
		db.execute("create table a3 (id int primary key, a3a varchar(20), a3b varchar(50) not null, a3c integer)");
	}

	public static void dropTables() {
		db.execute("drop table a1");
		db.execute("drop table a2");
		db.execute("drop table a3");
	}

	protected void perform(String db, String propertiesFileName) throws IOException {
		FileUtils.deleteDirectory(new File(GENERATED_DIR));
		Api.initialize(propertiesFileName);
		try {
			Api.generateXsd();
		} finally {
			Api.shutdown();
		}

		performTest("dataset-" + db + ".xsd", "dataset.xsd");
	}
}
