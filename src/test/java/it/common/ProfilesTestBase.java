package it.common;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import test.support.ConfigSupport;

public class ProfilesTestBase {

	static JdbcTemplate connect(String url, String username, String password) {
		DataSource dataSource = new SingleConnectionDataSource(url, username,
				password, false);
		return new JdbcTemplate(dataSource);
	}

	public static JdbcTemplate dbDefaultH2;
	public static JdbcTemplate dbHsqldb;
	public static JdbcTemplate dbDerby;
	static {
		dbDefaultH2 = connect("jdbc:h2:mem:test", "sa", "");
		dbHsqldb = connect("jdbc:hsqldb:mem:test", "sa", "");
		dbDerby = connect("jdbc:derby:memory:test;create=true", "root", "root");
	}

	@BeforeClass
	public static void beforeClass() {
		dbDefaultH2
				.execute("create table defaultPerson (defaultName varchar(50))");
		dbHsqldb.execute("create table hsqldbPerson (hsqldbName varchar(50))");
		dbDerby.execute("create table derbyPerson (derbyName varchar(50))");
		ConfigSupport.init();
		ConfigSupport.replaceConfig("profiles");
	}

	@AfterClass
	public static void afterClass() {
		dbDefaultH2.execute("drop table defaultPerson");
		dbHsqldb.execute("drop table hsqldbPerson");
		dbDerby.execute("drop table derbyPerson");
		ConfigSupport.restoreConfig();
	}

	public List<Map<String, Object>> values;

	public void fill(String hank) {
		dbDefaultH2.execute("delete from defaultPerson");
		dbHsqldb.execute("delete from hsqldbPerson");
		dbDerby.execute("delete from derbyPerson");

		dbDefaultH2
				.update("insert into defaultPerson (defaultName) values ('Joe')");
		dbHsqldb.update("insert into hsqldbPerson (hsqldbName) values ('Jane')");
		dbDerby.update("insert into derbyPerson (derbyName) values ('Jake')");
		dbDerby.update("insert into derbyPerson (derbyName) values ('" + hank
				+ "')");
	}

}
