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
public class DefaultTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeAll
	public static void beforeClass() {
		db.execute("create table a (id int primary key,"
			+ " fixedm char(20) default 'D' not null, autom char(20) default 'D' not null,"
			+ " fixedo char(20) default 'D', autoo char(20) default 'D')");
		ApiTestSupport.reInitialize();
	}

	@AfterAll
	public static void afterClass() {
		db.execute("drop table a");
	}

	@Test
	public void test() {
		assertEquals(Integer.valueOf(2), db.queryForObject("select count(*) from a", Integer.class), "Count");

		values = db.queryForList("select * from a");
		verifyRow(0, 1, "A                   ", "autom 1042168101    ",
			"A                   ", "autoo 1042128301    ");
		verifyRow(1, 2, "D                   ", "D                   ",
			"D                   ", "D                   ");
	}

	protected void verifyRow(int row, int id, String fixedm, String autom,
		String fixedo, String autoo
	) {
		assertEquals(id, values.get(row).get("id"), "id " + row);
		assertEquals(fixedm, values.get(row).get("fixedm"), "fixedm " + row);
		assertEquals(autom, values.get(row).get("autom"), "autom " + row);
		assertEquals(fixedo, values.get(row).get("fixedo"), "fixedo " + row);
		assertEquals(autoo, values.get(row).get("autoo"), "autoo " + row);
	}
}
