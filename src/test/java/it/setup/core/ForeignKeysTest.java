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
public class ForeignKeysTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeAll
	public static void beforeClass() {
		db.execute("create table master (id int primary key, m1 varchar(50))");
		db.execute("create table detail (id int primary key, master_id int, d1 varchar(50))");
		db.execute("alter table detail add constraint fk_detail_master "
			+ "foreign key (master_id) references master (id)");
		ApiTestSupport.reInitialize();
	}

	@AfterAll
	public static void afterClass() {
		db.execute("alter table detail drop constraint fk_detail_master");
		db.execute("drop table detail");
		db.execute("drop table master");
	}

	@Test
	public void test() {
		assertEquals(Integer.valueOf(2),
			db.queryForObject("select count(*) from master", Integer.class), "Count master");
		values = db.queryForList("select * from master");
		verifyMaster(0, "01");
		verifyMaster(1, "11");
		assertEquals(Integer.valueOf(6),
			db.queryForObject("select count(*) from detail", Integer.class), "Count detail");
		values = db.queryForList("select * from detail");
		verifyDetail(0, 0, 0, "001");
		verifyDetail(1, 1, 0, "011");
		verifyDetail(2, 2, 0, "021");
		verifyDetail(3, 10, 1, "101");
		verifyDetail(4, 11, 1, "111");
		verifyDetail(5, 12, 1, "121");
	}

	private void verifyMaster(int id, String m1) {
		assertEquals(id, values.get(id).get("id"), "id " + id);
		assertEquals(m1, values.get(id).get("m1"), "m1 " + id);
	}

	private void verifyDetail(int id, int dbId, int masterId, String d1) {
		assertEquals(dbId, values.get(id).get("id"), "id " + id);
		assertEquals(masterId, values.get(id).get("master_id"), "masterId " + id);
		assertEquals(d1, values.get(id).get("d1"), "d1 " + id);
	}
}
