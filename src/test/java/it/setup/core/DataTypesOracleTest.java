package it.setup.core;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.support.ConfigSupport;

@Ignore
/**
 * Requires:
 * <ul>
 * <li>Running Oracle XE 11.2.0</li>
 * <li>User with credentials: SYSTEM / password.</li>
 * <li>Driver ojdbc6_g.jar copied into jre/lib/ext directory in the JDK.</li>
 * </ul>
 */
@RunWith(LightAir.class)
@Setup
public class DataTypesOracleTest extends DataTypesSetupTestBase {

	static {
		connect("jdbc:oracle:thin:@localhost:1521:xe", "SYSTEM", "password");
		ConfigSupport.replaceConfig("oracle");
	}

	@BeforeClass
	public static void beforeClass() {
		createTable();
	}

	public static void createTable() {
		db.execute("create table data_types (id number(10,0) primary key, char_type char(20), "
				+ "varchar_type varchar2(50), integer_type integer, "
				+ "date_type date, time_type timestamp, timestamp_type timestamp, "
				+ "double_type binary_double, boolean_type number(1), bigint_type number(19), "
				+ "decimal_type decimal(20,2), clob_type clob, blob_type blob, binary_type raw(8))");
	}

	@Test
	public void test() {
		perform();
	}

	@Override
	protected void verify() {
		verifyRowOracle(0, "efghijklmnopqrs     ", "abcdefghijklmnopqrstuvxyz",
				12345678, "2999-12-31 00:00:00.0", "1970-01-01 23:59:58.0",
				"2998-11-30 22:57:56.789", 8765.4321, true,
				9223372036854770000L, new BigDecimal("12345678901234.56"),
				"text1", "EjRWeJCrzeI=", "/ty6CYdlQyI=");
		verifyRowOracle(1, null, null, 0, "2000-01-02 00:00:00.0",
				"1970-01-01 00:00:00.0", "2000-01-02 03:04:05.678", 0., false,
				0L, new BigDecimal("0"), null, null, null);
		verifyRowOracle(2, null, null, null, null, null, null, null, null,
				null, null, null, null, null);
		verifyRowOracle(3, "char_type 8466903   ", "varchar_type 8464103",
				8463603, "1903-01-09 00:31:43.0", "2096-08-03 23:53:23.803",
				"2088-12-03 23:06:43.003", 84684.03, false, 8464903L,
				new BigDecimal("84670.03"), "clob_type 8463203",
				"YmxvYl90eXBlIDg0NjU0MDM=", "Yjg0NjIzMDM=");
	}

	protected void verifyRowOracle(int id, String char_type,
			String varchar_type, Integer integer_type, String date_type,
			String time_type, String timestamp_type, Double double_type,
			Boolean boolean_type, Long bigint_type, BigDecimal decimal_type,
			String clob_type, String blob_type, String binary_type) {
		assertEquals("id " + id, id,
				((BigDecimal) values.get(id).get("id")).intValue());
		assertEquals("char_type " + id, char_type,
				values.get(id).get("char_type"));
		assertEquals("varchar_type " + id, varchar_type,
				values.get(id).get("varchar_type"));
		if (null == integer_type) {
			assertNull("integer_type " + id, values.get(id).get("integer_type"));
		} else {
			assertEquals("integer_type " + id, integer_type.intValue(),
					((BigDecimal) values.get(id).get("integer_type"))
							.intValue());
		}
		if (null == date_type) {
			assertNull("date_type " + id, values.get(id).get("date_type"));
		} else {
			assertEquals("date_type " + id, date_type,
					values.get(id).get("date_type").toString());
		}
		if (null == time_type) {
			assertNull("time_type " + id, values.get(id).get("time_type"));
		} else {
			assertEquals("time_type " + id, time_type,
					values.get(id).get("time_type").toString());
		}
		if (null == timestamp_type) {
			assertNull("timestamp_type " + id,
					values.get(id).get("timestamp_type"));
		} else {
			assertEquals("timestamp_type " + id, timestamp_type, values.get(id)
					.get("timestamp_type").toString());
		}
		assertEquals("double_type type " + id, double_type,
				values.get(id).get("double_type"));
		if (null == boolean_type) {
			assertNull("boolean_type type " + id,
					values.get(id).get("boolean_type"));
		} else {
			assertEquals("boolean_type type " + id, boolean_type,
					1 == ((BigDecimal) values.get(id).get("boolean_type"))
							.intValue());
		}
		if (null == bigint_type) {
			assertNull("bigint_type type " + id,
					values.get(id).get("bigint_type"));
		} else {
			assertEquals("bigint_type type " + id, bigint_type.longValue(),
					((BigDecimal) values.get(id).get("bigint_type"))
							.longValue());
		}
		assertEquals("decimal_type type " + id, decimal_type, values.get(id)
				.get("decimal_type"));
		assertEquals("clob_type type " + id, clob_type,
				values.get(id).get("clob_type"));
		assertEquals("blob_type type " + id, blob_type,
				convertBytesToString(values.get(id).get("blob_type")));
		assertEquals("binary_type type " + id, binary_type,
				convertBytesToString(values.get(id).get("binary_type")));
	}

}
