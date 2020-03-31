package unit.internal;

import net.sf.lightair.internal.Compare;
import net.sf.lightair.internal.Keywords;
import org.joda.time.DateTime;
import org.junit.Test;
import unit.internal.db.InsertTest;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CompareTest implements Keywords {

	public static Map<String, Object> createRow(Object... data) {
		assertTrue("Data in pairs", data.length % 2 == 0);
		Map<String, Object> row = new LinkedHashMap<>();
		for (int i = 0; i < data.length; i = i + 2) {
			row.put((String) data[i], data[i + 1]);
		}
		return Collections.unmodifiableMap(row);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static Map<String, List<Map<String, Object>>> createTables(Object... data) {
		assertTrue("Data in pairs", data.length % 2 == 0);
		Map<String, List<Map<String, Object>>> tables = new LinkedHashMap<>();
		for (int i = 0; i < data.length; i = i + 2) {
			tables.put((String) data[i], (List) data[i + 1]);
		}
		return Collections.unmodifiableMap(tables);
	}

	private static Map<String, String> createProfileProperties(int limit) {
		Map<String, String> profileProperties = new HashMap<>();
		profileProperties.put(TIME_DIFFERENCE_LIMIT_MILLIS, String.valueOf(limit));
		return Collections.unmodifiableMap(profileProperties);
	}

	@Test
	public void multipleProfilesAndTablesMatch() {
		Map<String, List<Map<String, Object>>> expectedDatasets = new LinkedHashMap<>();
		expectedDatasets.put("p1", Arrays.asList(
				InsertTest.createRow("t11", "t11a", "1231101", "t11b", "v11b01"),
				InsertTest.createRow("t11", "t11a", "1231102", "t11b", "v11b02"),
				InsertTest.createRow("t12", "t12a", "1231201", "t12b", "v12b01")
		));
		expectedDatasets.put("p2", Arrays.asList(
				InsertTest.createRow("t21", "t21a", "1232101", "t21b", "v21b01"),
				InsertTest.createRow("t21", "t21a", "1232102", "t21b", "v21b02")
		));

		Map<String, Map<String, List<Map<String, Object>>>> actualDatasets = new LinkedHashMap<>();
		actualDatasets.put("p1",
				createTables(
						"t11", Arrays.asList(
								createRow("t11a", "1231101", "t11b", "v11b01"),
								createRow("t11a", "1231102", "t11b", "v11b02")
						),
						"t12", Arrays.asList(
								createRow("t12a", "1231201", "t12b", "v12b01")
						)
				));
		actualDatasets.put("p2",
				createTables(
						"t21", Arrays.asList(
								createRow("t21a", "1232101", "t21b", "v21b01"),
								createRow("t21a", "1232102", "t21b", "v21b02")
						)
				));

		Map<String, Map<String, Map<String, List<?>>>> result =
				Compare.compare(createProfileProperties(0), expectedDatasets, actualDatasets);

		String expected = "{p1={t11={MISSING=[],\n" +
				" DIFFERENT=[],\n" +
				" UNEXPECTED=[]},\n" +
				" t12={MISSING=[],\n" +
				" DIFFERENT=[],\n" +
				" UNEXPECTED=[]}},\n" +
				" p2={t21={MISSING=[],\n" +
				" DIFFERENT=[],\n" +
				" UNEXPECTED=[]}}}";
		assertEquals(expected, result.toString().replace(", ", ",\n "));
	}

	@Test
	public void multipleProfilesAndTablesDiffs() {
		Map<String, List<Map<String, Object>>> expectedDatasets = new LinkedHashMap<>();
		expectedDatasets.put("p1", Arrays.asList(
				InsertTest.createRow("t11", "t11a", "1231101", "t11b", "v11b01"),
				InsertTest.createRow("t11", "t11a", "1231102exp", "t11b", "v11b02exp", "t11c", "v11c02"),
				InsertTest.createRow("t11", "t11a", "1231103mis", "t11b", "v11b03mis"),
				InsertTest.createRow("t11", "t11a", "1231104", "t11b", "v11b04"),
				InsertTest.createRow("t12", "t12a", "1231201", "t12b", "v12b01")
		));
		expectedDatasets.put("p2", Arrays.asList(
				InsertTest.createRow("t21", "t21a", "1232101", "t21b", "v21b01exp"),
				InsertTest.createRow("t21", "t21a", "1232102", "t21b", "v21b02")
		));

		Map<String, Map<String, List<Map<String, Object>>>> actualDatasets = new LinkedHashMap<>();
		actualDatasets.put("p1",
				createTables(
						"t11", Arrays.asList(
								createRow("t11a", "1231101", "t11b", "v11b01"),
								createRow("t11b", "v11b02act", "t11a", "1231102act", "t11c", "v11c02"),
								createRow("t11a", "1231104", "t11b", "v11b04")
						),
						"t12", Arrays.asList(
								createRow("t12a", "1231201", "t12b", "v12b01"),
								createRow("t12a", "1231202une", "t12b", "v12b02une")
						)
				));
		actualDatasets.put("p2",
				createTables(
						"t21", Arrays.asList(
								createRow("t21a", "1232102", "t21b", "v21b02"),
								createRow("t21a", "1232101", "t21b", "v21b01act")
						)
				));

		Map<String, Map<String, Map<String, List<?>>>> result =
				Compare.compare(createProfileProperties(0), expectedDatasets, actualDatasets);

		String expected = "{p1={t11={MISSING=[{t11a=1231103mis, t11b=v11b03mis}],\n" +
				" DIFFERENT=[{EXPECTED={t11a=1231102exp, t11b=v11b02exp, t11c=v11c02},\n" +
				" DIFFERENCES=[{COLUMN=t11a, EXPECTED=1231102exp, ACTUAL=1231102act},\n" +
				" {COLUMN=t11b, EXPECTED=v11b02exp, ACTUAL=v11b02act}]}],\n" +
				" UNEXPECTED=[]},\n" +
				" t12={MISSING=[],\n" +
				" DIFFERENT=[],\n" +
				" UNEXPECTED=[{t12a=1231202une, t12b=v12b02une}]}},\n" +
				" p2={t21={MISSING=[],\n" +
				" DIFFERENT=[{EXPECTED={t21a=1232101, t21b=v21b01exp},\n" +
				" DIFFERENCES=[{COLUMN=t21b, EXPECTED=v21b01exp, ACTUAL=v21b01act}]}],\n" +
				" UNEXPECTED=[]}}}";
		assertEquals(expected, result.toString()
				.replace("}, ", "},\n ")
				.replace("], ", "],\n ")
		);
	}

	@Test
	public void rowsWithIdenticalData() {
		Map<String, List<Map<String, Object>>> expectedDatasets = new LinkedHashMap<>();
		expectedDatasets.put("p1", Arrays.asList(
				InsertTest.createRow("t1", "a", "a1", "b", "b1"),
				InsertTest.createRow("t1", "a", "a1", "b", "b1")
		));

		Map<String, Map<String, List<Map<String, Object>>>> actualDatasets = new LinkedHashMap<>();
		actualDatasets.put("p1",
				createTables(
						"t1", Arrays.asList(
								createRow("a", "a1", "b", "b1"),
								createRow("a", "a1", "b", "b1")
						)
				));

		Map<String, Map<String, Map<String, List<?>>>> result =
				Compare.compare(createProfileProperties(0), expectedDatasets, actualDatasets);

		String expected = "{p1={t1={MISSING=[],\n" +
				" DIFFERENT=[],\n" +
				" UNEXPECTED=[]}}}";
		assertEquals(expected, result.toString()
				.replace("}, ", "},\n ")
				.replace("], ", "],\n ")
		);
	}

	@Test
	public void rowMatchingOrder() {
		Map<String, List<Map<String, Object>>> expectedDatasets = new LinkedHashMap<>();
		expectedDatasets.put("p1", Arrays.asList(
				InsertTest.createRow("t1", "a", "a4"),
				InsertTest.createRow("t1", "a", "a3exp", "b", "b3exp"),
				InsertTest.createRow("t1", "a", "a2", "b", "b2exp", "c", "c2exp"),
				InsertTest.createRow("t1", "a", "a1", "b", "b1", "c", "c1", "d", "d1")
		));

		Map<String, Map<String, List<Map<String, Object>>>> actualDatasets = new LinkedHashMap<>();
		actualDatasets.put("p1",
				createTables(
						"t1", Arrays.asList(
								createRow("a", "a1", "b", "b1", "c", "c1", "d", "d1"),
								createRow("a", "a2", "b", "b2act", "c", "c2act", "d", "d2"),
								createRow("a", "a3act", "b", "b3act", "c", "c3", "d", "d3"),
								createRow("a", "a4", "b", "b4", "c", "c4", "d", "d4")
						)
				));

		Map<String, Map<String, Map<String, List<?>>>> result =
				Compare.compare(createProfileProperties(0), expectedDatasets, actualDatasets);

		String expected = "{p1={t1={MISSING=[],\n" +
				" DIFFERENT=[{EXPECTED={a=a2, b=b2exp, c=c2exp},\n" +
				" DIFFERENCES=[{COLUMN=b, EXPECTED=b2exp, ACTUAL=b2act},\n" +
				" {COLUMN=c, EXPECTED=c2exp, ACTUAL=c2act}]},\n" +
				" {EXPECTED={a=a3exp, b=b3exp},\n" +
				" DIFFERENCES=[{COLUMN=a, EXPECTED=a3exp, ACTUAL=a3act},\n" +
				" {COLUMN=b, EXPECTED=b3exp, ACTUAL=b3act}]}],\n" +
				" UNEXPECTED=[]}}}";
		assertEquals(expected, result.toString()
				.replace("}, ", "},\n ")
				.replace("], ", "],\n ")
		);
	}

	@Test
	public void nulls() {
		Map<String, List<Map<String, Object>>> expectedDatasets = new LinkedHashMap<>();
		expectedDatasets.put("p1", Arrays.asList(
				InsertTest.createRow("t1", "a", "a1", "b", null),
				InsertTest.createRow("t1", "a", "a2", "b", "b2"),
				InsertTest.createRow("t1", "a", "a3", "b", null),
				InsertTest.createRow("t1", "a", "a4", "b", null),
				InsertTest.createRow("t1", "a", "a5", "b", "bytes5".getBytes())
		));

		Map<String, Map<String, List<Map<String, Object>>>> actualDatasets = new LinkedHashMap<>();
		actualDatasets.put("p1",
				createTables(
						"t1", Arrays.asList(
								createRow("a", "a1", "b", "b1"),
								createRow("a", "a2", "b", null),
								createRow("a", "a3", "b", null),
								createRow("a", "a4", "b", "bytes4".getBytes()),
								createRow("a", "a5", "b", null)
						)
				));

		Map<String, Map<String, Map<String, List<?>>>> result =
				Compare.compare(createProfileProperties(0), expectedDatasets, actualDatasets);

		String expected = "{p1={t1={MISSING=[],\n" +
				" DIFFERENT=[{EXPECTED={a=a1, b=null},\n" +
				" DIFFERENCES=[{COLUMN=b, EXPECTED=null, ACTUAL=b1}]},\n" +
				" {EXPECTED={a=a2, b=b2},\n" +
				" DIFFERENCES=[{COLUMN=b, EXPECTED=b2, ACTUAL=null}]},\n" +
				" {EXPECTED={a=a4, b=null},\n" +
				" DIFFERENCES=[{COLUMN=b, EXPECTED=null, ACTUAL=BYTEARRAY}]},\n" +
				" {EXPECTED={a=a5, b=BYTEARRAY},\n" +
				" DIFFERENCES=[{COLUMN=b, EXPECTED=BYTEARRAY, ACTUAL=null}]}],\n" +
				" UNEXPECTED=[]}}}";
		assertEquals(expected, result.toString()
				.replace("}, ", "},\n ")
				.replace("], ", "],\n ")
				.replaceAll("\\[B@[^,}]+", "BYTEARRAY")
		);
	}

	@Test
	public void dataTypesMatching() {
		Map<String, List<Map<String, Object>>> expectedDatasets = new LinkedHashMap<>();
		expectedDatasets.put("p1", Arrays.asList(
				InsertTest.createRow("t1", "booltrue", Boolean.TRUE, "boolfalse", Boolean.FALSE,
						"byte", (byte) 123, "short", (short) 12345, "integer", 1234567890, "long", 12345678901L,
						"float", (float) 123.45, "double", 123.4567, "bigdecimal", new BigDecimal("123456.789"),
						"date", new Date(DateTime.parse("2015-12-31").getMillis()),
						"time", new Time(DateTime.parse("1970-01-01T12:34:56").getMillis()),
						"timestamp", new Timestamp(DateTime.parse("2015-12-31T12:34:56.123").getMillis()),
						"bytes", "bytes1".getBytes(),
						"clob", "clob1",
						"blob", "blob1".getBytes(),
						"array_string", new String[]{"value 1", "value 2", "", " ", null, "value 3"},
						"array_string_empty", new String[0],
						"array_integer", new Integer[]{2345, 3456, null, 4567},
						"array_integer_empty", new Integer[0],
						"array_long", new Long[]{2345678901L, 2345678902L, null, 2345678903L},
						"array_long_empty", new Long[0]
				)));

		Map<String, Map<String, List<Map<String, Object>>>> actualDatasets = new LinkedHashMap<>();
		actualDatasets.put("p1",
				createTables(
						"t1", Arrays.asList(
								createRow("booltrue", Boolean.TRUE, "boolfalse", Boolean.FALSE,
										"byte", (byte) 123, "short", (short) 12345, "integer", 1234567890, "long", 12345678901L,
										"float", (float) 123.45, "double", 123.4567, "bigdecimal", new BigDecimal("123456.78900"),
										"date", new Date(DateTime.parse("2015-12-31").getMillis()),
										"time", new Time(DateTime.parse("1970-01-01T12:34:56").getMillis()),
										"timestamp", new Timestamp(DateTime.parse("2015-12-31T12:34:56.123").getMillis()),
										"bytes", "bytes1".getBytes(),
										"clob", "clob1",
										"blob", "blob1".getBytes(),
										"array_string", new String[]{"value 1", "value 2", "", " ", null, "value 3"},
										"array_string_empty", new String[0],
										"array_integer", new Integer[]{2345, 3456, null, 4567},
										"array_integer_empty", new Integer[0],
										"array_long", new Long[]{2345678901L, 2345678902L, null, 2345678903L},
										"array_long_empty", new Long[0]
								))));

		Map<String, Map<String, Map<String, List<?>>>> result =
				Compare.compare(createProfileProperties(0), expectedDatasets, actualDatasets);

		String expected = "{p1={t1={MISSING=[],\n" +
				" DIFFERENT=[],\n" +
				" UNEXPECTED=[]}}}";
		assertEquals(expected, result.toString()
				.replace("}, ", "},\n ")
				.replace("], ", "],\n ")
		);
	}

	@Test
	public void dataTypesDiffs() {
		Map<String, List<Map<String, Object>>> expectedDatasets = new LinkedHashMap<>();
		expectedDatasets.put("p1", Arrays.asList(
				InsertTest.createRow("t1", "booltrue", Boolean.TRUE, "boolfalse", Boolean.FALSE,
						"byte", (byte) 123, "short", (short) 12345, "integer", 1234567890, "long", 12345678901L,
						"float", (float) 123.45, "double", 123.4567, "bigdecimal", new BigDecimal("123456.789"),
						"date", new Date(DateTime.parse("2015-12-31").getMillis()),
						"time", new Time(DateTime.parse("1970-01-01T12:34:56").getMillis()),
						"timestamp", new Timestamp(DateTime.parse("2015-12-31T12:34:56.123").getMillis()),
						"bytes", "bytes1".getBytes(),
						"clob", "clob1",
						"blob", "blob1".getBytes(),
						"array_string", new String[]{"value 1", "value 2a", "", " ", null, "value 3"},
						"array_string_length", new String[]{"value 1", "value 2", "value 3"},
						"array_integer", new Integer[]{2345, 3456, null, 4567},
						"array_integer_length", new Integer[]{2345, 3456, 4567},
						"array_long", new Long[]{2345678901L, 2345678902L, null, 2345678903L},
						"array_long_length", new Long[]{2345678901L, 2345678902L, 2345678903L}
				)));

		Map<String, Map<String, List<Map<String, Object>>>> actualDatasets = new LinkedHashMap<>();
		actualDatasets.put("p1",
				createTables(
						"t1", Arrays.asList(
								createRow("booltrue", Boolean.FALSE, "boolfalse", Boolean.TRUE,
										"byte", (byte) 124, "short", (short) 12346, "integer", 1234567891, "long", 12345678902L,
										"float", (float) 123.46, "double", 123.4568, "bigdecimal", new BigDecimal("123456.781"),
										"date", new Date(DateTime.parse("2015-12-30").getMillis()),
										"time", new Time(DateTime.parse("1970-01-01T12:34:57").getMillis()),
										"timestamp", new Timestamp(DateTime.parse("2015-12-31T12:34:56.124").getMillis()),
										"bytes", "bytes2".getBytes(),
										"clob", "clob2",
										"blob", "blob2".getBytes(),
										"array_string", new String[]{"value 1", "value 2b", "", " ", null, "value 3"},
										"array_string_length", new String[]{"value 1", "value 2"},
										"array_integer", new Integer[]{2345, 3459, null, 4567},
										"array_integer_length", new Integer[]{2345, 3457},
										"array_long", new Long[]{2345678901L, 2345678909L, null, 2345678903L},
										"array_long_length", new Long[]{2345678901L, 2345678902L}
								))));

		Map<String, Map<String, Map<String, List<?>>>> result =
				Compare.compare(createProfileProperties(0), expectedDatasets, actualDatasets);

		String expected = "{p1={t1={MISSING=[],\n"
				+ " DIFFERENT=[{EXPECTED={booltrue=true, boolfalse=false,"
				+ " byte=123, short=12345, integer=1234567890, long=12345678901,"
				+ " float=123.45, double=123.4567, bigdecimal=123456.789,"
				+ " date=2015-12-31, time=12:34:56, timestamp=2015-12-31 12:34:56.123,"
				+ " bytes=BYTEARRAY, clob=clob1, blob=BYTEARRAY,"
				+ " array_string=[Ljava.lang.String;REPLACED, array_string_length=[Ljava.lang.String;REPLACED,"
				+ " array_integer=[Ljava.lang.Integer;REPLACED, array_integer_length=[Ljava.lang.Integer;REPLACED,"
				+ " array_long=[Ljava.lang.Long;REPLACED, array_long_length=[Ljava.lang.Long;REPLACED},\n"
				+ " DIFFERENCES=[{COLUMN=booltrue, EXPECTED=true, ACTUAL=false},\n"
				+ " {COLUMN=boolfalse, EXPECTED=false, ACTUAL=true},\n"
				+ " {COLUMN=byte, EXPECTED=123, ACTUAL=124},\n"
				+ " {COLUMN=short, EXPECTED=12345, ACTUAL=12346},\n"
				+ " {COLUMN=integer, EXPECTED=1234567890, ACTUAL=1234567891},\n"
				+ " {COLUMN=long, EXPECTED=12345678901, ACTUAL=12345678902},\n"
				+ " {COLUMN=float, EXPECTED=123.45, ACTUAL=123.46},\n"
				+ " {COLUMN=double, EXPECTED=123.4567, ACTUAL=123.4568},\n"
				+ " {COLUMN=bigdecimal, EXPECTED=123456.789, ACTUAL=123456.781},\n"
				+ " {COLUMN=date, EXPECTED=2015-12-31, ACTUAL=2015-12-30},\n"
				+ " {COLUMN=time, EXPECTED=12:34:56, ACTUAL=12:34:57},\n"
				+ " {COLUMN=timestamp, EXPECTED=2015-12-31 12:34:56.123, ACTUAL=2015-12-31 12:34:56.124},\n"
				+ " {COLUMN=bytes, EXPECTED=BYTEARRAY, ACTUAL=BYTEARRAY},\n"
				+ " {COLUMN=clob, EXPECTED=clob1, ACTUAL=clob2},\n"
				+ " {COLUMN=blob, EXPECTED=BYTEARRAY, ACTUAL=BYTEARRAY},\n"
				+ " {COLUMN=array_string, EXPECTED=[Ljava.lang.String;REPLACED, ACTUAL=[Ljava.lang.String;REPLACED},\n"
				+ " {COLUMN=array_string_length, EXPECTED=[Ljava.lang.String;REPLACED, ACTUAL=[Ljava.lang.String;REPLACED},\n"
				+ " {COLUMN=array_integer, EXPECTED=[Ljava.lang.Integer;REPLACED, ACTUAL=[Ljava.lang.Integer;REPLACED},\n"
				+ " {COLUMN=array_integer_length, EXPECTED=[Ljava.lang.Integer;REPLACED, ACTUAL=[Ljava.lang.Integer;REPLACED},\n"
				+ " {COLUMN=array_long, EXPECTED=[Ljava.lang.Long;REPLACED, ACTUAL=[Ljava.lang.Long;REPLACED},\n"
				+ " {COLUMN=array_long_length, EXPECTED=[Ljava.lang.Long;REPLACED, ACTUAL=[Ljava.lang.Long;REPLACED}"
				+ "]}],\n"
				+ " UNEXPECTED=[]}}}";
		assertEquals(expected, result.toString()
				.replace("}, ", "},\n ")
				.replace("], ", "],\n ")
				.replaceAll("\\[B@[^,}]+", "BYTEARRAY")
				.replaceAll("@[0-9a-fA-F]+", "REPLACED")
		);
	}

	@Test
	public void emptyTable() {
		Map<String, List<Map<String, Object>>> expectedDatasets = new LinkedHashMap<>();
		expectedDatasets.put("p1", Arrays.asList(
				InsertTest.createRow("empty_match"),
				InsertTest.createRow("empty_nonmatch"),
				InsertTest.createRow("empty_match"),
				InsertTest.createRow("empty_nonmatch"),
				InsertTest.createRow("non_empty"),
				InsertTest.createRow("non_empty", "c2", "v2"),
				InsertTest.createRow("non_empty")
		));

		Map<String, Map<String, List<Map<String, Object>>>> actualDatasets = new LinkedHashMap<>();
		actualDatasets.put("p1",
				createTables(
						"empty_match", Collections.emptyList(),
						"empty_nonmatch", Arrays.asList(createRow("c1", "v1")),
						"non_empty", Arrays.asList(createRow("c2", "v2"))
				));

		Map<String, Map<String, Map<String, List<?>>>> result =
				Compare.compare(createProfileProperties(0), expectedDatasets, actualDatasets);

		String expected = "{p1={empty_match={MISSING=[],\n" +
				" DIFFERENT=[],\n" +
				" UNEXPECTED=[]},\n" +
				" empty_nonmatch={MISSING=[],\n" +
				" DIFFERENT=[],\n" +
				" UNEXPECTED=[{c1=v1}]},\n" +
				" non_empty={MISSING=[],\n" +
				" DIFFERENT=[],\n" +
				" UNEXPECTED=[]}}}";
		assertEquals(expected, result.toString()
				.replace("}, ", "},\n ")
				.replace("], ", "],\n ")
		);
	}

	@Test
	public void tokenAny() {
		Map<String, List<Map<String, Object>>> expectedDatasets = new LinkedHashMap<>();
		expectedDatasets.put("p1", Arrays.asList(
				InsertTest.createRow("t1", "anystring", "@any", "anyint", "@any", "anynull", "@any")
		));

		Map<String, Map<String, List<Map<String, Object>>>> actualDatasets = new LinkedHashMap<>();
		actualDatasets.put("p1",
				createTables(
						"t1", Arrays.asList(
								createRow("anystring", "some string", "anyint", 123, "anynull", null)
						)
				));

		Map<String, Map<String, Map<String, List<?>>>> result =
				Compare.compare(createProfileProperties(0), expectedDatasets, actualDatasets);

		String expected = "{p1={t1={MISSING=[],\n" +
				" DIFFERENT=[{EXPECTED={anystring=@any, anyint=@any, anynull=@any},\n" +
				" DIFFERENCES=[{COLUMN=anynull, EXPECTED=@any, ACTUAL=null}]}],\n" +
				" UNEXPECTED=[]}}}";
		assertEquals(expected, result.toString()
				.replace("}, ", "},\n ")
				.replace("], ", "],\n ")
		);
	}

	@Test
	public void timeDifferenceLimit() {
		Map<String, List<Map<String, Object>>> expectedDatasets = new LinkedHashMap<>();
		expectedDatasets.put("p1", Arrays.asList(
				InsertTest.createRow("t1",
						"time_match", new Time(DateTime.parse("1970-01-01T12:34:56").getMillis()),
						"timestamp_match", new Timestamp(DateTime.parse("2015-12-31T12:34:56.123").getMillis()),
						"time_nonmatch", new Time(DateTime.parse("1970-01-01T12:34:56").getMillis()),
						"timestamp_nonmatch", new Timestamp(DateTime.parse("2015-12-31T12:34:56.123").getMillis())
				)));

		Map<String, Map<String, List<Map<String, Object>>>> actualDatasets = new LinkedHashMap<>();
		actualDatasets.put("p1",
				createTables(
						"t1", Arrays.asList(
								createRow(
										"time_match", new Time(DateTime.parse("1970-01-01T12:35:01").getMillis()),
										"timestamp_match", new Timestamp(DateTime.parse("2015-12-31T12:35:01.123").getMillis()),
										"time_nonmatch", new Time(DateTime.parse("1970-01-01T12:35:02").getMillis()),
										"timestamp_nonmatch", new Timestamp(DateTime.parse("2015-12-31T12:35:01.124").getMillis())
								))));

		Map<String, Map<String, Map<String, List<?>>>> result =
				Compare.compare(createProfileProperties(5_000), expectedDatasets, actualDatasets);

		String expected = "{p1={t1={MISSING=[],\n" +
				" DIFFERENT=[{EXPECTED={time_match=12:34:56, timestamp_match=2015-12-31 12:34:56.123, time_nonmatch=12:34:56, timestamp_nonmatch=2015-12-31 12:34:56.123},\n" +
				" DIFFERENCES=[{COLUMN=time_nonmatch, EXPECTED=12:34:56, ACTUAL=12:35:02},\n" +
				" {COLUMN=timestamp_nonmatch, EXPECTED=2015-12-31 12:34:56.123, ACTUAL=2015-12-31 12:35:01.124}]}],\n" +
				" UNEXPECTED=[]}}}";
		assertEquals(expected, result.toString()
				.replace("}, ", "},\n ")
				.replace("], ", "],\n ")
		);
	}

	@Test
	public void variables() {
		Map<String, List<Map<String, Object>>> expectedDatasets = new LinkedHashMap<>();
		expectedDatasets.put("p1", Arrays.asList(
				InsertTest.createRow("t1", "c1", "$var1", "c2", "$var2", "c3", "v131"),
				InsertTest.createRow("t1", "c1", "$var1", "c2", "$var2", "c3", "v132"),
				InsertTest.createRow("t1", "c1", "v113", "c2", 7123, "c3", "v133"),
				InsertTest.createRow("t1", "c1", "$var1", "c2", "$var2", "c3", "v134"),
				InsertTest.createRow("t1", "c1", "$var1", "c2", "$var2", "c3", "v135")
		));

		Map<String, Map<String, List<Map<String, Object>>>> actualDatasets = new LinkedHashMap<>();
		actualDatasets.put("p1",
				createTables(
						"t1", Arrays.asList(
								createRow("c1", "var1 value", "c2", 7482, "c3", "v131"),
								createRow("c1", "var1 value", "c2", 7482, "c3", "v132"),
								createRow("c1", "v113", "c2", 7123, "c3", "v133"),
								createRow("c1", "v114", "c2", 7124, "c3", "v134"),
								createRow("c1", "var1 value", "c2", 7482, "c3", "v135")
						)
				));

		Map<String, Map<String, Map<String, List<?>>>> result =
				Compare.compare(createProfileProperties(0), expectedDatasets, actualDatasets);

		String expected = "{p1={t1={MISSING=[],\n" +
				" DIFFERENT=[{EXPECTED={c1=$var1, c2=$var2, c3=v134},\n" +
				" DIFFERENCES=[{COLUMN=c1, EXPECTED=var1 value, ACTUAL=v114},\n" +
				" {COLUMN=c2, EXPECTED=7482, ACTUAL=7124}]}],\n" +
				" UNEXPECTED=[]}}}";
		assertEquals(expected, result.toString()
				.replace("}, ", "},\n ")
				.replace("], ", "],\n ")
		);
	}

	@Test
	public void variablesInfluenceBestRowMatch() {
		Map<String, List<Map<String, Object>>> expectedDatasets = new LinkedHashMap<>();
		expectedDatasets.put("p1", Arrays.asList(
				InsertTest.createRow("t1", "c1", "$var1", "c3", "v131"), // define var1
				InsertTest.createRow("t1", "c1", "$var2", "c3", "v132"), // define var2
				InsertTest.createRow("t1", "c1", "$var1", "c3", "same"), // match var1
				InsertTest.createRow("t1", "c1", "$var2", "c3", "same")  // match var2
		));

		Map<String, Map<String, List<Map<String, Object>>>> actualDatasets = new LinkedHashMap<>();
		actualDatasets.put("p1",
				createTables(
						"t1", Arrays.asList(
								createRow("c1", "var1 value", "c3", "v131"),
								createRow("c1", "var2 value", "c3", "v132"),
								// rows switched and only matched by var value:
								createRow("c1", "var2 value", "c3", "same"),
								createRow("c1", "var1 value", "c3", "same")
						)
				));

		Map<String, Map<String, Map<String, List<?>>>> result =
				Compare.compare(createProfileProperties(0), expectedDatasets, actualDatasets);

		String expected = "{p1={t1={MISSING=[],\n" +
				" DIFFERENT=[],\n" +
				" UNEXPECTED=[]}}}";
		assertEquals(expected, result.toString()
				.replace("}, ", "},\n ")
				.replace("], ", "],\n ")
		);
	}
}
