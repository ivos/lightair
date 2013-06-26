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
public class DeleteAllTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a (id int primary key, a1 varchar(50))");
		db.execute("insert into a (id, a1) values (10, 'deleted10')");
		db.execute("insert into a (id, a1) values (11, 'deleted11')");
		db.execute("insert into a (id, a1) values (12, 'deleted12')");
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
		verifyRow(0, "a10");
		verifyRow(1, "a11");
	}

	private void verifyRow(int id, String value) {
		assertEquals("id " + id, id, values.get(id).get("id"));
		assertEquals("a1 " + id, value, values.get(id).get("a1"));
	}

}
