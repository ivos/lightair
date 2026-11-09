package it.setup.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.support.ApiTestSupport;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(LightAir.class)
@Setup
public class ReservedSqlWordsTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeAll
	public static void beforeClass() {
		db.execute("create table \"SELECT\" (id int primary key," +
			" \"VARCHAR\" varchar(50), \"INTEGER\" integer, \"DATE\" date, \"ORDER\" varchar(50))");
		ApiTestSupport.reInitialize();
	}

	@AfterAll
	public static void afterClass() {
		db.execute("drop table \"SELECT\"");
	}

	@Disabled // TODO resolve
	@Test
	public void test() {
		assertEquals(Integer.valueOf(1), db.queryForObject("select count(*) from \"SELECT\"", Integer.class), "Count");
		values = db.queryForList("select * from \"SELECT\"");
		verifyRow(0, "abcdefghijklmnopqrstuvxyz", 12345678, LocalDate.of(2999, 12, 31), "order1");
	}

	private void verifyRow(int id, String varchar, int integer, LocalDate date, String order) {
		assertEquals(id, values.get(id).get("id"), "id " + id);
		assertEquals(varchar, values.get(id).get("VARCHAR"), "VARCHAR " + id);
		assertEquals(integer, values.get(id).get("INTEGER"), "INTEGER " + id);
		assertEquals(date, values.get(id).get("DATE"), "DATE " + id);
		assertEquals(order, values.get(id).get("ORDER"), "ORDER " + id);
	}
}
