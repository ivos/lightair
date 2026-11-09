package it.setup.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.support.ApiTestSupport;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(LightAir.class)
@Setup
public class MultipleTablesTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeAll
	public static void beforeClass() {
		db.execute("create table a (id int primary key, a1 varchar(50))");
		db.execute("create table b (id int primary key, b1 varchar(50))");
		db.execute("create table c (id int primary key, c1 varchar(50))");
		ApiTestSupport.reInitialize();
	}

	@AfterAll
	public static void afterClass() {
		db.execute("drop table a");
		db.execute("drop table b");
		db.execute("drop table c");
	}

	@Test
	public void tableRowsTogether() {
		verify();
	}

	@Test
	public void tableRowsMixed() {
		verify();
	}

	private void verify() {
		verifyTable("a");
		verifyTable("b");
		verifyTable("c");
	}

	private void verifyTable(String tableName) {
		Integer count = 3;
		assertEquals(count, db.queryForObject("select count(*) from " + tableName, Integer.class), "Count");
		values = db.queryForList("select * from " + tableName);
		for (int i = 0; i < count; i++) {
			verifyRow(i, tableName + "1", tableName + "1" + i);
		}
	}

	private void verifyRow(int id, String column, String value) {
		assertEquals(id, values.get(id).get("id"), "id " + id);
		assertEquals(value, values.get(id).get(column), column + " " + id);
	}
}
