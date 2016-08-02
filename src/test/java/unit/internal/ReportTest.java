package unit.internal;

import net.sf.lightair.internal.Keywords;
import net.sf.lightair.internal.Report;
import org.joda.time.DateTime;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static unit.internal.CompareTest.createRow;

public class ReportTest implements Keywords {

	private static Map<String, List<?>> createTable(
			List<Map<String, Object>> missing,
			List<Map<String, Object>> different,
			List<Map<String, Object>> une11xpected) {
		Map<String, List<?>> table = new LinkedHashMap<>();
		table.put(MISSING, missing);
		table.put(DIFFERENT, different);
		table.put(UNEXPECTED, une11xpected);
		return Collections.unmodifiableMap(table);
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Map<String, List<?>>> createProfile(Object... data) {
		assertTrue("Data in pairs", data.length % 2 == 0);
		Map<String, Map<String, List<?>>> profile = new LinkedHashMap<>();
		for (int i = 0; i < data.length; i = i + 2) {
			profile.put((String) data[i], (Map) data[i + 1]);
		}
		return Collections.unmodifiableMap(profile);
	}

	@Test
	public void empty() {
		Map<String, Map<String, Map<String, List<?>>>> differences = new LinkedHashMap<>();
		differences.put("p1", createProfile(
				"t11", createTable(
						Collections.emptyList(),
						Collections.emptyList(),
						Collections.emptyList()),
				"t12", createTable(
						Collections.emptyList(),
						Collections.emptyList(),
						Collections.emptyList())
		));
		differences.put("p2", createProfile(
				"t21", createTable(
						Collections.emptyList(),
						Collections.emptyList(),
						Collections.emptyList())
		));

		String report = Report.report(differences);
		assertEquals("", report);
	}

	@Test
	public void diffs() {
		Map<String, Map<String, Map<String, List<?>>>> differences = new LinkedHashMap<>();
		differences.put("p1", createProfile(
				"t11", createTable(
						Arrays.asList(
								createRow("mis111a", "mis111av", "mis111b", "mis111bv", "mis111c", "mis111cv"),
								createRow("mis112a", "mis112av", "mis112b", "mis112bv", "mis112c", "mis112cv")
						),
						Arrays.asList(
								createRow(
										EXPECTED, createRow("dif111a", "dif111av", "dif111b", "dif111bv", "dif111c", "dif111cv"),
										DIFFERENCES,
										Arrays.asList(
												createRow(COLUMN, "dif111a", EXPECTED, "dif111aexp", ACTUAL, "dif111aact"),
												createRow(COLUMN, "dif111b", EXPECTED, "dif111bexp", ACTUAL, "dif111bact")
										)),
								createRow(
										EXPECTED, createRow("dif112a", "dif112av", "dif112b", "dif112bv", "dif112c", "dif112cv"),
										DIFFERENCES,
										Arrays.asList(
												createRow(COLUMN, "dif112a", EXPECTED, "dif112aexp", ACTUAL, "dif112aact"),
												createRow(COLUMN, "dif112b", EXPECTED, "dif112bexp", ACTUAL, "dif112bact")
										))
						),
						Arrays.asList(
								createRow("une111a", "une111av", "une111b", "une111bv", "une111c", "une111cv"),
								createRow("une112a", "une112av", "une112b", "une112bv", "une112c", "une112cv")
						)),
				"t12", createTable(
						Arrays.asList(
								createRow("mis121a", "mis121av", "mis121b", "mis121bv", "mis121c", "mis121cv")
						),
						Arrays.asList(
								createRow(
										EXPECTED, createRow("dif121a", "dif121av", "dif121b", "dif121bv", "dif121c", "dif121cv"),
										DIFFERENCES,
										Arrays.asList(
												createRow(COLUMN, "dif121a", EXPECTED, "dif121aexp", ACTUAL, "dif121aact")
										))
						),
						Arrays.asList(
								createRow("une121a", "une121av", "une121b", "une121bv", "une121c", "une121cv")
						))
		));
		differences.put("p2", createProfile(
				"t21", createTable(
						Arrays.asList(
								createRow("mis211a", "mis211av", "mis211b", "mis211bv", "mis211c", "mis211cv")
						),
						Arrays.asList(
								createRow(
										EXPECTED, createRow("dif211a", "dif211av", "dif211b", "dif211bv", "dif211c", "dif211cv"),
										DIFFERENCES,
										Arrays.asList(
												createRow(COLUMN, "dif211a", EXPECTED, "dif211aexp", ACTUAL, "dif211aact")
										))
						),
						Arrays.asList(
								createRow("une211a", "une211av", "une211b", "une211bv", "une211c", "une211cv")
						))
		));
		differences.put(DEFAULT_PROFILE, createProfile(
				"t31", createTable(
						Arrays.asList(
								createRow("mis311a", "mis311av")
						),
						Collections.emptyList(),
						Collections.emptyList())
		));

		String report = Report.report(differences);

		String expected = "Differences found between the expected data set and actual database content.\n" +
				"Found differences for table [p1]/t11:\n" +
				"  Missing row: {mis111a=mis111av, mis111b=mis111bv, mis111c=mis111cv}\n" +
				"  Missing row: {mis112a=mis112av, mis112b=mis112bv, mis112c=mis112cv}\n" +
				"  Different row: {dif111a=dif111av, dif111b=dif111bv, dif111c=dif111cv}\n" +
				"   Best matching differences: \n" +
				"    dif111a: expected [dif111aexp], but was [dif111aact]\n" +
				"    dif111b: expected [dif111bexp], but was [dif111bact]\n" +
				"  Different row: {dif112a=dif112av, dif112b=dif112bv, dif112c=dif112cv}\n" +
				"   Best matching differences: \n" +
				"    dif112a: expected [dif112aexp], but was [dif112aact]\n" +
				"    dif112b: expected [dif112bexp], but was [dif112bact]\n" +
				"  Unexpected row: {une111a=une111av, une111b=une111bv, une111c=une111cv}\n" +
				"  Unexpected row: {une112a=une112av, une112b=une112bv, une112c=une112cv}\n" +
				"Found differences for table [p1]/t12:\n" +
				"  Missing row: {mis121a=mis121av, mis121b=mis121bv, mis121c=mis121cv}\n" +
				"  Different row: {dif121a=dif121av, dif121b=dif121bv, dif121c=dif121cv}\n" +
				"   Best matching differences: \n" +
				"    dif121a: expected [dif121aexp], but was [dif121aact]\n" +
				"  Unexpected row: {une121a=une121av, une121b=une121bv, une121c=une121cv}\n" +
				"Found differences for table [p2]/t21:\n" +
				"  Missing row: {mis211a=mis211av, mis211b=mis211bv, mis211c=mis211cv}\n" +
				"  Different row: {dif211a=dif211av, dif211b=dif211bv, dif211c=dif211cv}\n" +
				"   Best matching differences: \n" +
				"    dif211a: expected [dif211aexp], but was [dif211aact]\n" +
				"  Unexpected row: {une211a=une211av, une211b=une211bv, une211c=une211cv}\n" +
				"Found differences for table t31:\n" +
				"  Missing row: {mis311a=mis311av}\n";
		assertEquals(expected, report);
	}

	@Test
	public void dataTypes() {
		Map<String, Map<String, Map<String, List<?>>>> differences = new LinkedHashMap<>();
		Map<String, Object> row = createRow("null", null, "boolean", true,
				"byte", (byte) 123, "short", (short) 12345, "integer", 1234567890, "long", 12345678901L,
				"float", (float) 123.45, "double", 123.4567, "bigdecimal", new BigDecimal("123456.789"),
				"date", new Date(DateTime.parse("2015-12-31").getMillis()),
				"time", new Time(DateTime.parse("1970-01-01T12:34:56").getMillis()),
				"timestamp", new Timestamp(DateTime.parse("2015-12-31T12:34:56.123").getMillis()),
				"bytes", "bytes1".getBytes());
		differences.put("p1", createProfile(
				"t11", createTable(
						Arrays.asList(row),
						Arrays.asList(
								createRow(
										EXPECTED, row,
										DIFFERENCES,
										Arrays.asList(
												createRow(COLUMN, "null", EXPECTED, null, ACTUAL, null),
												createRow(COLUMN, "boolean", EXPECTED, true, ACTUAL, false),
												createRow(COLUMN, "byte", EXPECTED, (byte) 123, ACTUAL, (byte) 124),
												createRow(COLUMN, "short", EXPECTED, (short) 12345, ACTUAL, (short) 12346),
												createRow(COLUMN, "integer", EXPECTED, 1234567890, ACTUAL, 1234567891),
												createRow(COLUMN, "long", EXPECTED, 12345678901L, ACTUAL, 12345678902L),
												createRow(COLUMN, "float", EXPECTED, (float) 123.45, ACTUAL, (float) 123.46),
												createRow(COLUMN, "double", EXPECTED, 123.4567, ACTUAL, 123.4568),
												createRow(COLUMN, "bigdecimal", EXPECTED, new BigDecimal("123456.789"),
														ACTUAL, new BigDecimal("123456.781")),
												createRow(COLUMN, "date",
														EXPECTED, new Date(DateTime.parse("2015-12-31").getMillis()),
														ACTUAL, new Date(DateTime.parse("2015-12-30").getMillis())),
												createRow(COLUMN, "time",
														EXPECTED, new Time(DateTime.parse("1970-01-01T12:34:56").getMillis()),
														ACTUAL, new Time(DateTime.parse("1970-01-01T12:34:57").getMillis())),
												createRow(COLUMN, "timestamp",
														EXPECTED, new Timestamp(DateTime.parse("2015-12-31T12:34:56.123").getMillis()),
														ACTUAL, new Timestamp(DateTime.parse("2015-12-31T12:34:56.124").getMillis())),
												createRow(COLUMN, "bytes", EXPECTED, "bytes1".getBytes(),
														ACTUAL, "bytes2".getBytes())
										))
						), Arrays.asList(row))
		));

		String report = Report.report(differences);

		String expected = "Differences found between the expected data set and actual database content.\n" +
				"Found differences for table [p1]/t11:\n" +
				"  Missing row: {null=null, boolean=true, byte=123, short=12345, integer=1234567890, long=12345678901, float=123.45, double=123.4567, bigdecimal=123456.789, date=2015-12-31, time=12:34:56, timestamp=2015-12-31T12:34:56.123, bytes=Ynl0ZXMx}\n" +
				"  Different row: {null=null, boolean=true, byte=123, short=12345, integer=1234567890, long=12345678901, float=123.45, double=123.4567, bigdecimal=123456.789, date=2015-12-31, time=12:34:56, timestamp=2015-12-31T12:34:56.123, bytes=Ynl0ZXMx}\n" +
				"   Best matching differences: \n" +
				"    null: expected [null], but was [null]\n" +
				"    boolean: expected [true], but was [false]\n" +
				"    byte: expected [123], but was [124]\n" +
				"    short: expected [12345], but was [12346]\n" +
				"    integer: expected [1234567890], but was [1234567891]\n" +
				"    long: expected [12345678901], but was [12345678902]\n" +
				"    float: expected [123.45], but was [123.46]\n" +
				"    double: expected [123.4567], but was [123.4568]\n" +
				"    bigdecimal: expected [123456.789], but was [123456.781]\n" +
				"    date: expected [2015-12-31], but was [2015-12-30]\n" +
				"    time: expected [12:34:56], but was [12:34:57]\n" +
				"    timestamp: expected [2015-12-31T12:34:56.123], but was [2015-12-31T12:34:56.124]\n" +
				"    bytes: expected [Ynl0ZXMx], but was [Ynl0ZXMy]\n" +
				"  Unexpected row: {null=null, boolean=true, byte=123, short=12345, integer=1234567890, long=12345678901, float=123.45, double=123.4567, bigdecimal=123456.789, date=2015-12-31, time=12:34:56, timestamp=2015-12-31T12:34:56.123, bytes=Ynl0ZXMx}\n";
		assertEquals(expected, report);
	}
}
