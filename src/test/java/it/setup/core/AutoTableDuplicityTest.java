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
public class AutoTableDuplicityTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table adgb (a1 char(20))");
		db.execute("create table adkn (a1 char(20))");
		db.execute("create table adqk (a1 char(20))");
		db.execute("create table aeng (a1 char(20))");
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table adgb");
		db.execute("drop table adkn");
		db.execute("drop table adqk");
		db.execute("drop table aeng");
	}

	@Test
	public void test() {
		perform("Standard", "adgb", "998");
		perform("Same hash", "adkn", "999");
		perform("Increment to 000", "adqk", "000");
		perform("Increment to 001", "aeng", "001");
	}

	private void perform(String test, String table, String tableHash) {
		values = db.queryForList("select * from " + table);
		verifyValue(test, table, tableHash, 0);
		verifyValue(test, table, tableHash, 1);
	}

	private void verifyValue(String test, String table, String tableHash, int id) {
		assertEquals(test + " " + id, "a1 " + tableHash + "730" + id, values
				.get(id).get("a1"));
	}

}
