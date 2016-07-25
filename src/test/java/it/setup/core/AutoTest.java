package it.setup.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.support.ApiTestSupport;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(LightAir.class)
@Setup({"AutoTest.xml", "AutoTest2.xml"})
public class AutoTest extends CommonTestBase {

	List<Map<String, Object>> values;

	// TODO add additional sql types (smallint, ...)

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a1 (id int primary key, char_type char(20), "
				+ "varchar_type varchar(50), integer_type integer, "
				+ "date_type date, time_type time, timestamp_type timestamp, "
				+ "double_type double, boolean_type boolean, bigint_type bigint, "
				+ "decimal_type decimal(20,3), clob_type clob, blob_type blob, binary_type binary(20))");
		db.execute("create table a2 (id int primary key, char_type char(20), "
				+ "varchar_type varchar(50), integer_type integer, "
				+ "date_type date, time_type time, timestamp_type timestamp, "
				+ "double_type double, boolean_type boolean, bigint_type bigint, "
				+ "decimal_type decimal(20,3), clob_type clob, blob_type blob, binary_type binary(20))");
		db.execute("create table a3 (id int primary key, char_type char(20), "
				+ "varchar_type varchar(50), integer_type integer, "
				+ "date_type date, time_type time, timestamp_type timestamp, "
				+ "double_type double, boolean_type boolean, bigint_type bigint, "
				+ "decimal_type decimal(20,3), clob_type clob, blob_type blob, binary_type binary(20))");
		ApiTestSupport.reInitialize();
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a1");
		db.execute("drop table a2");
		db.execute("drop table a3");
	}

	@Test
	public void test() {
		assertEquals("Count", new Integer(5),
				db.queryForObject("select count(*) from a1", Integer.class));
		values = db.queryForList("select * from a1");

		verifyRow(0, 1327384901, "char_type 1327356901", "varchar_type 1327384101",
				1327353601, DateMidnight.parse("1990-08-18"),
				LocalTime.parse("03:46:41"),
				DateTime.parse("1913-09-11T00:13:21.001"), 13273684.01, true,
				1327344901L, new BigDecimal("1327346.901"), "clob_type 1327303201",
				"blob_type 1327375401", "binary_typ1327352301");
		verifyRow(1, 1327384902, "char_type 1327356902", "varchar_type 1327384102",
				1327353602, DateMidnight.parse("1990-08-19"),
				LocalTime.parse("03:46:42"),
				DateTime.parse("1913-09-12T00:13:22.002"), 13273684.02, false,
				1327344902L, new BigDecimal("1327346.902"), "clob_type 1327303202",
				"blob_type 1327375402", "binary_typ1327352302");
		verifyRow(2, 1327384903, "char_type 1327356903", "varchar_type 1327384103",
				1327353603, DateMidnight.parse("1990-08-20"),
				LocalTime.parse("03:46:43"),
				DateTime.parse("1913-09-13T00:13:23.003"), 13273684.03, true,
				1327344903L, new BigDecimal("1327346.903"), "clob_type 1327303203",
				"blob_type 1327375403", "binary_typ1327352303");
		verifyRow(3, 1327384904, "char_type 1327356904", "varchar_type 1327384104",
				1327353604, DateMidnight.parse("1990-08-21"),
				LocalTime.parse("03:46:44"),
				DateTime.parse("1913-09-14T00:13:24.004"), 13273684.04, false,
				1327344904L, new BigDecimal("1327346.904"), "clob_type 1327303204",
				"blob_type 1327375404", "binary_typ1327352304");

		assertEquals("Count", new Integer(4),
				db.queryForObject("select count(*) from a2", Integer.class));
		values = db.queryForList("select * from a2");

		verifyRow(0, 1603184901, "char_type 1603156901", "varchar_type 1603184101",
				1603153601, DateMidnight.parse("2007-01-21"),
				LocalTime.parse("06:53:21"),
				DateTime.parse("1930-02-14T03:20:01.001"), 16031684.01, true,
				1603144901L, new BigDecimal("1603146.901"), "clob_type 1603103201",
				"blob_type 1603175401", "binary_typ1603152301");
		verifyRow(1, 1603184902, "char_type 1603156902", "varchar_type 1603184102",
				1603153602, DateMidnight.parse("2007-01-22"),
				LocalTime.parse("06:53:22"),
				DateTime.parse("1930-02-15T03:20:02.002"), 16031684.02, false,
				1603144902L, new BigDecimal("1603146.902"), "clob_type 1603103202",
				"blob_type 1603175402", "binary_typ1603152302");
		verifyRow(2, 1603184903, "char_type 1603156903", "varchar_type 1603184103",
				1603153603, DateMidnight.parse("2007-01-23"),
				LocalTime.parse("06:53:23"),
				DateTime.parse("1930-02-16T03:20:03.003"), 16031684.03, true,
				1603144903L, new BigDecimal("1603146.903"), "clob_type 1603103203",
				"blob_type 1603175403", "binary_typ1603152303");
		verifyRow(3, 1603184904, "char_type 1603156904", "varchar_type 1603184104",
				1603153604, DateMidnight.parse("2007-01-24"),
				LocalTime.parse("06:53:24"),
				DateTime.parse("1930-02-17T03:20:04.004"), 16031684.04, false,
				1603144904L, new BigDecimal("1603146.904"), "clob_type 1603103204",
				"blob_type 1603175404", "binary_typ1603152304");

		assertEquals("Count", new Integer(1),
				db.queryForObject("select count(*) from a3", Integer.class));
		values = db.queryForList("select * from a3");

		verifyRow(0, 1336684901, "char_type 1336656901", "varchar_type 1336684101",
				1336653601, DateMidnight.parse("2070-01-10"),
				LocalTime.parse("19:06:41"),
				DateTime.parse("1993-02-03T15:33:21.001"), 13366684.01, true,
				1336644901L, new BigDecimal("1336646.901"), "clob_type 1336603201",
				"blob_type 1336675401", "binary_typ1336652301");
	}

	@Test
	@Setup("AutoTest2.xml")
	public void rowIndexStartsFrom0InNewTest() {
		assertEquals("Count", new Integer(2),
				db.queryForObject("select count(*) from a1", Integer.class));
		values = db.queryForList("select * from a1");

		verifyRow(0, 1327384901, "char_type 1327356901", "varchar_type 1327384101",
				1327353601, DateMidnight.parse("1990-08-18"),
				LocalTime.parse("03:46:41"),
				DateTime.parse("1913-09-11T00:13:21.001"), 13273684.01, true,
				1327344901L, new BigDecimal("1327346.901"), "clob_type 1327303201",
				"blob_type 1327375401", "binary_typ1327352301");
		verifyRow(1, 1327384902, "char_type 1327356902", "varchar_type 1327384102",
				1327353602, DateMidnight.parse("1990-08-19"),
				LocalTime.parse("03:46:42"),
				DateTime.parse("1913-09-12T00:13:22.002"), 13273684.02, false,
				1327344902L, new BigDecimal("1327346.902"), "clob_type 1327303202",
				"blob_type 1327375402", "binary_typ1327352302");
	}

	protected void verifyRow(int row, int id, String char_type,
	                         String varchar_type, Integer integer_type, DateMidnight date_type,
	                         LocalTime time_type, DateTime timestamp_type, Double double_type,
	                         Boolean boolean_type, Long bigint_type, BigDecimal decimal_type,
	                         String clob_type, String blob_type, String binary_type) {
		assertEquals("id " + row, id, values.get(row).get("id"));
		assertEquals("char_type " + row, char_type, values.get(row).get("char_type"));
		assertEquals("varchar_type " + row, varchar_type, values.get(row).get("varchar_type"));
		assertEquals("integer_type " + row, integer_type, values.get(row).get("integer_type"));
		if (null == date_type) {
			assertNull("date_type " + row, values.get(row).get("date_type"));
		} else {
			assertEquals("date_type " + row, date_type.toDate(), values.get(row).get("date_type"));
		}
		if (null == time_type) {
			assertNull("time_type " + row, values.get(row).get("time_type"));
		} else {
			assertEquals("time_type " + row, time_type,
					LocalTime.fromDateFields((Date) values.get(row).get("time_type")));
		}
		if (null == timestamp_type) {
			assertNull("timestamp_type " + row, values.get(row).get("timestamp_type"));
		} else {
			assertEquals("timestamp_type " + row, new Timestamp(timestamp_type.toDate().getTime()),
					values.get(row).get("timestamp_type"));
		}
		assertEquals("double_type type " + row, double_type, values.get(row).get("double_type"));
		assertEquals("boolean_type type " + row, boolean_type, values.get(row).get("boolean_type"));
		assertEquals("bigint_type type " + row, bigint_type, values.get(row).get("bigint_type"));
		assertEquals("decimal_type type " + row, decimal_type, values.get(row).get("decimal_type"));
		assertEquals("clob_type type " + row, clob_type, values.get(row).get("clob_type"));
		assertEquals("blob_type type " + row, blob_type, convertBytesToString(values.get(row).get("blob_type")));
		assertEquals("binary_type type " + row, binary_type, convertBytesToString(values.get(row).get("binary_type")));
	}

	protected String convertBytesToString(Object bytes) {
		if (null == bytes) {
			return null;
		}
		return new String((byte[]) bytes);
	}

}
