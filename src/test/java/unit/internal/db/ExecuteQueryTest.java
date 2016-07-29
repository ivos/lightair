package unit.internal.db;

import net.sf.lightair.internal.Keywords;
import net.sf.lightair.internal.db.ExecuteQuery;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import test.support.DbTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExecuteQueryTest implements Keywords {

	private List<Map<String, Object>> createStatements(Object... data) {
		assertTrue("Data in triples", data.length % 3 == 0);
		List<Map<String, Object>> statements = new ArrayList<>();
		for (int i = 0; i < data.length; i = i + 3) {
			Map<String, Object> statement = new LinkedHashMap<>();
			statement.put(TABLE, data[i]);
			statement.put(SQL, data[i + 1]);
			statement.put(COLUMNS, data[i + 2]);
			statements.add(statement);
		}
		return statements;
	}

	private Map<String, Map<String, Object>> createColumns(Object... data) {
		Map<String, Map<String, Object>> parameters = new LinkedHashMap<>();
		assertTrue("Data in triples", data.length % 3 == 0);
		for (int i = 0; i < data.length; i = i + 3) {
			Map<String, Object> parameter = new LinkedHashMap<>();
			parameter.put(DATA_TYPE, data[i + 1]);
			parameter.put(JDBC_DATA_TYPE, data[i + 2]);
			parameters.put((String) data[i], parameter);
		}
		return parameters;
	}

	@Test
	public void basic() {
		DbTemplate h2 = new DbTemplate("jdbc:h2:mem:test", "sa", "");
		h2.db.execute("create table t1 (t1a int, t1b varchar2(10) not null, t1c varchar2(20))");
		h2.db.execute("insert into t1 (t1a, t1b, t1c) values (1,'t1b1','t1c1')");
		h2.db.execute("insert into t1 (t1a, t1b, t1c) values (2,'t1b2','t1c2')");
		h2.db.execute("insert into t1 (t1a, t1b, t1c) values (3,'t1b3','t1c3')");
		h2.db.execute("create table t2 (t2a int, t2b varchar2(10) not null, t2c varchar2(20))");
		h2.db.execute("insert into t2 (t2a, t2b, t2c) values (1,'t2b1','t2c1')");
		h2.db.execute("insert into t2 (t2a, t2b, t2c) values (2,'t2b2','t2c2')");
		h2.db.execute("insert into t2 (t2a, t2b, t2c) values (3,'t2b3','t2c3')");

		Map<String, List<Map<String, Object>>> data = ExecuteQuery.run(h2.getConnection(),
				createStatements(
						"t1",
						"select t1a, t1b, t1c from t1",
						createColumns("t1a", INTEGER, Types.INTEGER,
								"t1b", STRING, Types.VARCHAR,
								"t1c", STRING, Types.VARCHAR),
						"t2",
						"select t2a, t2b, t2c from t2",
						createColumns("t2a", INTEGER, Types.INTEGER,
								"t2b", STRING, Types.VARCHAR,
								"t2c", STRING, Types.VARCHAR)
				));

		h2.db.execute("drop table t1");
		h2.db.execute("drop table t2");

		String expected = "{t1=[{t1a=1, t1b=t1b1, t1c=t1c1},\n" +
				" {t1a=2, t1b=t1b2, t1c=t1c2},\n" +
				" {t1a=3, t1b=t1b3, t1c=t1c3}],\n" +
				"t2=[{t2a=1, t2b=t2b1, t2c=t2c1},\n" +
				" {t2a=2, t2b=t2b2, t2c=t2c2},\n" +
				" {t2a=3, t2b=t2b3, t2c=t2c3}]}";
		assertEquals(expected, data.toString()
				.replace("}, ", "},\n ")
				.replace("], ", "],\n"));

		assertEquals(Integer.class, data.get("t1").get(0).get("t1a").getClass());
	}

	@Test
	public void dataTypesH2() throws IOException {
		DbTemplate h2 = new DbTemplate("jdbc:h2:mem:test", "sa", "");
		h2.db.execute("create table data_types (id int primary key," +
				" bit_type bit, boolean_type boolean," +
				" tinyint_type tinyint, smallint_type smallint, integer_type integer, bigint_type bigint," +
				" real_type real, float_type double, double_type double," +
				" numeric_type numeric(20,2), decimal_type decimal(20,2)," +
				" date_type date, time_type time, timestamp_type timestamp," +
				" char_type char(20), varchar_type varchar(50), longvarchar_type longvarchar(5000)," +
				" nchar_type nchar(20), nvarchar_type nvarchar(50), longnvarchar_type longnvarchar(5000)," +
				" binary_type binary(8), varbinary_type varbinary(8), longvarbinary_type longvarbinary(5000)," +
				" clob_type clob, nclob_type nclob, blob_type blob)");
		h2.db.update("insert into data_types (id," +
						"bit_type,boolean_type,tinyint_type,smallint_type,integer_type,bigint_type," +
						"real_type,float_type,double_type,numeric_type,decimal_type," +
						"date_type,time_type,timestamp_type,char_type,varchar_type,longvarchar_type," +
						"nchar_type,nvarchar_type,longnvarchar_type,binary_type,varbinary_type,longvarbinary_type," +
						"clob_type,nclob_type,blob_type)" +
						" values (1,false,true,123,12345,1234567890,10000000000,123456.7890123,123456.7890123,123456.7890123," +
						"123456.7890123,123456.7890123,'2015-12-31','12:34:56','2015-12-31T12:34:56'," +
						"'char1','varchar1','longvarchar1','nchar1','nvarchar1','longnvarchar1'," +
						"?,?,?,?,?,?)",
				"binary1".getBytes(StandardCharsets.UTF_8),
				"varbin1".getBytes(StandardCharsets.UTF_8),
				"longvarbinary1".getBytes(StandardCharsets.UTF_8),
				new StringReader("clob1"),
				new StringReader("nclob1"),
				new ByteArrayInputStream("blob1".getBytes()));

		Map<String, List<Map<String, Object>>> data = ExecuteQuery.run(h2.getConnection(),
				createStatements(
						"data_types",
						"select id,bit_type,boolean_type," +
								"tinyint_type,smallint_type,integer_type,bigint_type," +
								"real_type,float_type,double_type,numeric_type,decimal_type," +
								"date_type,time_type,timestamp_type," +
								"char_type,varchar_type,longvarchar_type,nchar_type,nvarchar_type,longnvarchar_type," +
								"binary_type,varbinary_type,longvarbinary_type," +
								"clob_type,nclob_type,blob_type from data_types",
						createColumns("id", INTEGER, Types.INTEGER,
								"bit_type", BOOLEAN, Types.BIT,
								"boolean_type", BOOLEAN, Types.BOOLEAN,
								"tinyint_type", BYTE, Types.TINYINT,
								"smallint_type", SHORT, Types.SMALLINT,
								"integer_type", INTEGER, Types.INTEGER,
								"bigint_type", LONG, Types.BIGINT,
								"real_type", FLOAT, Types.REAL,
								"float_type", FLOAT, Types.REAL,
								"double_type", DOUBLE, Types.DOUBLE,
								"numeric_type", BIGDECIMAL, Types.NUMERIC,
								"decimal_type", BIGDECIMAL, Types.DECIMAL,
								"date_type", DATE, Types.DATE,
								"time_type", TIME, Types.TIME,
								"timestamp_type", TIMESTAMP, Types.TIMESTAMP,
								"char_type", STRING, Types.CHAR,
								"varchar_type", STRING, Types.VARCHAR,
								"longvarchar_type", STRING, Types.LONGVARCHAR,
								"nchar_type", STRING, Types.CHAR,
								"nvarchar_type", STRING, Types.VARCHAR,
								"longnvarchar_type", STRING, Types.LONGVARCHAR,
								"binary_type", BYTES, Types.BINARY,
								"varbinary_type", BYTES, Types.VARBINARY,
								"longvarbinary_type", BYTES, Types.LONGVARBINARY,
								"clob_type", CLOB, Types.CLOB,
								"nclob_type", NCLOB, Types.NCLOB,
								"blob_type", BLOB, Types.BLOB
						)));

		h2.db.execute("drop table data_types");

		String expected = "{data_types=[{id=1,\n" +
				"bit_type=false,\n" +
				"boolean_type=true,\n" +
				"tinyint_type=123,\n" +
				"smallint_type=12345,\n" +
				"integer_type=1234567890,\n" +
				"bigint_type=10000000000,\n" +
				"real_type=123456.79,\n" +
				"float_type=123456.79,\n" +
				"double_type=123456.7890123,\n" +
				"numeric_type=123456.79,\n" +
				"decimal_type=123456.79,\n" +
				"date_type=2015-12-31,\n" +
				"time_type=12:34:56,\n" +
				"timestamp_type=2015-12-31 12:34:56.0,\n" +
				"char_type=char1,\n" +
				"varchar_type=varchar1,\n" +
				"longvarchar_type=longvarchar1,\n" +
				"nchar_type=nchar1,\n" +
				"nvarchar_type=nvarchar1,\n" +
				"longnvarchar_type=longnvarchar1,\n" +
				"binary_type=REPLACED,\n" +
				"varbinary_type=REPLACED,\n" +
				"longvarbinary_type=REPLACED,\n" +
				"clob_type=clob1,\n" +
				"nclob_type=nclob1,\n" +
				"blob_type=REPLACED}]}";
		assertEquals(expected, data.toString()
				.replaceAll("\\[B@[^,}]+", "REPLACED")
				.replace(", ", ",\n"));

		Map<String, Object> row = data.get("data_types").get(0);
		assertEquals(Boolean.class, row.get("bit_type").getClass());
		assertEquals(Boolean.class, row.get("boolean_type").getClass());
		assertEquals(Byte.class, row.get("tinyint_type").getClass());
		assertEquals(Short.class, row.get("smallint_type").getClass());
		assertEquals(Integer.class, row.get("integer_type").getClass());
		assertEquals(Long.class, row.get("bigint_type").getClass());
		assertEquals(Float.class, row.get("real_type").getClass());
		assertEquals(Float.class, row.get("float_type").getClass());
		assertEquals(Double.class, row.get("double_type").getClass());
		assertEquals(BigDecimal.class, row.get("numeric_type").getClass());
		assertEquals(BigDecimal.class, row.get("decimal_type").getClass());
		assertEquals(Date.class, row.get("date_type").getClass());
		assertEquals(Time.class, row.get("time_type").getClass());
		assertEquals(Timestamp.class, row.get("timestamp_type").getClass());
		assertEquals(String.class, row.get("char_type").getClass());
		assertEquals(String.class, row.get("varchar_type").getClass());
		assertEquals(String.class, row.get("longvarchar_type").getClass());
		assertEquals(String.class, row.get("nchar_type").getClass());
		assertEquals(String.class, row.get("nvarchar_type").getClass());
		assertEquals(String.class, row.get("longnvarchar_type").getClass());
		assertEquals(byte[].class, row.get("binary_type").getClass());
		assertEquals("binary1", new String((byte[]) row.get("binary_type")));
		assertEquals(byte[].class, row.get("varbinary_type").getClass());
		assertEquals("varbin1", new String((byte[]) row.get("varbinary_type")));
		assertEquals(byte[].class, row.get("longvarbinary_type").getClass());
		assertEquals("longvarbinary1", new String((byte[]) row.get("longvarbinary_type")));
		assertEquals("blob1", new String((byte[]) row.get("blob_type")));
	}

	@Test
	public void dataTypesHsql() throws IOException, SQLException, DecoderException {
		DbTemplate hsql = new DbTemplate("jdbc:hsqldb:mem:test", "sa", "");
		hsql.db.execute("create table data_types (id int primary key," +
				" bit_type bit, boolean_type boolean," +
				" tinyint_type tinyint, smallint_type smallint, integer_type integer, bigint_type bigint," +
				" real_type real, float_type double, double_type double," +
				" numeric_type numeric(20,2), decimal_type decimal(20,2)," +
				" date_type date, time_type time, timestamp_type timestamp," +
				" char_type char(20), varchar_type varchar(50), longvarchar_type longvarchar(5000)," +
				" binary_type binary(8), varbinary_type varbinary(8), longvarbinary_type longvarbinary(5000)," +
				" clob_type clob, blob_type blob)");
		hsql.db.update("insert into data_types (id," +
						"bit_type,boolean_type,tinyint_type,smallint_type,integer_type,bigint_type," +
						"real_type,float_type,double_type,numeric_type,decimal_type," +
						"date_type,time_type,timestamp_type,char_type,varchar_type,longvarchar_type," +
						"binary_type,varbinary_type,longvarbinary_type,clob_type,blob_type)" +
						" values (1,false,true,123,12345,1234567890,10000000000,123456.7890123,123456.7890123,123456.7890123," +
						"123456.7890123,123456.7890123,'2015-12-31','12:34:56','2015-12-31 12:34:56'," +
						"'char1','varchar1','longvarchar1',?,?,?,'clob1','1234567890abcdef')",
				"binary1".getBytes(StandardCharsets.UTF_8),
				"varbin1".getBytes(StandardCharsets.UTF_8),
				"longvarbinary1".getBytes(StandardCharsets.UTF_8)
		);

		Map<String, List<Map<String, Object>>> data = ExecuteQuery.run(hsql.getConnection(),
				createStatements(
						"data_types",
						"select id,bit_type,boolean_type," +
								"tinyint_type,smallint_type,integer_type,bigint_type," +
								"real_type,float_type,double_type,numeric_type,decimal_type," +
								"date_type,time_type,timestamp_type," +
								"char_type,varchar_type,longvarchar_type," +
								"binary_type,varbinary_type,longvarbinary_type," +
								"clob_type,blob_type from data_types",
						createColumns("id", INTEGER, Types.INTEGER,
								"bit_type", BOOLEAN, Types.BIT,
								"boolean_type", BOOLEAN, Types.BOOLEAN,
								"tinyint_type", BYTE, Types.TINYINT,
								"smallint_type", SHORT, Types.SMALLINT,
								"integer_type", INTEGER, Types.INTEGER,
								"bigint_type", LONG, Types.BIGINT,
								"real_type", FLOAT, Types.REAL,
								"float_type", FLOAT, Types.REAL,
								"double_type", DOUBLE, Types.DOUBLE,
								"numeric_type", BIGDECIMAL, Types.NUMERIC,
								"decimal_type", BIGDECIMAL, Types.DECIMAL,
								"date_type", DATE, Types.DATE,
								"time_type", TIME, Types.TIME,
								"timestamp_type", TIMESTAMP, Types.TIMESTAMP,
								"char_type", STRING, Types.CHAR,
								"varchar_type", STRING, Types.VARCHAR,
								"longvarchar_type", STRING, Types.LONGVARCHAR,
								"binary_type", BYTES, Types.BINARY,
								"varbinary_type", BYTES, Types.VARBINARY,
								"longvarbinary_type", BYTES, Types.LONGVARBINARY,
								"clob_type", CLOB, Types.CLOB,
								"blob_type", BLOB, Types.BLOB
						)));

		hsql.db.execute("drop table data_types");

		String expected = "{data_types=[{id=1,\n" +
				"bit_type=false,\n" +
				"boolean_type=true,\n" +
				"tinyint_type=123,\n" +
				"smallint_type=12345,\n" +
				"integer_type=1234567890,\n" +
				"bigint_type=10000000000,\n" +
				"real_type=123456.79,\n" +
				"float_type=123456.79,\n" +
				"double_type=123456.7890123,\n" +
				"numeric_type=123456.79,\n" +
				"decimal_type=123456.79,\n" +
				"date_type=2015-12-31,\n" +
				"time_type=12:34:56,\n" +
				"timestamp_type=2015-12-31 12:34:56.0,\n" +
				"char_type=char1               ,\n" +
				"varchar_type=varchar1,\n" +
				"longvarchar_type=longvarchar1,\n" +
				"binary_type=REPLACED,\n" +
				"varbinary_type=REPLACED,\n" +
				"longvarbinary_type=REPLACED,\n" +
				"clob_type=clob1,\n" +
				"blob_type=REPLACED}]}";
		assertEquals(expected, data.toString()
				.replaceAll("\\[B@[^,}]+", "REPLACED")
				.replace(", ", ",\n"));

		Map<String, Object> row = data.get("data_types").get(0);
		assertEquals(Boolean.class, row.get("bit_type").getClass());
		assertEquals(Boolean.class, row.get("boolean_type").getClass());
		assertEquals(Byte.class, row.get("tinyint_type").getClass());
		assertEquals(Short.class, row.get("smallint_type").getClass());
		assertEquals(Integer.class, row.get("integer_type").getClass());
		assertEquals(Long.class, row.get("bigint_type").getClass());
		assertEquals(Float.class, row.get("real_type").getClass());
		assertEquals(Float.class, row.get("float_type").getClass());
		assertEquals(Double.class, row.get("double_type").getClass());
		assertEquals(BigDecimal.class, row.get("numeric_type").getClass());
		assertEquals(BigDecimal.class, row.get("decimal_type").getClass());
		assertEquals(Date.class, row.get("date_type").getClass());
		assertEquals(Time.class, row.get("time_type").getClass());
		assertEquals(Timestamp.class, row.get("timestamp_type").getClass());
		assertEquals(String.class, row.get("char_type").getClass());
		assertEquals(String.class, row.get("varchar_type").getClass());
		assertEquals(String.class, row.get("longvarchar_type").getClass());
		assertEquals(byte[].class, row.get("binary_type").getClass());
		byte[] binary = new byte[8];
		System.arraycopy("binary1".getBytes(), 0, binary, 0, 7);
		assertArrayEquals(binary, (byte[]) row.get("binary_type"));
		assertEquals(byte[].class, row.get("varbinary_type").getClass());
		assertEquals("varbin1", new String((byte[]) row.get("varbinary_type")));
		assertEquals(byte[].class, row.get("longvarbinary_type").getClass());
		assertEquals("longvarbinary1", new String((byte[]) row.get("longvarbinary_type")));
		assertArrayEquals(Hex.decodeHex("1234567890abcdef".toCharArray()), (byte[]) row.get("blob_type"));
	}
}
