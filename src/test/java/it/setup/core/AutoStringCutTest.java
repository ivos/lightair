package it.setup.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.support.ApiTestSupport;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(LightAir.class)
@Setup
public class AutoStringCutTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table cut1prefix (char_type char(19), varchar_type varchar(22))");
		db.execute("create table leave1prefix (char_type char(11), varchar_type varchar(11))");
		db.execute("create table leave3number (char_type char(3), varchar_type varchar(3))");
		ApiTestSupport.reInitialize();
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table cut1prefix");
		db.execute("drop table leave1prefix");
		db.execute("drop table leave3number");
	}

	@Test
	public void test() {
		perform("cut1prefix", "char_type1733556901", "varchar_type1733584101");
		perform("leave1prefix", "c1197056901", "v1197084101");
		perform("leave3number", "901", "101");
	}

	private void perform(String table, String charValue, String varcharValue) {
		values = db.queryForList("select * from " + table);
		assertEquals(table + " char_type", charValue, values.get(0).get("char_type"));
		assertEquals(table + " varchar_type", varcharValue, values.get(0).get("varchar_type"));
	}

}
