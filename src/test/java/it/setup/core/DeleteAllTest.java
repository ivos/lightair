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
public class DeleteAllTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeAll
	public static void beforeClass() {
		db.execute("create table a (id int primary key, a1 varchar(50))");
		db.execute("insert into a (id, a1) values (10, 'deleted10')");
		db.execute("insert into a (id, a1) values (11, 'deleted11')");
		db.execute("insert into a (id, a1) values (12, 'deleted12')");
		ApiTestSupport.reInitialize();
	}

	@AfterAll
	public static void afterClass() {
		db.execute("drop table a");
	}

	@Test
	public void test() {
		assertEquals(Integer.valueOf(2),
			db.queryForObject("select count(*) from a", Integer.class),
			"Count");
		values = db.queryForList("select * from a");
		verifyRow(0, "a10");
		verifyRow(1, "a11");
	}

	private void verifyRow(int id, String value) {
		assertEquals(id, values.get(id).get("id"), "id " + id);
		assertEquals(value, values.get(id).get("a1"), "a1 " + id);
	}
}
