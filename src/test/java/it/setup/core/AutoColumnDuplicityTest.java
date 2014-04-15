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
public class AutoColumnDuplicityTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a (ao char(20),gs char(20),hm char(20),kg char(20))");
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a");
	}

	@Test
	public void aduplicity() {
		// method name must start with "a" to run first
		values = db.queryForList("select * from a");

		perform("Standard", "ao", "98");
		perform("Same hash", "gs", "99");
		perform("Increment to 00", "hm", "00");
		perform("Increment to 01", "kg", "01");
	}

	@Test
	@Setup("AutoColumnClearCacheTest.xml")
	public void clearCache_SingleDataset() {
		values = db.queryForList("select * from a");

		perform("First", "hm", "98");
		perform("Second", "kg", "99");
	}

	private void perform(String test, String column, String columnHash) {
		verifyValue(test, column, columnHash, 0);
		verifyValue(test, column, columnHash, 1);
	}

	private void verifyValue(String test, String column, String columnHash,
			int row) {
		assertEquals(test + " " + row,
				column + " 421" + columnHash + "0" + row,
				values.get(row).get(column));
	}

}
