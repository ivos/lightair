package it.setup.annotation;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import test.support.ConfigSupport;

@RunWith(LightAir.class)
@Setup.List({
		@Setup("profiles-defaulth2.xml"),
		@Setup(value = "profiles-hsqldb.xml", profile = "hsqldb"),
		@Setup(value = { "profiles-derby1.xml", "profiles-derby2.xml" }, profile = "derby") })
public class ProfilesTest {

	static JdbcTemplate connect(String url, String username, String password) {
		DataSource dataSource = new SingleConnectionDataSource(url, username,
				password, false);
		return new JdbcTemplate(dataSource);
	}

	static JdbcTemplate dbDefaultH2;
	static JdbcTemplate dbHsqldb;
	static JdbcTemplate dbDerby;
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

	@Test
	public void profiles() {
		verifyProfile(dbDefaultH2, "defaultPerson", "defaultName", 1, 0, "Joe");
		verifyProfile(dbHsqldb, "hsqldbPerson", "hsqldbName", 1, 0, "Jane");
		verifyProfile(dbDerby, "derbyPerson", "derbyName", 2, 0, "Jake");
		verifyProfile(dbDerby, "derbyPerson", "derbyName", 2, 1, "Hank");
	}

	private void verifyProfile(JdbcTemplate db, String tableName,
			String columnName, int size, int index, String value) {
		values = db.queryForList("select * from " + tableName);
		assertEquals("Size of " + tableName, size, values.size());
		assertEquals("Value of " + columnName, value,
				values.get(index).get(columnName));
	}

}
