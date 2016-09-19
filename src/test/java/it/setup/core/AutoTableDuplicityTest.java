package it.setup.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;
import net.sf.lightair.internal.auto.Hash;
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
public class AutoTableDuplicityTest extends CommonTestBase {

	//	@Test
	public void generateDuplicities() {
		for (char a = 'a'; a <= 'z'; a++) {
			for (char b = 'a'; b <= 'z'; b++) {
				for (char c = 'a'; c <= 'z'; c++) {
					for (char d = 'a'; d <= 'z'; d++) {
						String column = "" + a + b + c + d;
						if (Hash.generate(column, 4) == 5998) {
							System.out.println(column);
						}
					}
				}
			}
		}
	}

	List<Map<String, Object>> values;

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table ayck (a1 char(20))");
		db.execute("create table cbij (a1 char(20))");
		db.execute("create table cgom (a1 char(20))");
		db.execute("create table ciho (a1 char(20))");
		ApiTestSupport.reInitialize();
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table ayck");
		db.execute("drop table cbij");
		db.execute("drop table cgom");
		db.execute("drop table ciho");
	}

	@Test
	public void test() {
		perform("Standard", "ayck", "5998");
		perform("Same hash", "cbij", "5999");
		perform("Increment to 000", "cgom", "6000");
		perform("Increment to 001", "ciho", "6001");
	}

	private void perform(String test, String table, String tableHash) {
		values = db.queryForList("select * from " + table);
		verifyValue(test, table, tableHash, 1);
		verifyValue(test, table, tableHash, 2);
	}

	private void verifyValue(String test, String table, String tableHash, int id) {
		assertEquals(test + " " + id, "a1 1" + tableHash + "2730" + id, values.get(id - 1).get("a1"));
	}

}
