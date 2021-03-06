package unit.internal;

import net.sf.lightair.internal.Convert;
import net.sf.lightair.internal.Keywords;
import net.sf.lightair.internal.auto.Index;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import unit.internal.db.InsertTest;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ConvertTest implements Keywords {

	@Before
	public void setUp() {
		DateTimeUtils.setCurrentMillisFixed(DateTime.parse("2015-12-31T12:34:56.123").getMillis());
	}

	@After
	public void tearDown() {
		DateTimeUtils.setCurrentMillisSystem();
	}

	@Test
	public void profiles() {
		Map<String, Map<String, Map<String, Map<String, Object>>>> structures = new HashMap<>();
		Map<String, Map<String, Map<String, Object>>> profileStructure;
		// profile 1 structure
		profileStructure = new HashMap<>();
		profileStructure.put("t11", InsertTest.createTableStructure(
				"t11a", INTEGER, Types.INTEGER,
				"t11b", STRING, Types.VARCHAR,
				"t11c", STRING, Types.VARCHAR
		));
		profileStructure.put("t12", InsertTest.createTableStructure(
				"t12a", INTEGER, Types.INTEGER,
				"t12b", STRING, Types.VARCHAR
		));
		structures.put("profile1", profileStructure);
		// profile 2 structure
		profileStructure = new HashMap<>();
		profileStructure.put("t21", InsertTest.createTableStructure(
				"t21a", INTEGER, Types.INTEGER,
				"t21b", STRING, Types.VARCHAR
		));
		profileStructure.put("t22", InsertTest.createTableStructure(
				"t22a", INTEGER, Types.INTEGER,
				"t22b", STRING, Types.VARCHAR
		));
		structures.put("profile2", profileStructure);

		Map<String, List<Map<String, Object>>> datasets = new LinkedHashMap<>();
		datasets.put("profile1", Arrays.asList(
				InsertTest.createRow("t11", "t11b", "v11b01", "t11a", "1231101"), // swap order
				InsertTest.createRow("t11", "t11c", "v11c02", "t11b", "v11b02", "t11a", "1231102"), // swap order
				InsertTest.createRow("t12", "t12a", "1231201", "t12b", "v12b01")
		));
		datasets.put("profile2", Arrays.asList(
				InsertTest.createRow("t21", "t21a", "1232101", "t21b", "v21b01"),
				InsertTest.createRow("t21", "t21a", "1232102", "t21b", "v21b02")
		));

		Map<String, List<Map<String, Object>>> result = Convert.convert(structures, null, datasets);

		String expected = "{profile1=[{TABLE=t11,\n" +
				" COLUMNS={t11a=1231101,\n" +
				" t11b=v11b01}},\n" +
				" {TABLE=t11,\n" +
				" COLUMNS={t11a=1231102,\n" +
				" t11b=v11b02,\n" +
				" t11c=v11c02}},\n" +
				" {TABLE=t12,\n" +
				" COLUMNS={t12a=1231201,\n" +
				" t12b=v12b01}}],\n" +
				" profile2=[{TABLE=t21,\n" +
				" COLUMNS={t21a=1232101,\n" +
				" t21b=v21b01}},\n" +
				" {TABLE=t21,\n" +
				" COLUMNS={t21a=1232102,\n" +
				" t21b=v21b02}}]}";
		assertEquals(expected, result.toString().replace(", ", ",\n "));
	}

	@Test
	public void dataTypes() throws IOException {
		Map<String, Map<String, Map<String, Map<String, Object>>>> structures = new HashMap<>();
		Map<String, Map<String, Map<String, Object>>> profileStructure = new HashMap<>();
		profileStructure.put("data_types", InsertTest.createTableStructure(
				"boolean_type", BOOLEAN, Types.BOOLEAN,
				"boolean_as_int", BOOLEAN, Types.BOOLEAN,
				"byte_type", BYTE, Types.TINYINT,
				"byte_as_true", BYTE, Types.TINYINT,
				"byte_as_false", BYTE, Types.TINYINT,
				"short_type", SHORT, Types.SMALLINT,
				"short_as_true", SHORT, Types.SMALLINT,
				"short_as_false", SHORT, Types.SMALLINT,
				"integer_type", INTEGER, Types.INTEGER,
				"integer_as_true", INTEGER, Types.INTEGER,
				"integer_as_false", INTEGER, Types.INTEGER,
				"long_type", LONG, Types.BIGINT,
				"long_as_true", LONG, Types.BIGINT,
				"long_as_false", LONG, Types.BIGINT,
				"float_type", FLOAT, Types.REAL,
				"double_type", DOUBLE, Types.DOUBLE,
				"bigdecimal_type", BIGDECIMAL, Types.DECIMAL,
				"bigdecimal_as_true", BIGDECIMAL, Types.DECIMAL,
				"bigdecimal_as_false", BIGDECIMAL, Types.DECIMAL,
				"date_type", DATE, Types.DATE,
				"time_type", TIME, Types.TIME,
				"timestamp_type", TIMESTAMP, Types.TIMESTAMP,
				"string_type", STRING, Types.VARCHAR,
				"fixed_string_type", FIXED_STRING, Types.CHAR,
				"nstring_type", NSTRING, Types.NVARCHAR,
				"fixed_nstring_type", Keywords.FIXED_NSTRING, Types.NCHAR,
				"bytes_type", BYTES, Types.BINARY,
				"clob_type", CLOB, Types.CLOB,
				"nclob_type", NCLOB, Types.NCLOB,
				"blob_type", BLOB, Types.BLOB,
				"uuid_type", UUID, Types.OTHER,
				"json_type", JSON, Types.OTHER,
				"jsonb_type", JSONB, Types.OTHER,
				"array_varchar_type", ARRAY_STRING, Types.ARRAY,
				"array_integer_type", ARRAY_INTEGER, Types.ARRAY,
				"array_long_type", ARRAY_LONG, Types.ARRAY
		));
		structures.put(DEFAULT_PROFILE, profileStructure);

		Map<String, List<Map<String, Object>>> datasets = new LinkedHashMap<>();
		datasets.put(DEFAULT_PROFILE, Arrays.asList(
				InsertTest.createRow("data_types",
						"boolean_type", "true",
						"boolean_as_int", "1",
						"byte_type", "123",
						"byte_as_true", "truE",
						"byte_as_false", "falsE",
						"short_type", "12345",
						"short_as_true", "truE",
						"short_as_false", "falsE",
						"integer_type", "1234567890",
						"integer_as_true", "truE",
						"integer_as_false", "falsE",
						"long_type", "40234567890",
						"long_as_true", "truE",
						"long_as_false", "falsE",
						"float_type", "1234.56",
						"double_type", "123456.789123",
						"bigdecimal_type", "1234567890123456789.123456789",
						"bigdecimal_as_true", "true",
						"bigdecimal_as_false", "false",
						"date_type", "2016-08-09",
						"time_type", "21:43:59",
						"timestamp_type", "2016-08-09T21:43:59.321",
						"string_type", "string value",
						"fixed_string_type", "string value   ",
						"nstring_type", "nstring value",
						"fixed_nstring_type", "nstring value   ",
						"bytes_type", "Ynl0ZXMgdmFsdWU=",
						"clob_type", "clob value",
						"nclob_type", "nclob value",
						"blob_type", "YmxvYiB2YWx1ZQ==",
						"uuid_type", "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
						"json_type", "{\"key1\":\"value1\"}",
						"jsonb_type", "{\"key2\":\"value2\"}",
						"array_varchar_type", "value 1,value 2,, ,@null,value 3",
						"array_integer_type", "2345,3456,@null,4567",
						"array_long_type", "3456789012,3456789013,@null,3456789014"
				)
		));

		Map<String, List<Map<String, Object>>> result = Convert.convert(structures, null, datasets);

		String expected = "{=[{TABLE=data_types,\n" +
				" COLUMNS={boolean_type=true,\n" +
				" boolean_as_int=true,\n" +
				" byte_type=123,\n" +
				" byte_as_true=1,\n" +
				" byte_as_false=0,\n" +
				" short_type=12345,\n" +
				" short_as_true=1,\n" +
				" short_as_false=0,\n" +
				" integer_type=1234567890,\n" +
				" integer_as_true=1,\n" +
				" integer_as_false=0,\n" +
				" long_type=40234567890,\n" +
				" long_as_true=1,\n" +
				" long_as_false=0,\n" +
				" float_type=1234.56,\n" +
				" double_type=123456.789123,\n" +
				" bigdecimal_type=1234567890123456789.123456789,\n" +
				" bigdecimal_as_true=1,\n" +
				" bigdecimal_as_false=0,\n" +
				" date_type=2016-08-09,\n" +
				" time_type=21:43:59,\n" +
				" timestamp_type=2016-08-09 21:43:59.321,\n" +
				" string_type=string value,\n" +
				" fixed_string_type=string value   ,\n" +
				" nstring_type=nstring value,\n" +
				" fixed_nstring_type=nstring value   ,\n" +
				" bytes_type=REPLACED,\n" +
				" clob_type=clob value,\n" +
				" nclob_type=nclob value,\n" +
				" blob_type=REPLACED,\n" +
				" uuid_type=a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11,\n" +
				" json_type={\"key1\":\"value1\"},\n" +
				" jsonb_type={\"key2\":\"value2\"},\n" +
				" array_varchar_type=[Ljava.lang.String;REPLACED,\n" +
				" array_integer_type=[Ljava.lang.Integer;REPLACED,\n" +
				" array_long_type=[Ljava.lang.Long;REPLACED" +
				"}}]}";
		assertEquals(expected, result.toString()
				.replace(", ", ",\n ")
				.replaceAll("\\[B@[^,}]+", "REPLACED")
				.replaceAll("@[0-9a-fA-F]+", "REPLACED"));

		@SuppressWarnings({"unchecked", "rawtypes"})
		Map<String, Object> dataTypes = (Map) result.get(DEFAULT_PROFILE).get(0).get(COLUMNS);
		assertEquals(Boolean.class, dataTypes.get("boolean_type").getClass());
		assertEquals(Byte.class, dataTypes.get("byte_type").getClass());
		assertEquals(Short.class, dataTypes.get("short_type").getClass());
		assertEquals(Integer.class, dataTypes.get("integer_type").getClass());
		assertEquals(Long.class, dataTypes.get("long_type").getClass());
		assertEquals(Float.class, dataTypes.get("float_type").getClass());
		assertEquals(Double.class, dataTypes.get("double_type").getClass());
		assertEquals(BigDecimal.class, dataTypes.get("bigdecimal_type").getClass());
		assertEquals(Date.class, dataTypes.get("date_type").getClass());
		assertEquals(Time.class, dataTypes.get("time_type").getClass());
		assertEquals(Timestamp.class, dataTypes.get("timestamp_type").getClass());
		assertEquals(String.class, dataTypes.get("string_type").getClass());
		assertEquals(String.class, dataTypes.get("fixed_string_type").getClass());
		assertEquals(String.class, dataTypes.get("nstring_type").getClass());
		assertEquals(String.class, dataTypes.get("fixed_nstring_type").getClass());
		assertEquals(byte[].class, dataTypes.get("bytes_type").getClass());
		assertEquals(String.class, dataTypes.get("clob_type").getClass());
		assertEquals(String.class, dataTypes.get("nclob_type").getClass());
		assertEquals(byte[].class, dataTypes.get("blob_type").getClass());
		assertEquals(String.class, dataTypes.get("uuid_type").getClass());
		assertEquals(String.class, dataTypes.get("json_type").getClass());
		assertEquals(String.class, dataTypes.get("jsonb_type").getClass());
		assertEquals(String[].class, dataTypes.get("array_varchar_type").getClass());
		assertEquals(Integer[].class, dataTypes.get("array_integer_type").getClass());
		assertEquals(Long[].class, dataTypes.get("array_long_type").getClass());

		assertEquals("bytes value", new String((byte[]) dataTypes.get("bytes_type"), StandardCharsets.UTF_8));
		assertEquals("blob value", new String((byte[]) dataTypes.get("blob_type"), StandardCharsets.UTF_8));
		assertArrayEquals("array_varchar_type value", new String[]{"value 1", "value 2", "", " ", null, "value 3"},
				(String[]) dataTypes.get("array_varchar_type"));
		assertArrayEquals("array_integer_type value", new Integer[]{2345, 3456, null, 4567},
				(Integer[]) dataTypes.get("array_integer_type"));
		assertArrayEquals("array_long_type value", new Long[]{3456789012L, 3456789013L, null, 3456789014L},
				(Long[]) dataTypes.get("array_long_type"));
	}


	@Test
	public void emptyArrays() throws IOException {
		Map<String, Map<String, Map<String, Map<String, Object>>>> structures = new HashMap<>();
		Map<String, Map<String, Map<String, Object>>> profileStructure = new HashMap<>();
		profileStructure.put("data_types", InsertTest.createTableStructure(
				"array_string_type", ARRAY_STRING, Types.ARRAY,
				"array_integer_type", ARRAY_INTEGER, Types.ARRAY,
				"array_long_type", ARRAY_LONG, Types.ARRAY
		));
		structures.put(DEFAULT_PROFILE, profileStructure);

		Map<String, List<Map<String, Object>>> datasets = new LinkedHashMap<>();
		datasets.put(DEFAULT_PROFILE, Arrays.asList(
				InsertTest.createRow("data_types",
						"array_string_type", "",
						"array_integer_type", "",
						"array_long_type", ""
				)
		));

		Map<String, List<Map<String, Object>>> result = Convert.convert(structures, null, datasets);

		@SuppressWarnings({"unchecked", "rawtypes"})
		Map<String, Object> dataTypes = (Map) result.get(DEFAULT_PROFILE).get(0).get(COLUMNS);

		assertArrayEquals("array_string_type value", new String[0],
				(String[]) dataTypes.get("array_string_type"));
		assertArrayEquals("array_integer_type value", new Integer[0],
				(Integer[]) dataTypes.get("array_integer_type"));
		assertArrayEquals("array_long_type value", new Long[0],
				(Long[]) dataTypes.get("array_long_type"));
	}

	@Test
	public void tokenNull() {
		Map<String, Map<String, Map<String, Map<String, Object>>>> structures = new HashMap<>();
		Map<String, Map<String, Map<String, Object>>> profileStructure = new HashMap<>();
		profileStructure.put("data_types", InsertTest.createTableStructure(
				"integer_type", INTEGER, Types.INTEGER,
				"string_type", STRING, Types.VARCHAR
		));
		structures.put(DEFAULT_PROFILE, profileStructure);

		Map<String, List<Map<String, Object>>> datasets = new LinkedHashMap<>();
		datasets.put(DEFAULT_PROFILE, Arrays.asList(
				InsertTest.createRow("data_types",
						"integer_type", "@null",
						"string_type", "@null"
				)
		));

		Map<String, List<Map<String, Object>>> result = Convert.convert(structures, null, datasets);

		String expected = "{=[{TABLE=data_types,\n" +
				" COLUMNS={integer_type=null,\n" +
				" string_type=null}}]}";
		assertEquals(expected, result.toString().replace(", ", ",\n "));

		@SuppressWarnings({"unchecked", "rawtypes"})
		Map<String, Object> dataTypes = (Map) result.get(DEFAULT_PROFILE).get(0).get(COLUMNS);
		assertNull("integer value", dataTypes.get("integer_type"));
		assertNull("string value", dataTypes.get("string_type"));
	}

	@Test
	public void tokenDate() {
		Map<String, Map<String, Map<String, Map<String, Object>>>> structures = new HashMap<>();
		Map<String, Map<String, Map<String, Object>>> profileStructure = new HashMap<>();
		profileStructure.put("data_types", InsertTest.createTableStructure(
				"date_type", DATE, Types.DATE,
				"time_type", TIME, Types.TIME,
				"timestamp_type", TIMESTAMP, Types.TIMESTAMP
		));
		structures.put(DEFAULT_PROFILE, profileStructure);

		Map<String, List<Map<String, Object>>> datasets = new LinkedHashMap<>();
		datasets.put(DEFAULT_PROFILE, Arrays.asList(
				InsertTest.createRow("data_types",
						"date_type", "@date",
						"time_type", "@date",
						"timestamp_type", "@date"
				)
		));

		Map<String, List<Map<String, Object>>> result = Convert.convert(structures, null, datasets);

		String expected = "{=[{TABLE=data_types,\n" +
				" COLUMNS={date_type=2015-12-31,\n" +
				" time_type=00:00:00,\n" +
				" timestamp_type=2015-12-31 00:00:00.0}}]}";
		assertEquals(expected, result.toString().replace(", ", ",\n "));

		@SuppressWarnings({"unchecked", "rawtypes"})
		Map<String, Object> dataTypes = (Map) result.get(DEFAULT_PROFILE).get(0).get(COLUMNS);
		// types
		assertEquals(Date.class, dataTypes.get("date_type").getClass());
		assertEquals(Time.class, dataTypes.get("time_type").getClass());
		assertEquals(Timestamp.class, dataTypes.get("timestamp_type").getClass());
		// values
		assertEquals(LocalDate.parse("2015-12-31").toDateMidnight().toDateTime(), new DateTime(dataTypes.get("date_type")));
		assertEquals(new LocalTime("00:00:00").toDateTime(DateTime.parse("1970-01-01")),
				new DateTime(dataTypes.get("time_type")));
		assertEquals(DateTime.parse("2015-12-31T00:00:00.0"), new DateTime(dataTypes.get("timestamp_type")));
	}

	@Test
	public void tokenTime() {
		Map<String, Map<String, Map<String, Map<String, Object>>>> structures = new HashMap<>();
		Map<String, Map<String, Map<String, Object>>> profileStructure = new HashMap<>();
		profileStructure.put("data_types", InsertTest.createTableStructure(
				"date_type", DATE, Types.DATE,
				"time_type", TIME, Types.TIME,
				"timestamp_type", TIMESTAMP, Types.TIMESTAMP
		));
		structures.put(DEFAULT_PROFILE, profileStructure);

		Map<String, List<Map<String, Object>>> datasets = new LinkedHashMap<>();
		datasets.put(DEFAULT_PROFILE, Arrays.asList(
				InsertTest.createRow("data_types",
						"date_type", "@time",
						"time_type", "@time",
						"timestamp_type", "@time"
				)
		));

		Map<String, List<Map<String, Object>>> result = Convert.convert(structures, null, datasets);

		String expected = "{=[{TABLE=data_types,\n" +
				" COLUMNS={date_type=1970-01-01,\n" +
				" time_type=12:34:56,\n" +
				" timestamp_type=1970-01-01 12:34:56.0}}]}";
		assertEquals(expected, result.toString().replace(", ", ",\n "));

		@SuppressWarnings({"unchecked", "rawtypes"})
		Map<String, Object> dataTypes = (Map) result.get(DEFAULT_PROFILE).get(0).get(COLUMNS);
		// types
		assertEquals(Date.class, dataTypes.get("date_type").getClass());
		assertEquals(Time.class, dataTypes.get("time_type").getClass());
		assertEquals(Timestamp.class, dataTypes.get("timestamp_type").getClass());
		// values
		assertEquals(LocalDate.parse("1970-01-01").toDateMidnight().toDateTime(), new DateTime(dataTypes.get("date_type")));
		assertEquals(new LocalTime("12:34:56").toDateTime(DateTime.parse("1970-01-01")),
				new DateTime(dataTypes.get("time_type")));
		assertEquals(DateTime.parse("1970-01-01T12:34:56.0"), new DateTime(dataTypes.get("timestamp_type")));
	}

	@Test
	public void tokenTimestamp() {
		Map<String, Map<String, Map<String, Map<String, Object>>>> structures = new HashMap<>();
		Map<String, Map<String, Map<String, Object>>> profileStructure = new HashMap<>();
		profileStructure.put("data_types", InsertTest.createTableStructure(
				"date_type", DATE, Types.DATE,
				"time_type", TIME, Types.TIME,
				"timestamp_type", TIMESTAMP, Types.TIMESTAMP
		));
		structures.put(DEFAULT_PROFILE, profileStructure);

		Map<String, List<Map<String, Object>>> datasets = new LinkedHashMap<>();
		datasets.put(DEFAULT_PROFILE, Arrays.asList(
				InsertTest.createRow("data_types",
						"date_type", "@timestamp",
						"time_type", "@timestamp",
						"timestamp_type", "@timestamp"
				)
		));

		Map<String, List<Map<String, Object>>> result = Convert.convert(structures, null, datasets);

		String expected = "{=[{TABLE=data_types,\n" +
				" COLUMNS={date_type=2015-12-31,\n" +
				" time_type=12:34:56,\n" +
				" timestamp_type=2015-12-31 12:34:56.123}}]}";
		assertEquals(expected, result.toString().replace(", ", ",\n "));

		@SuppressWarnings({"unchecked", "rawtypes"})
		Map<String, Object> dataTypes = (Map) result.get(DEFAULT_PROFILE).get(0).get(COLUMNS);
		// types
		assertEquals(Date.class, dataTypes.get("date_type").getClass());
		assertEquals(Time.class, dataTypes.get("time_type").getClass());
		assertEquals(Timestamp.class, dataTypes.get("timestamp_type").getClass());
		// values
		assertEquals(LocalDate.parse("2015-12-31").toDateMidnight().toDateTime(), new DateTime(dataTypes.get("date_type")));
		assertEquals(new LocalTime("12:34:56").toDateTime(DateTime.parse("1970-01-01")),
				new DateTime(dataTypes.get("time_type")));
		assertEquals(DateTime.parse("2015-12-31T12:34:56.123"), new DateTime(dataTypes.get("timestamp_type")));
	}

	@Test
	public void duration() {
		Map<String, Map<String, Map<String, Map<String, Object>>>> structures = new HashMap<>();
		Map<String, Map<String, Map<String, Object>>> profileStructure = new HashMap<>();
		profileStructure.put("data_types", InsertTest.createTableStructure(
				"date_type", DATE, Types.DATE,
				"time_type", TIME, Types.TIME,
				"timestamp_type", TIMESTAMP, Types.TIMESTAMP
		));
		structures.put(DEFAULT_PROFILE, profileStructure);

		Map<String, List<Map<String, Object>>> datasets = new LinkedHashMap<>();
		datasets.put(DEFAULT_PROFILE, Arrays.asList(
				InsertTest.createRow("data_types",
						"date_type", "@date+P3D",
						"time_type", "@time+PT3M",
						"timestamp_type", "@timestamp+P3DT3M"
				),
				InsertTest.createRow("data_types",
						"date_type", "@date-P3D",
						"time_type", "@time-PT3M",
						"timestamp_type", "@timestamp-P3DT3M"
				)
		));

		Map<String, List<Map<String, Object>>> result = Convert.convert(structures, null, datasets);

		// now: 2015-12-31 12:34:56.123
		String expected = "{=[{TABLE=data_types,\n" +
				" COLUMNS={date_type=2016-01-03,\n" +
				" time_type=12:37:56,\n" +
				" timestamp_type=2016-01-03 12:37:56.123}},\n" +
				" {TABLE=data_types,\n" +
				" COLUMNS={date_type=2015-12-28,\n" +
				" time_type=12:31:56,\n" +
				" timestamp_type=2015-12-28 12:31:56.123}}]}";
		assertEquals(expected, result.toString().replace(", ", ",\n "));
	}

	public static Map<String, Map<String, Object>> createTableStructure(Object... data) {
		assertTrue("Data by fours", data.length % 4 == 0);
		Map<String, Map<String, Object>> table = new LinkedHashMap<>();
		for (int i = 0; i < data.length; i = i + 4) {
			Map<String, Object> column = new LinkedHashMap<>();
			Object dataType = data[i + 1];
			Object jdbcDataType = data[i + 2];
			Object size = data[i + 3];
			column.put(DATA_TYPE, dataType);
			column.put(JDBC_DATA_TYPE, jdbcDataType);
			column.put(SIZE, size);
			table.put((String) data[i], column);
		}
		return table;
	}

	@Test
	public void tokenAuto() {
		Map<String, Map<String, Map<String, Map<String, Object>>>> structures = new HashMap<>();
		Map<String, Map<String, Map<String, Object>>> profileStructure;
		// profile 1
		profileStructure = new HashMap<>();
		profileStructure.put("t1", createTableStructure(
				"t1i", INTEGER, Types.INTEGER, 0,
				"t1s", STRING, Types.VARCHAR, 20
		));
		profileStructure.put("t2", createTableStructure(
				"t2i", INTEGER, Types.INTEGER, 0,
				"t2s", STRING, Types.VARCHAR, 20
		));
		structures.put("p1", profileStructure);
		// profile 2
		profileStructure = new HashMap<>();
		profileStructure.put("t1", createTableStructure( // same names in other profile
				"t1i", INTEGER, Types.INTEGER, 0,
				"t1s", STRING, Types.VARCHAR, 12
		));
		structures.put("p2", profileStructure);

		Map<String, String> index = new HashMap<>();
		index.put(Index.formatTableKey("p1", "t1"), "1001");
		index.put(Index.formatColumnKey("p1", "t1", "t1i"), "101");
		index.put(Index.formatColumnKey("p1", "t1", "t1s"), "102");
		index.put(Index.formatTableKey("p1", "t2"), "1002");
		index.put(Index.formatColumnKey("p1", "t2", "t2i"), "201");
		index.put(Index.formatColumnKey("p1", "t2", "t2s"), "202");
		index.put(Index.formatTableKey("p2", "t1"), "2001");
		index.put(Index.formatColumnKey("p2", "t1", "t1i"), "501");
		index.put(Index.formatColumnKey("p2", "t1", "t1s"), "502");

		Map<String, List<Map<String, Object>>> datasets = new LinkedHashMap<>();
		datasets.put("p1", Arrays.asList(
				InsertTest.createRow("t1", "t1i", "@auto", "t1s", "@auto"),
				InsertTest.createRow("t1", "t1i", "@auto", "t1s", "fixed 11"), // partial auto
				InsertTest.createRow("t1", "t1i", "123", "t1s", "fixed 12"), // row id incremented
				InsertTest.createRow("t1", "t1i", "@auto", "t1s", "@auto"),
				InsertTest.createRow("t1"), // row id not incremented
				InsertTest.createRow("t1", "t1i", "@auto", "t1s", "@auto"),
				InsertTest.createRow("t2", "t2i", "987", "t2s", "fixed 21"),
				InsertTest.createRow("t1", "t1i", "@auto", "t1s", "@auto"),
				InsertTest.createRow("t2", "t2i", "@auto", "t2s", "@auto"),
				InsertTest.createRow("t2", "t2i", "@auto", "t2s", "@auto")
		));
		datasets.put("p2", Arrays.asList(
				InsertTest.createRow("t1", "t1i", "@auto", "t1s", "@auto"),
				InsertTest.createRow("t1", "t1i", "@auto", "t1s", "@auto")
		));

		Map<String, List<Map<String, Object>>> result = Convert.convert(structures, index, datasets);

		String expected = "{p1=[\n" +
				" {TABLE=t1, COLUMNS={t1i=1100110101, t1s=t1s 1100110201}}, \n" +
				" {TABLE=t1, COLUMNS={t1i=1100110102, t1s=fixed 11}}, \n" +
				" {TABLE=t1, COLUMNS={t1i=123, t1s=fixed 12}}, \n" +
				" {TABLE=t1, COLUMNS={t1i=1100110104, t1s=t1s 1100110204}}, \n" +
				" {TABLE=t1, COLUMNS={}}, \n" +
				" {TABLE=t1, COLUMNS={t1i=1100110105, t1s=t1s 1100110205}}, \n" +
				" {TABLE=t2, COLUMNS={t2i=987, t2s=fixed 21}}, \n" +
				" {TABLE=t1, COLUMNS={t1i=1100110106, t1s=t1s 1100110206}}, \n" +
				" {TABLE=t2, COLUMNS={t2i=1100220102, t2s=t2s 1100220202}}, \n" +
				" {TABLE=t2, COLUMNS={t2i=1100220103, t2s=t2s 1100220203}}],\n" +
				" p2=[\n" +
				" {TABLE=t1, COLUMNS={t1i=1200150101, t1s=t11200150201}}, \n" +
				" {TABLE=t1, COLUMNS={t1i=1200150102, t1s=t11200150202}}]}";
		assertEquals(expected, result.toString()
				.replace("{TABLE=", "\n {TABLE=").replace("], p", "],\n p"));

		@SuppressWarnings({"unchecked", "rawtypes"})
		Map<String, Object> row = (Map) result.get("p1").get(0).get(COLUMNS);
		assertEquals(Integer.class, row.get("t1i").getClass());
		assertEquals(String.class, row.get("t1s").getClass());
	}

	@Test
	public void duplicateAutoValue_SameTable() {
		Map<String, Map<String, Map<String, Map<String, Object>>>> structures = new HashMap<>();
		Map<String, Map<String, Map<String, Object>>> profileStructure = new HashMap<>();
		profileStructure.put("t1", createTableStructure(
				"t1s", STRING, Types.VARCHAR, 1
		));
		structures.put("p1", profileStructure);

		Map<String, String> index = new HashMap<>();
		index.put(Index.formatTableKey("p1", "t1"), "1001");
		index.put(Index.formatColumnKey("p1", "t1", "t1s"), "101");

		Map<String, List<Map<String, Object>>> datasets = new LinkedHashMap<>();
		datasets.put("p1", Arrays.asList(
				InsertTest.createRow("t1", "t1s", "@auto"),
				InsertTest.createRow("t1", "t1s", "@auto"),
				InsertTest.createRow("t1", "t1s", "@auto"),
				InsertTest.createRow("t1", "t1s", "@auto"),
				InsertTest.createRow("t1", "t1s", "@auto"),
				InsertTest.createRow("t1", "t1s", "@auto"),
				InsertTest.createRow("t1", "t1s", "@auto"),
				InsertTest.createRow("t1", "t1s", "@auto"),
				InsertTest.createRow("t1", "t1s", "@auto"),
				InsertTest.createRow("t1", "t1s", "@auto"),
				InsertTest.createRow("t1", "t1s", "@auto")
		));

		try {
			Convert.convert(structures, index, datasets);
			fail("Should throw.");
		} catch (IllegalStateException e) {
			assertEquals("Duplicate auto value [1] in [p1]/t1.t1s.", e.getMessage());
		}
	}

	@Test
	public void duplicateAutoValue_OtherTable() {
		Map<String, Map<String, Map<String, Map<String, Object>>>> structures = new HashMap<>();
		Map<String, Map<String, Map<String, Object>>> profileStructure = new HashMap<>();
		profileStructure.put("t1", createTableStructure(
				"t1s", STRING, Types.VARCHAR, 1
		));
		profileStructure.put("t2", createTableStructure(
				"t2s", STRING, Types.VARCHAR, 1
		));
		structures.put("p1", profileStructure);

		Map<String, String> index = new HashMap<>();
		index.put(Index.formatTableKey("p1", "t1"), "1001");
		index.put(Index.formatColumnKey("p1", "t1", "t1s"), "101");
		index.put(Index.formatTableKey("p1", "t2"), "1002");
		index.put(Index.formatColumnKey("p1", "t2", "t2s"), "201");

		Map<String, List<Map<String, Object>>> datasets = new LinkedHashMap<>();
		datasets.put("p1", Arrays.asList(
				InsertTest.createRow("t1", "t1s", "@auto"),
				InsertTest.createRow("t2", "t2s", "@auto")
		));

		try {
			Convert.convert(structures, index, datasets);
			fail("Should throw.");
		} catch (IllegalStateException e) {
			assertEquals("Duplicate auto value [1] in [p1]/t2.t2s.", e.getMessage());
		}
	}

	@Test
	public void duplicateAutoValue_OtherProfile() {
		Map<String, Map<String, Map<String, Map<String, Object>>>> structures = new HashMap<>();
		Map<String, Map<String, Map<String, Object>>> profileStructure;
		// profile 1
		profileStructure = new HashMap<>();
		profileStructure.put("t1", createTableStructure(
				"t1s", STRING, Types.VARCHAR, 1
		));
		structures.put("p1", profileStructure);
		// profile 2
		profileStructure = new HashMap<>();
		profileStructure.put("t1", createTableStructure(
				"t1s", STRING, Types.VARCHAR, 1
		));
		structures.put("p2", profileStructure);

		Map<String, String> index = new HashMap<>();
		index.put(Index.formatTableKey("p1", "t1"), "1001");
		index.put(Index.formatColumnKey("p1", "t1", "t1s"), "101");
		index.put(Index.formatTableKey("p2", "t1"), "2001");
		index.put(Index.formatColumnKey("p2", "t1", "t1s"), "501");

		Map<String, List<Map<String, Object>>> datasets = new LinkedHashMap<>();
		datasets.put("p1", Arrays.asList(
				InsertTest.createRow("t1", "t1s", "@auto")
		));
		datasets.put("p2", Arrays.asList(
				InsertTest.createRow("t1", "t1s", "@auto")
		));

		try {
			Convert.convert(structures, index, datasets);
			fail("Should throw.");
		} catch (IllegalStateException e) {
			assertEquals("Duplicate auto value [1] in [p2]/t1.t1s.", e.getMessage());
		}
	}

	@Test
	public void passThrough() { // @any, variables
		Map<String, Map<String, Map<String, Map<String, Object>>>> structures = new HashMap<>();
		Map<String, Map<String, Map<String, Object>>> profileStructure = new HashMap<>();
		profileStructure.put("data_types", InsertTest.createTableStructure(
				"integer_any", INTEGER, Types.INTEGER,
				"string_any", STRING, Types.VARCHAR,
				"integer_var", INTEGER, Types.INTEGER,
				"string_var", STRING, Types.VARCHAR
		));
		structures.put(DEFAULT_PROFILE, profileStructure);

		Map<String, List<Map<String, Object>>> datasets = new LinkedHashMap<>();
		datasets.put(DEFAULT_PROFILE, Arrays.asList(
				InsertTest.createRow("data_types",
						"integer_any", "@any",
						"string_any", "@any",
						"integer_var", "$var1",
						"string_var", "$var2"
				)
		));

		Map<String, List<Map<String, Object>>> result = Convert.convert(structures, null, datasets);

		String expected = "{=[{TABLE=data_types,\n" +
				" COLUMNS={integer_any=@any,\n" +
				" string_any=@any,\n" +
				" integer_var=$var1,\n" +
				" string_var=$var2}}]}";
		assertEquals(expected, result.toString().replace(", ", ",\n "));

		@SuppressWarnings({"unchecked", "rawtypes"})
		Map<String, Object> dataTypes = (Map) result.get(DEFAULT_PROFILE).get(0).get(COLUMNS);
		assertEquals("integer any", "@any", dataTypes.get("integer_any"));
		assertEquals("string any", "@any", dataTypes.get("string_any"));
		assertEquals("integer var", "$var1", dataTypes.get("integer_var"));
		assertEquals("string var", "$var2", dataTypes.get("string_var"));
	}
}
