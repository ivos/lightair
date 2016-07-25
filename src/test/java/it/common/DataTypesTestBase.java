package it.common;

import net.sf.lightair.internal.factory.Factory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import test.support.ApiTestSupport;
import test.support.ConfigSupport;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class DataTypesTestBase {

	public static JdbcTemplate db;

	public List<Map<String, Object>> values;

	@BeforeClass
	public static void initDataTypesTestBase() {
		Factory.getInstance().init();
		ConfigSupport.init();
	}

	@AfterClass
	public static void afterClass() {
		dropTable();
		ConfigSupport.restoreConfig();
	}

	public static void connect(String url, String username, String password) {
		DataSource dataSource = new SingleConnectionDataSource(url, username,
				password, false);
		db = new JdbcTemplate(dataSource);
	}

	public static void createTable() {
		db.execute("create table data_types (id int primary key, char_type char(20), "
				+ "varchar_type varchar(50), integer_type integer, "
				+ "date_type date, time_type time, timestamp_type timestamp, "
				+ "double_type double, boolean_type boolean, bigint_type bigint, "
				+ "decimal_type decimal(20,2), clob_type clob, blob_type blob, binary_type binary(8))");
		ApiTestSupport.reInitialize();
	}

	public static void dropTable() {
		db.execute("drop table data_types");
	}

}
