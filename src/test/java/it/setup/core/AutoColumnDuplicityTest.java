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
public class AutoColumnDuplicityTest extends CommonTestBase {

	//	@Test
	public void generateDuplicities() {
		for (char a = 'a'; a <= 'z'; a++) {
			for (char b = 'a'; b <= 'z'; b++) {
				for (char c = 'a'; c <= 'z'; c++) {
					String column = "" + a + b + c;
					if (Hash.generate(column, 3) == 598) {
						System.out.println(column);
					}
				}
			}
		}
	}

	List<Map<String, Object>> values;

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a (dld char(20),glx char(20),gps char(20),hff char(20))");
		ApiTestSupport.reInitialize();
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a");
	}

	@Test
	public void aduplicity() {
		// method name must start with "a" to run first
		values = db.queryForList("select * from a");

		perform("Standard", "dld", "598");
		perform("Same hash", "glx", "599");
		perform("Increment to 00", "gps", "600");
		perform("Increment to 01", "hff", "601");
	}

	@Test
	@Setup("AutoColumnKeepValues.xml")
	public void keepValues() {
		values = db.queryForList("select * from a");

		perform("First", "gps", "600");
		perform("Second", "hff", "601");
	}

	private void perform(String test, String column, String columnHash) {
		verifyValue(test, column, columnHash, 1);
		verifyValue(test, column, columnHash, 2);
	}

	private void verifyValue(String test, String column, String columnHash, int row) {
		assertEquals(test + " " + row,
				column + " 10421" + columnHash + "0" + row,
				values.get(row - 1).get(column));
	}
}
