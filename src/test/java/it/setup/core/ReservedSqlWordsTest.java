package it.setup.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.support.ApiTestSupport;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(LightAir.class)
@Setup
public class ReservedSqlWordsTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table \"SELECT\" (id int primary key," +
				" \"VARCHAR\" varchar(50), \"INTEGER\" integer, \"DATE\" date, \"ORDER\" varchar(50))");
		ApiTestSupport.reInitialize();
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table \"SELECT\"");
	}

	@Ignore // TODO resolve
	@Test
	public void test() {
		assertEquals("Count", Integer.valueOf(1), db.queryForObject("select count(*) from \"SELECT\"", Integer.class));
		values = db.queryForList("select * from \"SELECT\"");
		verifyRow(0, "abcdefghijklmnopqrstuvxyz", 12345678, LocalDate.of(2999, 12, 31), "order1");
	}

	private void verifyRow(int id, String varchar, int integer, LocalDate date, String order) {
		assertEquals("id " + id, id, values.get(id).get("id"));
		assertEquals("VARCHAR " + id, varchar, values.get(id).get("VARCHAR"));
		assertEquals("INTEGER " + id, integer, values.get(id).get("INTEGER"));
		assertEquals("DATE " + id, date, values.get(id).get("DATE"));
		assertEquals("ORDER " + id, order, values.get(id).get("ORDER"));
	}
}
