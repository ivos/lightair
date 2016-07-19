package it.setup.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;
import net.sf.lightair.internal.Api;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(LightAir.class)
@Setup({ "MergeColumnsAcrossFiles.1.xml", "MergeColumnsAcrossFiles.2.xml",
		"MergeColumnsAcrossFiles.3.xml" })
public class MergeColumnsAcrossFilesTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a (id int primary key, "
				+ "a1 varchar(50), a2 varchar(50), "
				+ "a3 varchar(50), a4 varchar(50))");
		Api.reInitialize();
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a");
	}

	@Test
	public void test() {
		assertEquals("Count", new Integer(5),
				db.queryForObject("select count(*) from a", Integer.class));
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
		assertEquals("id " + id, id, values.get(id).get("id"));
		assertEquals("a1 " + id, a1, values.get(id).get("a1"));
		assertEquals("a2 " + id, a2, values.get(id).get("a2"));
		assertEquals("a3 " + id, a3, values.get(id).get("a3"));
		assertEquals("a4 " + id, a4, values.get(id).get("a4"));
	}

}
