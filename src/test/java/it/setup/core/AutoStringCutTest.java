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
public class AutoStringCutTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table cut1prefix (char_type char(16), varchar_type varchar(19))");
		db.execute("create table leave1prefix (char_type char(8), varchar_type varchar(8))");
		db.execute("create table leave3number (char_type char(3), varchar_type varchar(3))");
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table cut1prefix");
		db.execute("drop table leave1prefix");
		db.execute("drop table leave3number");
	}

	@Test
	public void test() {
		perform("cut1prefix", "char_type3356900", "varchar_type3354100");
		perform("leave1prefix", "c9706900", "v9704100");
		perform("leave3number", "900", "100");
	}

	private void perform(String table, String charValue, String varcharValue) {
		values = db.queryForList("select * from " + table);
		assertEquals(table + " char_type", charValue,
				values.get(0).get("char_type"));
		assertEquals(table + " varchar_type", varcharValue,
				values.get(0).get("varchar_type"));
	}

}
