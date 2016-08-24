package unit.internal.db;

import net.sf.lightair.internal.Connections;
import net.sf.lightair.internal.Keywords;
import net.sf.lightair.internal.db.Structure;
import org.junit.Test;
import test.support.DbTemplate;
import unit.internal.ConnectionsTest;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class StructureTest implements Keywords {

	private Map<String, String> createProperties(
			String driverClassName, String url, String userName, String password, String dialect, String schema) {
		Map<String, String> properties = ConnectionsTest.createProperties(driverClassName, url, userName, password);
		properties.put(DATABASE_DIALECT, dialect);
		properties.put(DATABASE_SCHEMA, schema);
		return properties;
	}

	@Test
	public void test() {
		Map<String, Map<String, String>> properties = new HashMap<>();
		properties.put("profile1", createProperties(
				"org.h2.Driver", "jdbc:h2:mem:test", "sa", "", "h2", "PUBLIC"));
		properties.put("profile2", createProperties(
				"org.hsqldb.jdbc.JDBCDriver", "jdbc:hsqldb:mem:test", "sa", "", "hsql", "PUBLIC"));

		Map<String, Connection> connections = Connections.open(properties);

		DbTemplate h2 = new DbTemplate("jdbc:h2:mem:test", "sa", "");
		h2.db.execute("create table data_types (id int primary key," +
				" bit_type bit, boolean_type boolean," +
				" tinyint_type tinyint, smallint_type smallint, integer_type integer, bigint_type bigint," +
				" real_type real, float_type float, double_type double," +
				" numeric_type numeric(20,2), decimal_type decimal(20,2)," +
				" date_type date, time_type time, timestamp_type timestamp," +
				" char_type char(20), varchar_type varchar(50), longvarchar_type longvarchar(5000)," +
				" nchar_type nchar(20), nvarchar_type nvarchar(50), longnvarchar_type longnvarchar(5000)," +
				" binary_type binary(8), varbinary_type varbinary(8), longvarbinary_type longvarbinary(5000)," +
				" clob_type clob, nclob_type nclob, blob_type blob)");
		h2.db.execute("create table t1 (t1a int, t1b varchar2(10) not null, t1c varchar2(20))");

		DbTemplate hsql = new DbTemplate("jdbc:hsqldb:mem:test", "sa", "");
		hsql.db.execute("create table data_types (id int primary key," +
				" bit_type bit, boolean_type boolean," +
				" tinyint_type tinyint, smallint_type smallint, integer_type integer, bigint_type bigint," +
				" real_type real, float_type float, double_type double," +
				" numeric_type numeric(20,2), decimal_type decimal(20,2)," +
				" date_type date, time_type time, timestamp_type timestamp," +
				" char_type char(20), varchar_type varchar(50), longvarchar_type longvarchar(5000)," +
				" nvarchar_type nvarchar(50)," +
				" binary_type binary(8), varbinary_type varbinary(8), longvarbinary_type longvarbinary(5000)," +
				" clob_type clob, blob_type blob)");
		hsql.db.execute("create table t2 (t2a int, t2b varchar(10) not null, t2c varchar(20))");

		@SuppressWarnings("rawtypes")
		Map structures = Structure.loadAll(properties, connections);

		h2.db.execute("drop table data_types");
		h2.db.execute("drop table t1");
		hsql.db.execute("drop table data_types");
		hsql.db.execute("drop table t2");

		String expected = "{profile1={data_types={id={DATA_TYPE=INTEGER, JDBC_DATA_TYPE=4, NOT_NULL=true, SIZE=10, DECIMAL_DIGITS=0},\n" +
				" bit_type={DATA_TYPE=BOOLEAN, JDBC_DATA_TYPE=16, NOT_NULL=false, SIZE=1, DECIMAL_DIGITS=0},\n" +
				" boolean_type={DATA_TYPE=BOOLEAN, JDBC_DATA_TYPE=16, NOT_NULL=false, SIZE=1, DECIMAL_DIGITS=0},\n" +
				" tinyint_type={DATA_TYPE=BYTE, JDBC_DATA_TYPE=-6, NOT_NULL=false, SIZE=3, DECIMAL_DIGITS=0},\n" +
				" smallint_type={DATA_TYPE=SHORT, JDBC_DATA_TYPE=5, NOT_NULL=false, SIZE=5, DECIMAL_DIGITS=0},\n" +
				" integer_type={DATA_TYPE=INTEGER, JDBC_DATA_TYPE=4, NOT_NULL=false, SIZE=10, DECIMAL_DIGITS=0},\n" +
				" bigint_type={DATA_TYPE=LONG, JDBC_DATA_TYPE=-5, NOT_NULL=false, SIZE=19, DECIMAL_DIGITS=0},\n" +
				" real_type={DATA_TYPE=FLOAT, JDBC_DATA_TYPE=7, NOT_NULL=false, SIZE=7, DECIMAL_DIGITS=0},\n" +
				" float_type={DATA_TYPE=DOUBLE, JDBC_DATA_TYPE=8, NOT_NULL=false, SIZE=17, DECIMAL_DIGITS=0},\n" +
				" double_type={DATA_TYPE=DOUBLE, JDBC_DATA_TYPE=8, NOT_NULL=false, SIZE=17, DECIMAL_DIGITS=0},\n" +
				" numeric_type={DATA_TYPE=BIGDECIMAL, JDBC_DATA_TYPE=3, NOT_NULL=false, SIZE=20, DECIMAL_DIGITS=2},\n" +
				" decimal_type={DATA_TYPE=BIGDECIMAL, JDBC_DATA_TYPE=3, NOT_NULL=false, SIZE=20, DECIMAL_DIGITS=2},\n" +
				" date_type={DATA_TYPE=DATE, JDBC_DATA_TYPE=91, NOT_NULL=false, SIZE=8, DECIMAL_DIGITS=0},\n" +
				" time_type={DATA_TYPE=TIME, JDBC_DATA_TYPE=92, NOT_NULL=false, SIZE=6, DECIMAL_DIGITS=0},\n" +
				" timestamp_type={DATA_TYPE=TIMESTAMP, JDBC_DATA_TYPE=93, NOT_NULL=false, SIZE=23, DECIMAL_DIGITS=10},\n" +
				" char_type={DATA_TYPE=STRING, JDBC_DATA_TYPE=1, NOT_NULL=false, SIZE=20, DECIMAL_DIGITS=0},\n" +
				" varchar_type={DATA_TYPE=STRING, JDBC_DATA_TYPE=12, NOT_NULL=false, SIZE=50, DECIMAL_DIGITS=0},\n" +
				" longvarchar_type={DATA_TYPE=STRING, JDBC_DATA_TYPE=12, NOT_NULL=false, SIZE=5000, DECIMAL_DIGITS=0},\n" +
				" nchar_type={DATA_TYPE=STRING, JDBC_DATA_TYPE=1, NOT_NULL=false, SIZE=20, DECIMAL_DIGITS=0},\n" +
				" nvarchar_type={DATA_TYPE=STRING, JDBC_DATA_TYPE=12, NOT_NULL=false, SIZE=50, DECIMAL_DIGITS=0},\n" +
				" longnvarchar_type={DATA_TYPE=STRING, JDBC_DATA_TYPE=12, NOT_NULL=false, SIZE=5000, DECIMAL_DIGITS=0},\n" +
				" binary_type={DATA_TYPE=BYTES, JDBC_DATA_TYPE=-3, NOT_NULL=false, SIZE=8, DECIMAL_DIGITS=0},\n" +
				" varbinary_type={DATA_TYPE=BYTES, JDBC_DATA_TYPE=-3, NOT_NULL=false, SIZE=8, DECIMAL_DIGITS=0},\n" +
				" longvarbinary_type={DATA_TYPE=BYTES, JDBC_DATA_TYPE=-3, NOT_NULL=false, SIZE=5000, DECIMAL_DIGITS=0},\n" +
				" clob_type={DATA_TYPE=CLOB, JDBC_DATA_TYPE=2005, NOT_NULL=false, SIZE=2147483647, DECIMAL_DIGITS=0},\n" +
				" nclob_type={DATA_TYPE=CLOB, JDBC_DATA_TYPE=2005, NOT_NULL=false, SIZE=2147483647, DECIMAL_DIGITS=0},\n" +
				" blob_type={DATA_TYPE=BLOB, JDBC_DATA_TYPE=2004, NOT_NULL=false, SIZE=2147483647, DECIMAL_DIGITS=0}},\n" +
				" t1={t1a={DATA_TYPE=INTEGER, JDBC_DATA_TYPE=4, NOT_NULL=false, SIZE=10, DECIMAL_DIGITS=0},\n" +
				" t1b={DATA_TYPE=STRING, JDBC_DATA_TYPE=12, NOT_NULL=true, SIZE=10, DECIMAL_DIGITS=0},\n" +
				" t1c={DATA_TYPE=STRING, JDBC_DATA_TYPE=12, NOT_NULL=false, SIZE=20, DECIMAL_DIGITS=0}}},\n" +
				" profile2={data_types={id={DATA_TYPE=INTEGER, JDBC_DATA_TYPE=4, NOT_NULL=true, SIZE=32, DECIMAL_DIGITS=0},\n" +
				" bit_type={DATA_TYPE=BOOLEAN, JDBC_DATA_TYPE=-7, NOT_NULL=false, SIZE=1, DECIMAL_DIGITS=0},\n" +
				" boolean_type={DATA_TYPE=BOOLEAN, JDBC_DATA_TYPE=16, NOT_NULL=false, SIZE=0, DECIMAL_DIGITS=0},\n" +
				" tinyint_type={DATA_TYPE=BYTE, JDBC_DATA_TYPE=-6, NOT_NULL=false, SIZE=8, DECIMAL_DIGITS=0},\n" +
				" smallint_type={DATA_TYPE=SHORT, JDBC_DATA_TYPE=5, NOT_NULL=false, SIZE=16, DECIMAL_DIGITS=0},\n" +
				" integer_type={DATA_TYPE=INTEGER, JDBC_DATA_TYPE=4, NOT_NULL=false, SIZE=32, DECIMAL_DIGITS=0},\n" +
				" bigint_type={DATA_TYPE=LONG, JDBC_DATA_TYPE=-5, NOT_NULL=false, SIZE=64, DECIMAL_DIGITS=0},\n" +
				" real_type={DATA_TYPE=DOUBLE, JDBC_DATA_TYPE=8, NOT_NULL=false, SIZE=64, DECIMAL_DIGITS=0},\n" +
				" float_type={DATA_TYPE=DOUBLE, JDBC_DATA_TYPE=8, NOT_NULL=false, SIZE=64, DECIMAL_DIGITS=0},\n" +
				" double_type={DATA_TYPE=DOUBLE, JDBC_DATA_TYPE=8, NOT_NULL=false, SIZE=64, DECIMAL_DIGITS=0},\n" +
				" numeric_type={DATA_TYPE=BIGDECIMAL, JDBC_DATA_TYPE=2, NOT_NULL=false, SIZE=20, DECIMAL_DIGITS=2},\n" +
				" decimal_type={DATA_TYPE=BIGDECIMAL, JDBC_DATA_TYPE=3, NOT_NULL=false, SIZE=20, DECIMAL_DIGITS=2},\n" +
				" date_type={DATA_TYPE=DATE, JDBC_DATA_TYPE=91, NOT_NULL=false, SIZE=10, DECIMAL_DIGITS=0},\n" +
				" time_type={DATA_TYPE=TIME, JDBC_DATA_TYPE=92, NOT_NULL=false, SIZE=8, DECIMAL_DIGITS=0},\n" +
				" timestamp_type={DATA_TYPE=TIMESTAMP, JDBC_DATA_TYPE=93, NOT_NULL=false, SIZE=26, DECIMAL_DIGITS=0},\n" +
				" char_type={DATA_TYPE=STRING, JDBC_DATA_TYPE=1, NOT_NULL=false, SIZE=20, DECIMAL_DIGITS=0},\n" +
				" varchar_type={DATA_TYPE=STRING, JDBC_DATA_TYPE=12, NOT_NULL=false, SIZE=50, DECIMAL_DIGITS=0},\n" +
				" longvarchar_type={DATA_TYPE=STRING, JDBC_DATA_TYPE=12, NOT_NULL=false, SIZE=5000, DECIMAL_DIGITS=0},\n" +
				" nvarchar_type={DATA_TYPE=STRING, JDBC_DATA_TYPE=12, NOT_NULL=false, SIZE=50, DECIMAL_DIGITS=0},\n" +
				" binary_type={DATA_TYPE=BYTES, JDBC_DATA_TYPE=-2, NOT_NULL=false, SIZE=8, DECIMAL_DIGITS=0},\n" +
				" varbinary_type={DATA_TYPE=BYTES, JDBC_DATA_TYPE=-3, NOT_NULL=false, SIZE=8, DECIMAL_DIGITS=0},\n" +
				" longvarbinary_type={DATA_TYPE=BYTES, JDBC_DATA_TYPE=-3, NOT_NULL=false, SIZE=5000, DECIMAL_DIGITS=0},\n" +
				" clob_type={DATA_TYPE=CLOB, JDBC_DATA_TYPE=2005, NOT_NULL=false, SIZE=1073741824, DECIMAL_DIGITS=0},\n" +
				" blob_type={DATA_TYPE=BLOB, JDBC_DATA_TYPE=2004, NOT_NULL=false, SIZE=1073741824, DECIMAL_DIGITS=0}},\n" +
				" t2={t2a={DATA_TYPE=INTEGER, JDBC_DATA_TYPE=4, NOT_NULL=false, SIZE=32, DECIMAL_DIGITS=0},\n" +
				" t2b={DATA_TYPE=STRING, JDBC_DATA_TYPE=12, NOT_NULL=true, SIZE=10, DECIMAL_DIGITS=0},\n" +
				" t2c={DATA_TYPE=STRING, JDBC_DATA_TYPE=12, NOT_NULL=false, SIZE=20, DECIMAL_DIGITS=0}}}}";
		assertEquals(expected, structures.toString().replace("}, ", "},\n "));

		Connections.close(connections);
	}
}
