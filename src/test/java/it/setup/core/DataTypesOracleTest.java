package it.setup.core;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;

import org.apache.commons.codec.binary.Hex;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(LightAir.class)
@Setup
public class DataTypesOracleTest extends DataTypesTestBase {

	static {
		db = connect("jdbc:oracle:thin:@localhost:1521:xe", "SYSTEM",
				"password");
		replaceConfig("oracle");
	}

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table data_types (id number(10,0) primary key, char_type char(20), "
				+ "string_type varchar2(50), integer_type integer, date_type date, "
				+ "timestamp_type timestamp, double_type binary_double, boolean_type number(1), "
				+ "bigint_type number(19), decimal_type decimal(20,2), clob_type clob, "
				+ "blob_type blob, raw_type raw(8))");
	}

	@Test
	// requires Oracle XE 11.2.0
	// installed locally at default path /u01/app/oracle/product/11.2.0/xe
	// with user SYSTEM / password
	@Ignore
	public void test() {
		perform();
	}

	@Override
	protected void verify() {
		verifyRow(0, "ABCDEFGHIJKLMNOP    ", "abcdefghijklmnopqrstuvxyz",
				12345678, new DateMidnight(2999, 12, 31), new DateTime(2998,
						11, 30, 22, 57, 56, 789), 8765.4321, true,
				9223372036854770000L, new BigDecimal("12345678901234.56"),
				"text1", "1234567890abcdef", "fedcba0987654321");
		verifyRow(1, "ABCDEFGHIJKLMNO2    ", "abcdefghijklmnopqrstuvxy2",
				12345672, new DateMidnight(2999, 12, 21), new DateTime(2998,
						11, 20, 22, 57, 26, 789), 8762.4322, false,
				9223372036854770002L, new BigDecimal("12345678901232.52"),
				"text2", "1234567890abcde2", "fedcba0987654322");
	}

	private void verifyRow(int id, String char_type, String string_type,
			int integer_type, DateMidnight date_type, DateTime timestamp_type,
			double double_type, boolean boolean_type, long bigint_type,
			BigDecimal decimal_type, String clob_type, String blob_type,
			String raw_type) {
		assertEquals("id " + id, id,
				((BigDecimal) values.get(id).get("id")).intValue());
		assertEquals("char_type " + id, char_type,
				values.get(id).get("char_type"));
		assertEquals("string_type " + id, string_type,
				values.get(id).get("string_type"));
		assertEquals("integer_type " + id, integer_type, ((BigDecimal) values
				.get(id).get("integer_type")).intValue());
		assertEquals("date_type " + id, date_type.toDate(),
				values.get(id).get("date_type"));
		assertEquals("timestamp_type " + id, timestamp_type.toDate(), values
				.get(id).get("timestamp_type"));
		assertEquals("double_type type " + id, double_type,
				values.get(id).get("double_type"));
		assertEquals(
				"boolean_type type " + id,
				boolean_type,
				new Integer(1).equals(((BigDecimal) values.get(id).get(
						"boolean_type")).intValue()));
		assertEquals("bigint_type type " + id, bigint_type,
				((BigDecimal) values.get(id).get("bigint_type")).longValue());
		assertEquals("decimal_type type " + id, decimal_type, values.get(id)
				.get("decimal_type"));
		assertEquals("clob_type type " + id, clob_type,
				values.get(id).get("clob_type"));
		assertEquals("blob_type type " + id, blob_type,
				Hex.encodeHexString(((byte[]) values.get(id).get("blob_type"))));
		assertEquals("raw_type type " + id, raw_type,
				Hex.encodeHexString(((byte[]) values.get(id).get("raw_type"))));
	}

}
