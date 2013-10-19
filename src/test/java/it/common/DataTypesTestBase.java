package it.common;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import net.sf.lightair.internal.factory.Factory;
import net.sf.lightair.internal.properties.PropertiesProvider;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class DataTypesTestBase {

	public static JdbcTemplate db;

	public List<Map<String, Object>> values;

	@BeforeClass
	public static void initDataTypesTestBase() {
		Factory.getInstance().init();
		propertiesProvider = Factory.getInstance().getPropertiesProvider();
	}

	@AfterClass
	public static void afterClass() {
		dropTable();
		restoreConfig();
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
	}

	public static void dropTable() {
		db.execute("drop table data_types");
	}

	static PropertiesProvider propertiesProvider;

	public static void replaceConfig(String dbName) {
		propertiesProvider.setPropertiesFileName("light-air-" + dbName
				+ ".properties");
		Factory.getInstance().init();
	}

	public static void restoreConfig() {
		propertiesProvider
				.setPropertiesFileName(PropertiesProvider.DEFAULT_PROPERTIES_FILE_NAME);
		Factory.getInstance().init();
	}

}
