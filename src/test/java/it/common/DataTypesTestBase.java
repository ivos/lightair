package it.common;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import net.sf.lightair.internal.junit.util.Factory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import test.support.ConfigSupport;
import test.support.PostgresUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class DataTypesTestBase {

	public static JdbcTemplate db;

	public List<Map<String, Object>> values;

	@BeforeClass
	public static void initDataTypesTestBase() {
		Factory.getInstance().init();
	}

	@AfterClass
	public static void afterClass() {
		dropTable();
		ConfigSupport.restoreConfig();
		PostgresUtils.closePostgresIfOpen(postgres);
	}

	public static void connect(String url, String username, String password) {
		DataSource dataSource = new SingleConnectionDataSource(url, username, password, false);
		db = new JdbcTemplate(dataSource);
	}

	public static void dropTable() {
		db.execute("drop table data_types");
	}

	protected static EmbeddedPostgres postgres;
}
