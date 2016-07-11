package test.support;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;

public class DbTemplate {

	public JdbcTemplate db;

	public DbTemplate(String url, String username, String password) {
		db = connect(url, username, password);
	}

	private static JdbcTemplate connect(String url, String username, String password) {
		DataSource dataSource = new SingleConnectionDataSource(url, username, password, false);
		return new JdbcTemplate(dataSource);
	}
}
