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
@Setup({"MergeColumnsAcrossFiles.1.xml", "MergeColumnsAcrossFiles.2.xml",
	"MergeColumnsAcrossFiles.3.xml"})
public class MergeColumnsAcrossFilesTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeAll
	public static void beforeClass() {
		db.execute("create table a (id int primary key, "
			+ "a1 varchar(50), a2 varchar(50), "
			+ "a3 varchar(50), a4 varchar(50))");
		ApiTestSupport.reInitialize();
	}

	@AfterAll
	public static void afterClass() {
		db.execute("drop table a");
	}

	@Test
	public void test() {
		assertEquals(Integer.valueOf(5), db.queryForObject("select count(*) from a", Integer.class), "Count");
		values = db.queryForList("select * from a");
		// file 1:
		verifyRow(0, "01", null, null, null);
		verifyRow(1, "11", "12", null, null);
		// file 2:
		verifyRow(2, null, "22", "23", null);
		// file 3:
		verifyRow(3, null, null, "33", "34");
		verifyRow(4, null, null, null, "44");
	}

	private void verifyRow(int id, String a1, String a2, String a3, String a4) {
		assertEquals(id, values.get(id).get("id"), "id " + id);
		assertEquals(a1, values.get(id).get("a1"), "a1 " + id);
		assertEquals(a2, values.get(id).get("a2"), "a2 " + id);
		assertEquals(a3, values.get(id).get("a3"), "a3 " + id);
		assertEquals(a4, values.get(id).get("a4"), "a4 " + id);
	}
}
