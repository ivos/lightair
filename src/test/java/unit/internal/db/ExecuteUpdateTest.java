package unit.internal.db;

import net.sf.lightair.internal.Keywords;
import net.sf.lightair.internal.db.Execute;
import org.junit.Test;
import test.support.DbTemplate;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExecuteUpdateTest implements Keywords {

	private List<Map<String, Object>> createStatements(Object... data) {
		assertTrue("Data in pairs", data.length % 2 == 0);
		List<Map<String, Object>> statements = new ArrayList<>();
		for (int i = 0; i < data.length; i = i + 2) {
			Map<String, Object> statement = new LinkedHashMap<>();
			statement.put(SQL, data[i]);
			statement.put(PARAMETERS, data[i + 1]);
			statements.add(statement);
		}
		return statements;
	}

	@Test
	public void delete() {
		DbTemplate h2 = new DbTemplate("jdbc:h2:mem:test", "sa", "");
		h2.db.execute("create table t1 (t1a int, t1b varchar2(10) not null, t1c varchar2(20))");
		h2.db.execute("insert into t1 (t1a, t1b, t1c) values (1,'b1','c1')");
		h2.db.execute("insert into t1 (t1a, t1b, t1c) values (2,'b2',null)");
		h2.db.execute("insert into t1 (t1a, t1b, t1c) values (3,'b3','c3')");

		int count;
		count = h2.db.queryForList("select * from t1").size();
		assertEquals("Before", 3, count);

		Execute.update(h2.getConnection(), createStatements("delete from t1", Collections.emptyList()));

		count = h2.db.queryForList("select * from t1").size();
		assertEquals("After", 0, count);

		h2.db.execute("drop table t1");
	}

	private List<Map<String, Object>> createParameters(Object... data) {
		List<Map<String, Object>> parameters = new ArrayList<>();
		assertTrue("Data in triples", data.length % 3 == 0);
		for (int i = 0; i < data.length; i = i + 3) {
			Map<String, Object> parameter = new LinkedHashMap<>();
			parameter.put(DATA_TYPE, data[i]);
			parameter.put(JDBC_DATA_TYPE, data[i + 1]);
			parameter.put(VALUE, data[i + 2]);
			parameters.add(parameter);
		}
		return parameters;
	}

	@Test
	public void insert() {
		DbTemplate h2 = new DbTemplate("jdbc:h2:mem:test", "sa", "");
		h2.db.execute("create table t1 (t1a int, t1b varchar2(10) not null, t1c varchar2(20))");
		h2.db.execute("insert into t1 (t1a, t1b, t1c) values (1,'b1','c1')");

		List<Map<String, Object>> data;
		data = h2.db.queryForList("select * from t1");
		assertEquals("Before", "[{T1A=1, T1B=b1, T1C=c1}]", data.toString());

		Execute.update(h2.getConnection(),
				createStatements(
						"insert into t1 (t1a, t1b, t1c) values (?,?,?)",
						createParameters(INTEGER, Types.INTEGER, 2,
								STRING, Types.VARCHAR, "b2",
								STRING, Types.VARCHAR, null),
						"insert into t1 (t1a, t1b, t1c) values (?,?,?)",
						createParameters(INTEGER, Types.INTEGER, 3,
								STRING, Types.VARCHAR, "b3",
								STRING, Types.VARCHAR, "c3")
				));

		data = h2.db.queryForList("select * from t1");
		assertEquals("After", "[{T1A=1, T1B=b1, T1C=c1}, {T1A=2, T1B=b2, T1C=null}, {T1A=3, T1B=b3, T1C=c3}]",
				data.toString());

		h2.db.execute("drop table t1");
	}

	@Test
	public void cleanInsert() {
		DbTemplate h2 = new DbTemplate("jdbc:h2:mem:test", "sa", "");
		h2.db.execute("create table t1 (t1a int, t1b varchar2(10) not null, t1c varchar2(20))");
		h2.db.execute("insert into t1 (t1a, t1b, t1c) values (1,'b1','c1')");
		h2.db.execute("insert into t1 (t1a, t1b, t1c) values (2,'b2',null)");
		h2.db.execute("insert into t1 (t1a, t1b, t1c) values (3,'b3','c3')");

		List<Map<String, Object>> data;
		data = h2.db.queryForList("select * from t1");
		assertEquals("Before",
				"[{T1A=1, T1B=b1, T1C=c1}, {T1A=2, T1B=b2, T1C=null}, {T1A=3, T1B=b3, T1C=c3}]",
				data.toString());

		Execute.update(h2.getConnection(),
				createStatements(
						"delete from t1",
						Collections.emptyList(),
						"insert into t1 (t1a, t1b, t1c) values (?,?,?)",
						createParameters(INTEGER, Types.INTEGER, 2,
								STRING, Types.VARCHAR, "b2",
								STRING, Types.VARCHAR, null),
						"insert into t1 (t1a, t1b, t1c) values (?,?,?)",
						createParameters(INTEGER, Types.INTEGER, 3,
								STRING, Types.VARCHAR, "b3",
								STRING, Types.VARCHAR, "c3")
				));

		data = h2.db.queryForList("select * from t1");
		assertEquals("After", "[{T1A=2, T1B=b2, T1C=null}, {T1A=3, T1B=b3, T1C=c3}]", data.toString());

		h2.db.execute("drop table t1");
	}

	private Date getDate(String data) {
		return new Date(java.util.Date.from(
				LocalDate.parse(data).atStartOfDay(ZoneId.systemDefault()).toInstant()
		).getTime());
	}

	private Time getTime(String data) {
		return new Time(java.util.Date.from(
				LocalTime.parse(data).atDate(LocalDate.of(1970, 1, 1)).
						atZone(ZoneId.systemDefault()).toInstant()
		).getTime());
	}

	private Timestamp getTimestamp(String data) {
		return new Timestamp(java.util.Date.from(
				LocalDateTime.parse(data).atZone(ZoneId.systemDefault()).toInstant()
		).getTime());
	}

	@Test
	public void insertDataTypesH2() throws UnsupportedEncodingException {
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

		List<Map<String, Object>> data;
		data = h2.db.queryForList("select * from data_types");
		assertEquals("Before", "[]", data.toString());

		String sql = "insert into data_types (id," +
				"bit_type,boolean_type,tinyint_type,smallint_type,integer_type,bigint_type," +
				"real_type,float_type,double_type,numeric_type,decimal_type," +
				"date_type,time_type,timestamp_type,char_type,varchar_type,longvarchar_type," +
				"nchar_type,nvarchar_type,longnvarchar_type,binary_type,varbinary_type,longvarbinary_type," +
				"clob_type,nclob_type,blob_type)" +
				" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Execute.update(h2.getConnection(),
				createStatements(
						sql,
						createParameters(INTEGER, Types.INTEGER, 1,
								BOOLEAN, Types.BIT, false,
								BOOLEAN, Types.BOOLEAN, true,
								BYTE, Types.TINYINT, (byte) 123,
								SHORT, Types.SMALLINT, (short) 12345,
								INTEGER, Types.INTEGER, 1234567890,
								LONG, Types.BIGINT, 10_000_000_000L,
								FLOAT, Types.REAL, (float) 123456.7890123,
								FLOAT, Types.REAL, (float) 123456.7890123,
								DOUBLE, Types.DOUBLE, 123456.7890123,
								BIGDECIMAL, Types.NUMERIC, new BigDecimal("123456.7890123"),
								BIGDECIMAL, Types.DECIMAL, new BigDecimal("234567.8901234"),
								DATE, Types.DATE, getDate("2015-12-31"),
								TIME, Types.TIME, getTime("12:34:56"),
								TIMESTAMP, Types.TIMESTAMP, getTimestamp("2015-12-31T12:34:56"),
								STRING, Types.CHAR, "char1",
								STRING, Types.VARCHAR, "varchar1",
								STRING, Types.LONGVARCHAR, "longvarchar1",
								STRING, Types.NCHAR, "nchar1",
								STRING, Types.NVARCHAR, "nvarchar1",
								STRING, Types.LONGNVARCHAR, "longnvarchar1",
								BYTES, Types.BINARY, "binary1".getBytes("UTF-8"),
								BYTES, Types.VARBINARY, "varbin1".getBytes("UTF-8"),
								BYTES, Types.LONGVARBINARY, "longvarbinary1".getBytes("UTF-8"),
								CLOB, Types.CLOB, new StringReader("clob1"),
								NCLOB, Types.NCLOB, new StringReader("nclob1"),
								BLOB, Types.BLOB, new ByteArrayInputStream("blob1".getBytes())
						),
						sql,
						createParameters(INTEGER, Types.INTEGER, 2,
								BOOLEAN, Types.BIT, null,
								BOOLEAN, Types.BOOLEAN, null,
								BYTE, Types.TINYINT, null,
								SHORT, Types.SMALLINT, null,
								INTEGER, Types.INTEGER, null,
								LONG, Types.BIGINT, null,
								FLOAT, Types.REAL, null,
								FLOAT, Types.REAL, null,
								DOUBLE, Types.DOUBLE, null,
								BIGDECIMAL, Types.NUMERIC, null,
								BIGDECIMAL, Types.DECIMAL, null,
								DATE, Types.DATE, null,
								TIME, Types.TIME, null,
								TIMESTAMP, Types.TIMESTAMP, null,
								STRING, Types.CHAR, null,
								STRING, Types.VARCHAR, null,
								STRING, Types.LONGVARCHAR, null,
								STRING, Types.NCHAR, null,
								STRING, Types.NVARCHAR, null,
								STRING, Types.LONGNVARCHAR, null,
								BYTES, Types.BINARY, null,
								BYTES, Types.VARBINARY, null,
								BYTES, Types.LONGVARBINARY, null,
								CLOB, Types.CLOB, null,
								NCLOB, Types.NCLOB, null,
								BLOB, Types.BLOB, null
						)
				));

		data = h2.db.queryForList("select * from data_types");
		String expected = "[{ID=1, BIT_TYPE=false, BOOLEAN_TYPE=true," +
				" TINYINT_TYPE=123, SMALLINT_TYPE=12345, INTEGER_TYPE=1234567890, BIGINT_TYPE=10000000000," +
				" REAL_TYPE=123456.79, FLOAT_TYPE=123456.7890625, DOUBLE_TYPE=123456.7890123," +
				" NUMERIC_TYPE=123456.79, DECIMAL_TYPE=234567.89," +
				" DATE_TYPE=2015-12-31, TIME_TYPE=12:34:56, TIMESTAMP_TYPE=2015-12-31 12:34:56.0," +
				" CHAR_TYPE=char1, VARCHAR_TYPE=varchar1, LONGVARCHAR_TYPE=longvarchar1," +
				" NCHAR_TYPE=nchar1, NVARCHAR_TYPE=nvarchar1, LONGNVARCHAR_TYPE=longnvarchar1," +
				" BINARY_TYPE=REPLACED, VARBINARY_TYPE=REPLACED, LONGVARBINARY_TYPE=REPLACED," +
				" CLOB_TYPE=clob1, NCLOB_TYPE=nclob1, BLOB_TYPE=REPLACED}," +
				" {ID=2, BIT_TYPE=null, BOOLEAN_TYPE=null," +
				" TINYINT_TYPE=null, SMALLINT_TYPE=null, INTEGER_TYPE=null, BIGINT_TYPE=null," +
				" REAL_TYPE=null, FLOAT_TYPE=null, DOUBLE_TYPE=null, NUMERIC_TYPE=null, DECIMAL_TYPE=null," +
				" DATE_TYPE=null, TIME_TYPE=null, TIMESTAMP_TYPE=null," +
				" CHAR_TYPE=null, VARCHAR_TYPE=null, LONGVARCHAR_TYPE=null," +
				" NCHAR_TYPE=null, NVARCHAR_TYPE=null, LONGNVARCHAR_TYPE=null," +
				" BINARY_TYPE=null, VARBINARY_TYPE=null, LONGVARBINARY_TYPE=null," +
				" CLOB_TYPE=null, NCLOB_TYPE=null, BLOB_TYPE=null}]";
		assertEquals("After", expected.replace(", ", ",\n "),
				data.toString().replaceAll("\\[B@[^,}]+", "REPLACED").replace(", ", ",\n "));

		h2.db.execute("drop table data_types");
	}

	@Test
	public void insertDataTypesHsql() throws UnsupportedEncodingException {
		DbTemplate h2 = new DbTemplate("jdbc:hsqldb:mem:test", "sa", "");
		h2.db.execute("create table data_types (id int primary key," +
				" bit_type bit, boolean_type boolean," +
				" tinyint_type tinyint, smallint_type smallint, integer_type integer, bigint_type bigint," +
				" real_type real, float_type double, double_type double," +
				" numeric_type numeric(20,2), decimal_type decimal(20,2)," +
				" date_type date, time_type time, timestamp_type timestamp," +
				" char_type char(20), varchar_type varchar(50), longvarchar_type longvarchar(5000)," +
				" binary_type binary(8), varbinary_type varbinary(8), longvarbinary_type longvarbinary(5000)," +
				" clob_type clob, blob_type blob)");

		List<Map<String, Object>> data;
		data = h2.db.queryForList("select * from data_types");
		assertEquals("Before", "[]", data.toString());

		String sql = "insert into data_types (id," +
				"bit_type,boolean_type,tinyint_type,smallint_type,integer_type,bigint_type," +
				"real_type,float_type,double_type,numeric_type,decimal_type," +
				"date_type,time_type,timestamp_type,char_type,varchar_type,longvarchar_type," +
				"binary_type,varbinary_type,longvarbinary_type," +
				"clob_type,blob_type)" +
				" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Execute.update(h2.getConnection(),
				createStatements(
						sql,
						createParameters(INTEGER, Types.INTEGER, 1,
								BOOLEAN, Types.BIT, false,
								BOOLEAN, Types.BOOLEAN, true,
								BYTE, Types.TINYINT, (byte) 123,
								SHORT, Types.SMALLINT, (short) 12345,
								INTEGER, Types.INTEGER, 1234567890,
								LONG, Types.BIGINT, 10_000_000_000L,
								FLOAT, Types.REAL, (float) 123456.7890123,
								FLOAT, Types.REAL, (float) 123456.7890123,
								DOUBLE, Types.DOUBLE, 123456.7890123,
								BIGDECIMAL, Types.NUMERIC, new BigDecimal("123456.7890123"),
								BIGDECIMAL, Types.DECIMAL, new BigDecimal("234567.8901234"),
								DATE, Types.DATE, getDate("2015-12-31"),
								TIME, Types.TIME, getTime("12:34:56"),
								TIMESTAMP, Types.TIMESTAMP, getTimestamp("2015-12-31T12:34:56"),
								STRING, Types.CHAR, "char1",
								STRING, Types.VARCHAR, "varchar1",
								STRING, Types.LONGVARCHAR, "longvarchar1",
								BYTES, Types.BINARY, "binary1".getBytes("UTF-8"),
								BYTES, Types.VARBINARY, "varbin1".getBytes("UTF-8"),
								BYTES, Types.LONGVARBINARY, "longvarbinary1".getBytes("UTF-8"),
								CLOB, Types.CLOB, new StringReader("clob1"),
								BLOB, Types.BLOB, new ByteArrayInputStream("blob1".getBytes())
						),
						sql,
						createParameters(INTEGER, Types.INTEGER, 2,
								BOOLEAN, Types.BIT, null,
								BOOLEAN, Types.BOOLEAN, null,
								BYTE, Types.TINYINT, null,
								SHORT, Types.SMALLINT, null,
								INTEGER, Types.INTEGER, null,
								LONG, Types.BIGINT, null,
								FLOAT, Types.REAL, null,
								FLOAT, Types.REAL, null,
								DOUBLE, Types.DOUBLE, null,
								BIGDECIMAL, Types.NUMERIC, null,
								BIGDECIMAL, Types.DECIMAL, null,
								DATE, Types.DATE, null,
								TIME, Types.TIME, null,
								TIMESTAMP, Types.TIMESTAMP, null,
								STRING, Types.CHAR, null,
								STRING, Types.VARCHAR, null,
								STRING, Types.LONGVARCHAR, null,
								BYTES, Types.BINARY, null,
								BYTES, Types.VARBINARY, null,
								BYTES, Types.LONGVARBINARY, null,
								CLOB, Types.CLOB, null,
								BLOB, Types.BLOB, null
						)
				));

		data = h2.db.queryForList("select * from data_types");
		String expected = "[{ID=1, BIT_TYPE=false, BOOLEAN_TYPE=true," +
				" TINYINT_TYPE=123, SMALLINT_TYPE=12345, INTEGER_TYPE=1234567890, BIGINT_TYPE=10000000000," +
				" REAL_TYPE=123456.7890625, FLOAT_TYPE=123456.7890625, DOUBLE_TYPE=123456.7890123," +
				" NUMERIC_TYPE=123456.79, DECIMAL_TYPE=234567.89," +
				" DATE_TYPE=2015-12-31, TIME_TYPE=12:34:56, TIMESTAMP_TYPE=2015-12-31 12:34:56.0," +
				" CHAR_TYPE=char1               , VARCHAR_TYPE=varchar1, LONGVARCHAR_TYPE=longvarchar1," +
				" BINARY_TYPE=REPLACED, VARBINARY_TYPE=REPLACED, LONGVARBINARY_TYPE=REPLACED," +
				" CLOB_TYPE=clob1, BLOB_TYPE=REPLACED}," +
				" {ID=2, BIT_TYPE=null, BOOLEAN_TYPE=null," +
				" TINYINT_TYPE=null, SMALLINT_TYPE=null, INTEGER_TYPE=null, BIGINT_TYPE=null," +
				" REAL_TYPE=null, FLOAT_TYPE=null, DOUBLE_TYPE=null, NUMERIC_TYPE=null, DECIMAL_TYPE=null," +
				" DATE_TYPE=null, TIME_TYPE=null, TIMESTAMP_TYPE=null," +
				" CHAR_TYPE=null, VARCHAR_TYPE=null, LONGVARCHAR_TYPE=null," +
				" BINARY_TYPE=null, VARBINARY_TYPE=null, LONGVARBINARY_TYPE=null," +
				" CLOB_TYPE=null, BLOB_TYPE=null}]";
		assertEquals("After", expected.replace(", ", ",\n "),
				data.toString().replaceAll("\\[B@[^,}]+", "REPLACED").replace(", ", ",\n "));

		h2.db.execute("drop table data_types");
	}
}
