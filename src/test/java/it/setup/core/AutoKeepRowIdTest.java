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
public class AutoKeepRowIdTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a (id int primary key, "
				+ "startsAuto varchar(20), startsManual varchar(20))");
		db.execute("create table b (id int primary key, "
				+ "startsAuto varchar(20), startsManual varchar(20))");
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a");
		db.execute("drop table b");
	}

	@Test
	public void test() {
		verifyTable("a", "421");
		verifyTable("b", "241");
	}

	private void verifyTable(String table, String prefix) {
		values = db.queryForList("select * from " + table);
		verifyRow(table, 0, 1, "startsauto " + prefix + "7500", "sm1");
		verifyRow(table, 1, 2, "sa2", "startsmanual " + prefix + "9501");
		verifyRow(table, 2, 3, "sa3", "sm3");
		verifyRow(table, 3, 4, "startsauto " + prefix + "7503", "startsmanual "
				+ prefix + "9503");
		assertEquals("Count " + table, new Integer(4), db.queryForObject(
				"select count(*) from " + table, Integer.class));
	}

	private void verifyRow(String table, int row, int id, String startsAuto,
			String startsManual) {
		assertEquals(table + " id, " + row, id, values.get(row).get("id"));
		assertEquals(table + " startsAuto, " + row, startsAuto, values.get(row)
				.get("startsAuto"));
		assertEquals(table + " startsManual, " + row, startsManual,
				values.get(row).get("startsManual"));
	}

}
