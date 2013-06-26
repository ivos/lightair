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
public class ForeignKeysTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table master (id int primary key, m1 varchar(50))");
		db.execute("create table detail (id int primary key, master_id int, d1 varchar(50))");
		db.execute("alter table detail add constraint fk_detail_master "
				+ "foreign key (master_id) references master (id)");
	}

	@AfterClass
	public static void afterClass() {
		db.execute("alter table detail drop constraint fk_detail_master");
		db.execute("drop table detail");
		db.execute("drop table master");
	}

	@Test
	public void test() {
		assertEquals("Count master", new Integer(2),
				db.queryForObject("select count(*) from master", Integer.class));
		values = db.queryForList("select * from master");
		verifyMaster(0, "01");
		verifyMaster(1, "11");
		assertEquals("Count detail", new Integer(6),
				db.queryForObject("select count(*) from detail", Integer.class));
		values = db.queryForList("select * from detail");
		verifyDetail(0, 0, 0, "001");
		verifyDetail(1, 1, 0, "011");
		verifyDetail(2, 2, 0, "021");
		verifyDetail(3, 10, 1, "101");
		verifyDetail(4, 11, 1, "111");
		verifyDetail(5, 12, 1, "121");
	}

	private void verifyMaster(int id, String m1) {
		assertEquals("id " + id, id, values.get(id).get("id"));
		assertEquals("m1 " + id, m1, values.get(id).get("m1"));
	}

	private void verifyDetail(int id, int dbId, int masterId, String d1) {
		assertEquals("id " + id, dbId, values.get(id).get("id"));
		assertEquals("masterId " + id, masterId, values.get(id)
				.get("master_id"));
		assertEquals("d1 " + id, d1, values.get(id).get("d1"));
	}

}
