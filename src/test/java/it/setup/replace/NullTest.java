package it.setup.replace;

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
public class NullTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a (id int primary key, a1 varchar(50))");
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a");
	}

	@Test
	public void test() {
		assertEquals("Count", 3, db.queryForInt("select count(*) from a"));
		values = db.queryForList("select * from a");
		verifyRow(0, "01");
		verifyRow(1, null);
		verifyRow(2, "21");
	}

	private void verifyRow(int id, String a1) {
		assertEquals("id " + id, id, values.get(id).get("id"));
		assertEquals("a1 " + id, a1, values.get(id).get("a1"));
	}

}
