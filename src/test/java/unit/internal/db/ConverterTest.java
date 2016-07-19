package unit.internal.db;

import net.sf.lightair.internal.Keywords;
import net.sf.lightair.internal.db.Converter;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ConverterTest implements Keywords {

	@Before
	public void setUp() {
		DateTimeUtils.setCurrentMillisFixed(DateTime.parse("2015-12-31T12:34:56.123").getMillis());
	}

	@After
	public void tearDown() {
		DateTimeUtils.setCurrentMillisSystem();
	}

	@Test
	public void profiles() throws IOException {
		Map<String, Map<String, Map<String, Map<String, Object>>>> structures = new HashMap<>();
		Map<String, Map<String, Map<String, Object>>> profileStructure;
		// profile 1 structure
		profileStructure = new HashMap<>();
		profileStructure.put("t11", InsertTest.createTableStructure(
				"t11a", INTEGER, Types.INTEGER,
				"t11b", STRING, Types.VARCHAR
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
				InsertTest.createRow("t11", "t11a", "1231101", "t11b", "v11b01"),
				InsertTest.createRow("t11", "t11a", "1231102", "t11b", "v11b02"),
				InsertTest.createRow("t12", "t12a", "1231201", "t12b", "v12b01")
		));
		datasets.put("profile2", Arrays.asList(
				InsertTest.createRow("t21", "t21a", "1232101", "t21b", "v21b01"),
				InsertTest.createRow("t21", "t21a", "1232102", "t21b", "v21b02")
		));

		Map<String, List<Map<String, Object>>> result = Converter.convert(structures, datasets);

		String expected = "{profile1=[{TABLE=t11,\n" +
				" COLUMNS={t11a=1231101,\n" +
				" t11b=v11b01}},\n" +
				" {TABLE=t11,\n" +
				" COLUMNS={t11a=1231102,\n" +
				" t11b=v11b02}},\n" +
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
				"byte_type", BYTE, Types.TINYINT,
				"short_type", SHORT, Types.SMALLINT,
				"integer_type", INTEGER, Types.INTEGER,
				"long_type", LONG, Types.BIGINT,
				"float_type", FLOAT, Types.REAL,
				"double_type", DOUBLE, Types.DOUBLE,
				"bigdecimal_type", BIGDECIMAL, Types.DECIMAL,
				"date_type", DATE, Types.DATE,
				"time_type", TIME, Types.TIME,
				"timestamp_type", TIMESTAMP, Types.TIMESTAMP,
				"string_type", STRING, Types.VARCHAR,
				"nstring_type", NSTRING, Types.NVARCHAR,
				"bytes_type", BYTES, Types.BINARY,
				"clob_type", CLOB, Types.CLOB,
				"nclob_type", NCLOB, Types.NCLOB,
				"blob_type", BLOB, Types.BLOB
		));
		structures.put(DEFAULT_PROFILE, profileStructure);

		Map<String, List<Map<String, Object>>> datasets = new LinkedHashMap<>();
		datasets.put(DEFAULT_PROFILE, Arrays.asList(
				InsertTest.createRow("data_types",
						"boolean_type", "true",
						"byte_type", "123",
						"short_type", "12345",
						"integer_type", "1234567890",
						"long_type", "40234567890",
						"float_type", "1234.56",
						"double_type", "123456.789123",
						"bigdecimal_type", "1234567890123456789.123456789",
						"date_type", "2016-08-09",
						"time_type", "21:43:59",
						"timestamp_type", "2016-08-09T21:43:59.321",
						"string_type", "string value",
						"nstring_type", "nstring value",
						"bytes_type", "bytes value",
						"clob_type", "clob value",
						"nclob_type", "nclob value",
						"blob_type", "blob value"
				)
		));

		Map<String, List<Map<String, Object>>> result = Converter.convert(structures, datasets);

		String expected = "{=[{TABLE=data_types,\n" +
				" COLUMNS={boolean_type=true,\n" +
				" byte_type=123,\n" +
				" short_type=12345,\n" +
				" integer_type=1234567890,\n" +
				" long_type=40234567890,\n" +
				" float_type=1234.56,\n" +
				" double_type=123456.789123,\n" +
				" bigdecimal_type=1234567890123456789.123456789,\n" +
				" date_type=2016-08-09,\n" +
				" time_type=21:43:59,\n" +
				" timestamp_type=2016-08-09 21:43:59.321,\n" +
				" string_type=string value,\n" +
				" nstring_type=nstring value,\n" +
				" bytes_type=REPLACED,\n" +
				" clob_type=REPLACED,\n" +
				" nclob_type=REPLACED,\n" +
				" blob_type=REPLACED}}]}";
		assertEquals(expected, result.toString()
				.replace(", ", ",\n ")
				.replaceAll("\\[B@[^,}]+", "REPLACED")
				.replaceAll("java.io.StringReader@[^,}]+", "REPLACED")
				.replaceAll("java.io.ByteArrayInputStream@[^,}]+", "REPLACED"));

		@SuppressWarnings("unchecked")
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
		assertEquals(String.class, dataTypes.get("nstring_type").getClass());
		assertEquals(byte[].class, dataTypes.get("bytes_type").getClass());
		assertEquals(StringReader.class, dataTypes.get("clob_type").getClass());
		assertEquals(StringReader.class, dataTypes.get("nclob_type").getClass());
		assertEquals(ByteArrayInputStream.class, dataTypes.get("blob_type").getClass());

		assertEquals("bytes value", new String((byte[]) dataTypes.get("bytes_type"), StandardCharsets.UTF_8));
		assertEquals("clob value", IOUtils.toString((Reader) dataTypes.get("clob_type")));
		assertEquals("nclob value", IOUtils.toString((Reader) dataTypes.get("nclob_type")));
		assertEquals("blob value", IOUtils.toString((InputStream) dataTypes.get("blob_type")));
	}

	@Test
	public void tokenNull() throws IOException {
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

		Map<String, List<Map<String, Object>>> result = Converter.convert(structures, datasets);

		String expected = "{=[{TABLE=data_types,\n" +
				" COLUMNS={integer_type=null,\n" +
				" string_type=null}}]}";
		assertEquals(expected, result.toString().replace(", ", ",\n "));

		@SuppressWarnings("unchecked")
		Map<String, Object> dataTypes = (Map) result.get(DEFAULT_PROFILE).get(0).get(COLUMNS);
		assertNull("integer value", dataTypes.get("integer_type"));
		assertNull("string value", dataTypes.get("string value"));
	}

	@Test
	public void tokenDate() throws IOException {
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

		Map<String, List<Map<String, Object>>> result = Converter.convert(structures, datasets);

		String expected = "{=[{TABLE=data_types,\n" +
				" COLUMNS={date_type=2015-12-31,\n" +
				" time_type=00:00:00,\n" +
				" timestamp_type=2015-12-31 00:00:00.0}}]}";
		assertEquals(expected, result.toString().replace(", ", ",\n "));

		@SuppressWarnings("unchecked")
		Map<String, Object> dataTypes = (Map) result.get(DEFAULT_PROFILE).get(0).get(COLUMNS);
		assertEquals(Date.class, dataTypes.get("date_type").getClass());
		assertEquals(Time.class, dataTypes.get("time_type").getClass());
		assertEquals(Timestamp.class, dataTypes.get("timestamp_type").getClass());
	}

	@Test
	public void tokenTime() throws IOException {
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

		Map<String, List<Map<String, Object>>> result = Converter.convert(structures, datasets);

		String expected = "{=[{TABLE=data_types,\n" +
				" COLUMNS={date_type=1970-01-01,\n" +
				" time_type=12:34:56,\n" +
				" timestamp_type=1970-01-01 12:34:56.0}}]}";
		assertEquals(expected, result.toString().replace(", ", ",\n "));

		@SuppressWarnings("unchecked")
		Map<String, Object> dataTypes = (Map) result.get(DEFAULT_PROFILE).get(0).get(COLUMNS);
		assertEquals(Date.class, dataTypes.get("date_type").getClass());
		assertEquals(Time.class, dataTypes.get("time_type").getClass());
		assertEquals(Timestamp.class, dataTypes.get("timestamp_type").getClass());
	}

	@Test
	public void tokenTimestamp() throws IOException {
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

		Map<String, List<Map<String, Object>>> result = Converter.convert(structures, datasets);

		String expected = "{=[{TABLE=data_types,\n" +
				" COLUMNS={date_type=2015-12-31,\n" +
				" time_type=12:34:56,\n" +
				" timestamp_type=2015-12-31 12:34:56.123}}]}";
		assertEquals(expected, result.toString().replace(", ", ",\n "));

		@SuppressWarnings("unchecked")
		Map<String, Object> dataTypes = (Map) result.get(DEFAULT_PROFILE).get(0).get(COLUMNS);
		assertEquals(Date.class, dataTypes.get("date_type").getClass());
		assertEquals(Time.class, dataTypes.get("time_type").getClass());
		assertEquals(Timestamp.class, dataTypes.get("timestamp_type").getClass());
	}
}
