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
public class AutoStringCutTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeAll
	public static void beforeClass() {
		db.execute("create table cut1prefix (char_type char(19), varchar_type varchar(22))");
		db.execute("create table leave1prefix (char_type char(11), varchar_type varchar(11))");
		db.execute("create table leave3number (char_type char(3), varchar_type varchar(3))");
		ApiTestSupport.reInitialize();
	}

	@AfterAll
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
		assertEquals(charValue, values.get(0).get("char_type"), table + " char_type");
		assertEquals(varcharValue, values.get(0).get("varchar_type"), table + " varchar_type");
	}

}
