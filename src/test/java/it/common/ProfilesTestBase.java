package it.common;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import test.support.ConfigSupport;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class ProfilesTestBase {

	static JdbcTemplate connect(String url, String username, String password) {
		DataSource dataSource = new SingleConnectionDataSource(url, username,
				password, false);
		return new JdbcTemplate(dataSource);
	}

	public static JdbcTemplate dbDefaultH2;
	public static JdbcTemplate dbHsqldb;
	public static JdbcTemplate dbCustom;
	static {
		dbDefaultH2 = connect("jdbc:h2:mem:test", "sa", "");
		dbHsqldb = connect("jdbc:hsqldb:mem:test", "sa", "");
		dbCustom = connect("jdbc:h2:mem:test", "sa", "");
	}

	@BeforeClass
	public static void beforeClass() {
		dbDefaultH2.execute("create table defaultPerson (defaultName varchar(50))");
		dbHsqldb.execute("create table hsqldbPerson (hsqldbName varchar(50))");
		dbDefaultH2.execute("create schema custom authorization sa");
		dbCustom.execute("create table custom.customPerson (customName varchar(50))");
		ConfigSupport.init();
		ConfigSupport.replaceConfig("profiles");
	}

	@AfterClass
	public static void afterClass() {
		dbDefaultH2.execute("drop table defaultPerson");
		dbHsqldb.execute("drop table hsqldbPerson");
		dbCustom.execute("drop table custom.customPerson");
		dbDefaultH2.execute("drop schema custom");
		ConfigSupport.restoreConfig();
	}

	public List<Map<String, Object>> values;

	public void fill(String hank) {
		dbDefaultH2.execute("delete from defaultPerson");
		dbHsqldb.execute("delete from hsqldbPerson");
		dbCustom.execute("delete from custom.customPerson");

		dbDefaultH2.update("insert into defaultPerson (defaultName) values ('Joe')");
		dbHsqldb.update("insert into hsqldbPerson (hsqldbName) values ('Jane')");
		dbCustom.update("insert into custom.customPerson (customName) values ('Jake')");
		dbCustom.update("insert into custom.customPerson (customName) values ('" + hank + "')");
	}

}
