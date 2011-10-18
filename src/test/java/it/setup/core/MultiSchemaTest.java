package it.setup.core;

import static org.junit.Assert.*;
import it.common.CommonTestBase;

import java.util.List;
import java.util.Map;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(LightAir.class)
@Setup
public class MultiSchemaTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeClass
	public static void beforeClass() {
		db.execute("create schema s1 authorization sa");
		db.execute("create schema s2 authorization sa");
		db.execute("create schema s3 authorization sa");
		db.execute("create table s1.ts1 (id int primary key, a varchar(50))");
		db.execute("create table s2.ts2 (id int primary key, a varchar(50))");
		db.execute("create table s3.ts3 (id int primary key, a varchar(50))");
		db.execute("create table tds (id int primary key, a varchar(50))");
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table s1.ts1");
		db.execute("drop table s2.ts2");
		db.execute("drop table s3.ts3");
		db.execute("drop table tds");
		db.execute("drop schema s1");
		db.execute("drop schema s2");
		db.execute("drop schema s3");
	}

	@Test
	public void tableRowsMixed() {
		verifyTable("s1.ts1", "1");
		verifyTable("s2.ts2", "2");
		verifyTable("s3.ts3", "3");
		verifyTable("tds", "d");
	}

	private void verifyTable(String tableName, String schemaCode) {
		int count = 3;
		assertEquals("Count", count,
				db.queryForInt("select count(*) from " + tableName));
		values = db.queryForList("select * from " + tableName);
		for (int i = 0; i < count; i++) {
			verifyRow(i, "a" + schemaCode + i);
		}
	}

	private void verifyRow(int id, String value) {
		assertEquals("id " + id, id, values.get(id).get("id"));
		assertEquals("a " + id, value, values.get(id).get("a"));
	}

}
