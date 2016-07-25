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
public class DefaultTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a (id int primary key,"
				+ " fixedm char(20) default 'D' not null, autom char(20) default 'D' not null,"
				+ " fixedo char(20) default 'D', autoo char(20) default 'D')");
		ApiTestSupport.reInitialize();
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a");
	}

	@Test
	public void test() {
		assertEquals("Count", new Integer(2),
				db.queryForObject("select count(*) from a", Integer.class));

		values = db.queryForList("select * from a");
		verifyRow(0, 1, "A", "autom 1042168101", "A", "autoo 1042128301");
		verifyRow(1, 2, "D", "D", "D", "D");
	}

	protected void verifyRow(int row, int id, String fixedm, String autom,
			String fixedo, String autoo) {
		assertEquals("id " + row, id, values.get(row).get("id"));
		assertEquals("fixedm " + row, fixedm, values.get(row).get("fixedm"));
		assertEquals("autom " + row, autom, values.get(row).get("autom"));
		assertEquals("fixedo " + row, fixedo, values.get(row).get("fixedo"));
		assertEquals("autoo " + row, autoo, values.get(row).get("autoo"));
	}

}
