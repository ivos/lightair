package unit.internal.db;

import net.sf.lightair.internal.Keywords;
import net.sf.lightair.internal.db.Converter;
import org.apache.commons.io.IOUtils;
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

public class ConverterTest implements Keywords {

	private Map<String, Map<String, Map<String, Map<String, Object>>>> createStructures() {
		Map<String, Map<String, Map<String, Map<String, Object>>>> structures = new HashMap<>();
		Map<String, Map<String, Map<String, Object>>> profileStructure;

		profileStructure = new HashMap<>();
		profileStructure.put("t1", InsertTest.createTableStructure(
				"t1a", INTEGER, Types.INTEGER,
				"t1b", STRING, Types.VARCHAR
		));
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
		structures.put("profile1", profileStructure);

		profileStructure = new HashMap<>();
		profileStructure.put("t2", InsertTest.createTableStructure(
				"t2a", INTEGER, Types.INTEGER,
				"t2b", STRING, Types.VARCHAR
		));
		structures.put("profile2", profileStructure);

		return structures;
	}

	private Map<String, List<Map<String, Object>>> createDatasets() {
		Map<String, List<Map<String, Object>>> datasets = new LinkedHashMap<>();
		List<Map<String, Object>> dataset;

		datasets.put("profile1", Arrays.asList(
				InsertTest.createRow("t1", "t1a", "1230101", "t1b", "v1b01"),
				InsertTest.createRow("t1", "t1a", "1230102", "t1b", "v1b02"),
				InsertTest.createRow("data_types",
						"boolean_type", "true",
						"byte_type", "123",
						"short_type", "12345",
						"integer_type", "1234567890",
						"long_type", "40234567890",
						"float_type", "1234.56",
						"double_type", "123456.789123",
						"bigdecimal_type", "1234567890123456789.123456789",
						"date_type", "2015-12-31",
						"time_type", "12:34:56",
						"timestamp_type", "2015-12-31T12:34:56",
						"string_type", "string value",
						"nstring_type", "nstring value",
						"bytes_type", "bytes value",
						"clob_type", "clob value",
						"nclob_type", "nclob value",
						"blob_type", "blob value"
				),
				InsertTest.createRow("data_types",
						"boolean_type", "@null",
						"byte_type", "@null",
						"short_type", "@null",
						"integer_type", "@null",
						"long_type", "@null",
						"float_type", "@null",
						"double_type", "@null",
						"bigdecimal_type", "@null",
						"date_type", "@null",
						"time_type", "@null",
						"timestamp_type", "@null",
						"string_type", "@null",
						"nstring_type", "@null",
						"bytes_type", "@null",
						"clob_type", "@null",
						"nclob_type", "@null",
						"blob_type", "@null"
				)
		));

		datasets.put("profile2", Arrays.asList(
				InsertTest.createRow("t2", "t2a", "1230201", "t2b", "v2b01"),
				InsertTest.createRow("t2", "t2a", "1230202", "t2b", "v2b02")
		));

		return datasets;
	}

	@Test
	public void test() throws IOException {
		Map<String, Map<String, Map<String, Map<String, Object>>>> structures = createStructures();
		Map<String, List<Map<String, Object>>> datasets = createDatasets();

		Map<String, List<Map<String, Object>>> result = Converter.convert(structures, datasets);

		String expected = "{profile1=[{TABLE=t1,\n" +
				" COLUMNS={t1a=1230101,\n" +
				" t1b=v1b01}},\n" +
				" {TABLE=t1,\n" +
				" COLUMNS={t1a=1230102,\n" +
				" t1b=v1b02}},\n" +
				" {TABLE=data_types,\n" +
				" COLUMNS={boolean_type=true,\n" +
				" byte_type=123,\n" +
				" short_type=12345,\n" +
				" integer_type=1234567890,\n" +
				" long_type=40234567890,\n" +
				" float_type=1234.56,\n" +
				" double_type=123456.789123,\n" +
				" bigdecimal_type=1234567890123456789.123456789,\n" +
				" date_type=2015-12-31,\n" +
				" time_type=12:34:56,\n" +
				" timestamp_type=2015-12-31 12:34:56.0,\n" +
				" string_type=string value,\n" +
				" nstring_type=nstring value,\n" +
				" bytes_type=REPLACED,\n" +
				" clob_type=REPLACED,\n" +
				" nclob_type=REPLACED,\n" +
				" blob_type=REPLACED}},\n" +
				" {TABLE=data_types,\n" +
				" COLUMNS={boolean_type=null,\n" +
				" byte_type=null,\n" +
				" short_type=null,\n" +
				" integer_type=null,\n" +
				" long_type=null,\n" +
				" float_type=null,\n" +
				" double_type=null,\n" +
				" bigdecimal_type=null,\n" +
				" date_type=null,\n" +
				" time_type=null,\n" +
				" timestamp_type=null,\n" +
				" string_type=null,\n" +
				" nstring_type=null,\n" +
				" bytes_type=null,\n" +
				" clob_type=null,\n" +
				" nclob_type=null,\n" +
				" blob_type=null}}],\n" +
				" profile2=[{TABLE=t2,\n" +
				" COLUMNS={t2a=1230201,\n" +
				" t2b=v2b01}},\n" +
				" {TABLE=t2,\n" +
				" COLUMNS={t2a=1230202,\n" +
				" t2b=v2b02}}]}";
		assertEquals(expected, result.toString()
				.replace(", ", ",\n ")
				.replaceAll("\\[B@[^,}]+", "REPLACED")
				.replaceAll("java.io.StringReader@[^,}]+", "REPLACED")
				.replaceAll("java.io.ByteArrayInputStream@[^,}]+", "REPLACED"));

		@SuppressWarnings("unchecked")
		Map<String, Object> dataTypes = (Map) result.get("profile1").get(2).get(COLUMNS);
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
}
