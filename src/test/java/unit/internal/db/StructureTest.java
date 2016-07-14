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

		Map structures = Structure.loadAll(properties, connections);

		String h2DataTypes = "{DATA_TYPES={ID={DATA_TYPE=INTEGER, JDBC_DATA_TYPE=4, NOT_NULL=true, SIZE=10, DECIMAL_DIGITS=0}," +
				" BIT_TYPE={DATA_TYPE=BOOLEAN, JDBC_DATA_TYPE=16, NOT_NULL=false, SIZE=1, DECIMAL_DIGITS=0}," +
				" BOOLEAN_TYPE={DATA_TYPE=BOOLEAN, JDBC_DATA_TYPE=16, NOT_NULL=false, SIZE=1, DECIMAL_DIGITS=0}," +
				" TINYINT_TYPE={DATA_TYPE=BYTE, JDBC_DATA_TYPE=-6, NOT_NULL=false, SIZE=3, DECIMAL_DIGITS=0}," +
				" SMALLINT_TYPE={DATA_TYPE=SHORT, JDBC_DATA_TYPE=5, NOT_NULL=false, SIZE=5, DECIMAL_DIGITS=0}," +
				" INTEGER_TYPE={DATA_TYPE=INTEGER, JDBC_DATA_TYPE=4, NOT_NULL=false, SIZE=10, DECIMAL_DIGITS=0}," +
				" BIGINT_TYPE={DATA_TYPE=LONG, JDBC_DATA_TYPE=-5, NOT_NULL=false, SIZE=19, DECIMAL_DIGITS=0}," +
				" REAL_TYPE={DATA_TYPE=FLOAT, JDBC_DATA_TYPE=7, NOT_NULL=false, SIZE=7, DECIMAL_DIGITS=0}," +
				" FLOAT_TYPE={DATA_TYPE=DOUBLE, JDBC_DATA_TYPE=8, NOT_NULL=false, SIZE=17, DECIMAL_DIGITS=0}," +
				" DOUBLE_TYPE={DATA_TYPE=DOUBLE, JDBC_DATA_TYPE=8, NOT_NULL=false, SIZE=17, DECIMAL_DIGITS=0}," +
				" NUMERIC_TYPE={DATA_TYPE=BIGDECIMAL, JDBC_DATA_TYPE=3, NOT_NULL=false, SIZE=20, DECIMAL_DIGITS=2}," +
				" DECIMAL_TYPE={DATA_TYPE=BIGDECIMAL, JDBC_DATA_TYPE=3, NOT_NULL=false, SIZE=20, DECIMAL_DIGITS=2}," +
				" DATE_TYPE={DATA_TYPE=DATE, JDBC_DATA_TYPE=91, NOT_NULL=false, SIZE=8, DECIMAL_DIGITS=0}," +
				" TIME_TYPE={DATA_TYPE=TIME, JDBC_DATA_TYPE=92, NOT_NULL=false, SIZE=6, DECIMAL_DIGITS=0}," +
				" TIMESTAMP_TYPE={DATA_TYPE=TIMESTAMP, JDBC_DATA_TYPE=93, NOT_NULL=false, SIZE=23, DECIMAL_DIGITS=10}," +
				" CHAR_TYPE={DATA_TYPE=STRING, JDBC_DATA_TYPE=1, NOT_NULL=false, SIZE=20, DECIMAL_DIGITS=0}," +
				" VARCHAR_TYPE={DATA_TYPE=STRING, JDBC_DATA_TYPE=12, NOT_NULL=false, SIZE=50, DECIMAL_DIGITS=0}," +
				" LONGVARCHAR_TYPE={DATA_TYPE=STRING, JDBC_DATA_TYPE=12, NOT_NULL=false, SIZE=5000, DECIMAL_DIGITS=0}," +
				" NCHAR_TYPE={DATA_TYPE=STRING, JDBC_DATA_TYPE=1, NOT_NULL=false, SIZE=20, DECIMAL_DIGITS=0}," +
				" NVARCHAR_TYPE={DATA_TYPE=STRING, JDBC_DATA_TYPE=12, NOT_NULL=false, SIZE=50, DECIMAL_DIGITS=0}," +
				" LONGNVARCHAR_TYPE={DATA_TYPE=STRING, JDBC_DATA_TYPE=12, NOT_NULL=false, SIZE=5000, DECIMAL_DIGITS=0}," +
				" BINARY_TYPE={DATA_TYPE=BYTES, JDBC_DATA_TYPE=-3, NOT_NULL=false, SIZE=8, DECIMAL_DIGITS=0}," +
				" VARBINARY_TYPE={DATA_TYPE=BYTES, JDBC_DATA_TYPE=-3, NOT_NULL=false, SIZE=8, DECIMAL_DIGITS=0}," +
				" LONGVARBINARY_TYPE={DATA_TYPE=BYTES, JDBC_DATA_TYPE=-3, NOT_NULL=false, SIZE=5000, DECIMAL_DIGITS=0}," +
				" CLOB_TYPE={DATA_TYPE=CLOB, JDBC_DATA_TYPE=2005, NOT_NULL=false, SIZE=2147483647, DECIMAL_DIGITS=0}," +
				" NCLOB_TYPE={DATA_TYPE=CLOB, JDBC_DATA_TYPE=2005, NOT_NULL=false, SIZE=2147483647, DECIMAL_DIGITS=0}," +
				" BLOB_TYPE={DATA_TYPE=BLOB, JDBC_DATA_TYPE=2004, NOT_NULL=false, SIZE=2147483647, DECIMAL_DIGITS=0}}";
		String hsqlDataTypes = "{DATA_TYPES={ID={DATA_TYPE=INTEGER, JDBC_DATA_TYPE=4, NOT_NULL=true, SIZE=32, DECIMAL_DIGITS=0}," +
				" BIT_TYPE={DATA_TYPE=BOOLEAN, JDBC_DATA_TYPE=-7, NOT_NULL=false, SIZE=1, DECIMAL_DIGITS=0}," +
				" BOOLEAN_TYPE={DATA_TYPE=BOOLEAN, JDBC_DATA_TYPE=16, NOT_NULL=false, SIZE=0, DECIMAL_DIGITS=0}," +
				" TINYINT_TYPE={DATA_TYPE=BYTE, JDBC_DATA_TYPE=-6, NOT_NULL=false, SIZE=8, DECIMAL_DIGITS=0}," +
				" SMALLINT_TYPE={DATA_TYPE=SHORT, JDBC_DATA_TYPE=5, NOT_NULL=false, SIZE=16, DECIMAL_DIGITS=0}," +
				" INTEGER_TYPE={DATA_TYPE=INTEGER, JDBC_DATA_TYPE=4, NOT_NULL=false, SIZE=32, DECIMAL_DIGITS=0}," +
				" BIGINT_TYPE={DATA_TYPE=LONG, JDBC_DATA_TYPE=-5, NOT_NULL=false, SIZE=64, DECIMAL_DIGITS=0}," +
				" REAL_TYPE={DATA_TYPE=DOUBLE, JDBC_DATA_TYPE=8, NOT_NULL=false, SIZE=64, DECIMAL_DIGITS=0}," +
				" FLOAT_TYPE={DATA_TYPE=DOUBLE, JDBC_DATA_TYPE=8, NOT_NULL=false, SIZE=64, DECIMAL_DIGITS=0}," +
				" DOUBLE_TYPE={DATA_TYPE=DOUBLE, JDBC_DATA_TYPE=8, NOT_NULL=false, SIZE=64, DECIMAL_DIGITS=0}," +
				" NUMERIC_TYPE={DATA_TYPE=BIGDECIMAL, JDBC_DATA_TYPE=2, NOT_NULL=false, SIZE=20, DECIMAL_DIGITS=2}," +
				" DECIMAL_TYPE={DATA_TYPE=BIGDECIMAL, JDBC_DATA_TYPE=3, NOT_NULL=false, SIZE=20, DECIMAL_DIGITS=2}," +
				" DATE_TYPE={DATA_TYPE=DATE, JDBC_DATA_TYPE=91, NOT_NULL=false, SIZE=10, DECIMAL_DIGITS=0}," +
				" TIME_TYPE={DATA_TYPE=TIME, JDBC_DATA_TYPE=92, NOT_NULL=false, SIZE=8, DECIMAL_DIGITS=0}," +
				" TIMESTAMP_TYPE={DATA_TYPE=TIMESTAMP, JDBC_DATA_TYPE=93, NOT_NULL=false, SIZE=26, DECIMAL_DIGITS=0}," +
				" CHAR_TYPE={DATA_TYPE=STRING, JDBC_DATA_TYPE=1, NOT_NULL=false, SIZE=20, DECIMAL_DIGITS=0}," +
				" VARCHAR_TYPE={DATA_TYPE=STRING, JDBC_DATA_TYPE=12, NOT_NULL=false, SIZE=50, DECIMAL_DIGITS=0}," +
				" LONGVARCHAR_TYPE={DATA_TYPE=STRING, JDBC_DATA_TYPE=12, NOT_NULL=false, SIZE=5000, DECIMAL_DIGITS=0}," +
				" NVARCHAR_TYPE={DATA_TYPE=STRING, JDBC_DATA_TYPE=12, NOT_NULL=false, SIZE=50, DECIMAL_DIGITS=0}," +
				" BINARY_TYPE={DATA_TYPE=BYTES, JDBC_DATA_TYPE=-2, NOT_NULL=false, SIZE=8, DECIMAL_DIGITS=0}," +
				" VARBINARY_TYPE={DATA_TYPE=BYTES, JDBC_DATA_TYPE=-3, NOT_NULL=false, SIZE=8, DECIMAL_DIGITS=0}," +
				" LONGVARBINARY_TYPE={DATA_TYPE=BYTES, JDBC_DATA_TYPE=-3, NOT_NULL=false, SIZE=5000, DECIMAL_DIGITS=0}," +
				" CLOB_TYPE={DATA_TYPE=CLOB, JDBC_DATA_TYPE=2005, NOT_NULL=false, SIZE=1073741824, DECIMAL_DIGITS=0}," +
				" BLOB_TYPE={DATA_TYPE=BLOB, JDBC_DATA_TYPE=2004, NOT_NULL=false, SIZE=1073741824, DECIMAL_DIGITS=0}}";
		String expected = "{profile1=" + h2DataTypes + "," +
				" T1={T1A={DATA_TYPE=INTEGER, JDBC_DATA_TYPE=4, NOT_NULL=false, SIZE=10, DECIMAL_DIGITS=0}," +
				" T1B={DATA_TYPE=STRING, JDBC_DATA_TYPE=12, NOT_NULL=true, SIZE=10, DECIMAL_DIGITS=0}," +
				" T1C={DATA_TYPE=STRING, JDBC_DATA_TYPE=12, NOT_NULL=false, SIZE=20, DECIMAL_DIGITS=0}}}," +
				" profile2=" + hsqlDataTypes + "," +
				" T2={T2A={DATA_TYPE=INTEGER, JDBC_DATA_TYPE=4, NOT_NULL=false, SIZE=32, DECIMAL_DIGITS=0}," +
				" T2B={DATA_TYPE=STRING, JDBC_DATA_TYPE=12, NOT_NULL=true, SIZE=10, DECIMAL_DIGITS=0}," +
				" T2C={DATA_TYPE=STRING, JDBC_DATA_TYPE=12, NOT_NULL=false, SIZE=20, DECIMAL_DIGITS=0}}}}";
		assertEquals(expected.replace("}, ", "},\n "), structures.toString().replace("}, ", "},\n "));

		Connections.close(connections);
	}
}
