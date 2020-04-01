package it.setup.core;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.support.ApiTestSupport;
import test.support.ConfigSupport;

import java.math.BigDecimal;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

/**
 * Requires:
 * <ul>
 * <li>Running Oracle XE 18.4.0.0.0 (18c)</li>
 * <li>User with credentials: SYSTEM / password.</li>
 * <li>Driver ojdbc8.jar copied into jre/lib/ext directory in the JDK.</li>
 * </ul>
 */
@Ignore
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
		db.execute("create table data_types (id number(10,0) primary key,"
				+ " char_type char(25), varchar_type varchar(50), varchar2_type varchar2(50),"
				+ " nchar_type nchar(20), nvarchar2_type nvarchar2(50),"
				+ " integer_type integer, bigint_type number(19),"
				+ " decimal_type decimal(20,2), number_type number(16, 8), float_type float(126),"
				+ " binary_float_type binary_float, binary_double_type binary_double,"
				+ " date_type date, time_type timestamp, timestamp_type timestamp,"
				+ " boolean_type number(1),"
				+ " clob_type clob, nclob_type nclob, blob_type blob, binary_type raw(15)"
				+ ")");
		ApiTestSupport.reInitialize();
	}

	@Test
	public void test() {
		perform();
	}

	@Override
	protected void verify() {
		// full
		verifyRow(0,
				"efghijklmnopqrs          ", "abcdefghijklmnopqrstuvxyz",
				"2abcdefghijklmnopqrstuvxyz",
				"nefghijklmnopqrs    ", "n2abcdefghijklmnopqrstuvxyz",
				12345678, 9223372036854770000L,
				new BigDecimal("12345678901234.56"), new BigDecimal("12345678.90123456"),
				new BigDecimal("876.543"),
				765.432f, 8765.4321,
				"2999-12-31 00:00:00.0", "1970-01-01 23:59:58.0",
				"2998-11-30 22:57:56.789",
				true,
				"text1", "ntext1", "EjRWeJCrzeI=", "/ty6CYdlQyI="
		);
		// empty
		verifyRow(1,
				null, null, null,
				null, null,
				0, 0L,
				new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"),
				0f, 0.,
				"2000-01-02 00:00:00.0", "1970-01-01 00:00:00.0",
				"2000-01-02 03:04:05.678",
				false,
				null, null, null, null
		);
		// null
		verifyRow(2,
				null, null, null,
				null, null,
				null, null,
				null, null, null,
				null, null,
				null, null, null,
				null,
				null, null, null, null
		);
		// auto
		verifyRow(3,
				"char_type 1384656904     ", "varchar_type 1384684104",
				"varchar2_type 1384671904",
				"nchar_type1384698804", "nvarchar2_type 1384660104",
				1384653604, 1384644904L,
				new BigDecimal("13846469.04"), new BigDecimal("13.84603704"), new BigDecimal("13846568"),
				13846789f, 13846774.04,
				"1976-12-12 00:00:00.0", "1935-01-22 08:26:44.804",
				"1900-01-05 04:53:24.004",
				false,
				"clob_type 1384603204", "nclob_type 1384602404",
				"YmxvYl90eXBlIDEzODQ2NzU0MDQ=", "YmluYXIxMzg0NjUyMzA0"
		);
	}

	private void verifyRow(
			int id,
			String char_type, String varchar_type, String varchar2_type,
			String nchar_type, String nvarchar2_type,
			Integer integer_type, Long bigint_type,
			BigDecimal decimal_type, BigDecimal number_type, BigDecimal float_type,
			Float binary_float_type, Double binary_double_type,
			String date_type, String time_type, String timestamp_type,
			Boolean boolean_type,
			String clob_type, String nclob_type, String blob_type, String binary_type
	) {
		assertEquals("id " + id, id, ((BigDecimal) values.get(id).get("id")).intValue());
		assertEquals("char_type " + id, char_type, values.get(id).get("char_type"));
		assertEquals("varchar_type " + id, varchar_type, values.get(id).get("varchar_type"));
		assertEquals("varchar2_type " + id, varchar2_type, values.get(id).get("varchar2_type"));
		assertEquals("nchar_type " + id, nchar_type, values.get(id).get("nchar_type"));
		assertEquals("nvarchar2_type " + id, nvarchar2_type, values.get(id).get("nvarchar2_type"));
		verifyField(id, "integer_type", integer_type, Function.identity(), v -> ((BigDecimal) v).intValue());
		verifyField(id, "bigint_type", bigint_type, Function.identity(), v -> ((BigDecimal) v).longValue());
		assertEquals("decimal_type type " + id, decimal_type, values.get(id).get("decimal_type"));
		assertEquals("number_type type " + id, number_type, values.get(id).get("number_type"));
		assertEquals("float_type type " + id, float_type, values.get(id).get("float_type"));
		assertEquals("binary_float_type type " + id, binary_float_type, values.get(id).get("binary_float_type"));
		assertEquals("binary_double_type type " + id, binary_double_type, values.get(id).get("binary_double_type"));
		verifyField(id, "date_type", date_type, Function.identity(), Object::toString);
		verifyField(id, "time_type", time_type, Function.identity(), Object::toString);
		verifyField(id, "timestamp_type", timestamp_type, Function.identity(), Object::toString);
		verifyField(id, "boolean_type", boolean_type, Function.identity(), v -> 1 == ((BigDecimal) v).intValue());
		assertEquals("clob_type type " + id, clob_type, values.get(id).get("clob_type"));
		assertEquals("nclob_type type " + id, nclob_type, values.get(id).get("nclob_type"));
		assertEquals("blob_type type " + id, blob_type, convertBytesToString(values.get(id).get("blob_type")));
		assertEquals("binary_type type " + id, binary_type, convertBytesToString(values.get(id).get("binary_type")));
	}
}
