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
public class AutoKeepRowIdTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeAll
	public static void beforeClass() {
		db.execute("create table a (id int primary key, "
			+ "startsauto varchar(20), startsmanual varchar(20))");
		db.execute("create table b (id int primary key, "
			+ "startsauto varchar(20), startsmanual varchar(20))");
		ApiTestSupport.reInitialize();
	}

	@AfterAll
	public static void afterClass() {
		db.execute("drop table a");
		db.execute("drop table b");
	}

	@Test
	public void test() {
		verifyTable("a", "0421");
		verifyTable("b", "4241");
	}

	private void verifyTable(String table, String prefix) {
		values = db.queryForList("select * from " + table);
		verifyRow(table, 0, 1, "startsauto1" + prefix + "67501", "sm1");
		verifyRow(table, 1, 2, "sa2", "startsmanu1" + prefix + "39502");
		verifyRow(table, 2, 3, "sa3", "sm3");
		verifyRow(table, 3, 4, "startsauto1" + prefix + "67504",
			"startsmanu1" + prefix + "39504");
		assertEquals(Integer.valueOf(4),
			db.queryForObject("select count(*) from " + table, Integer.class),
			"Count " + table);
	}

	private void verifyRow(String table, int row, int id, String startsAuto,
		String startsManual) {
		assertEquals(id, values.get(row).get("id"), table + " id, " + row);
		assertEquals(startsAuto, values.get(row).get("startsAuto"), table + " startsAuto, " + row);
		assertEquals(startsManual, values.get(row).get("startsManual"), table + " startsManual, " + row);
	}
}
