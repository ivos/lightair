package unit.internal.util;

import static org.junit.Assert.*;
import net.sf.lightair.internal.util.AutoNumberGenerator;
import net.sf.lightair.internal.util.AutoValueGenerator;
import net.sf.seaf.test.jmock.JMockSupport;

import org.dbunit.dataset.datatype.DataType;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

public class AutoValueGeneratorTest extends JMockSupport {

	AutoValueGenerator g;
	AutoNumberGenerator autoNumberGenerator;

	@Before
	public void setup() {
		g = new AutoValueGenerator();
		autoNumberGenerator = mock(AutoNumberGenerator.class);
		g.setAutoNumberGenerator(autoNumberGenerator);
	}

	private void check(final int rowIndex, final int autoNumber) {
		check(new Expectations() {
			{
				one(autoNumberGenerator).generateAutoNumber("tableName1",
						"columnName1", rowIndex);
				will(returnValue(autoNumber));
			}
		});
	}

	@Test
	public void integer_0_0() {
		check(0, 0);
		assertEquals("0", g.generateAutoValue(DataType.INTEGER, "tableName1",
				"columnName1"));
	}

	@Test
	public void integer_0_1() {
		check(0, 1);
		assertEquals("1", g.generateAutoValue(DataType.INTEGER, "tableName1",
				"columnName1"));
	}

	@Test
	public void integer_0_9999999() {
		check(0, 9999999);
		assertEquals("9999999", g.generateAutoValue(DataType.INTEGER,
				"tableName1", "columnName1"));
	}

	@Test
	public void integer_0_1234567() {
		check(0, 1234567);
		assertEquals("1234567", g.generateAutoValue(DataType.INTEGER,
				"tableName1", "columnName1"));
	}

	@Test
	public void char_0_0() {
		check(0, 0);
		assertEquals("columnName1 0000000",
				g.generateAutoValue(DataType.CHAR, "tableName1", "columnName1"));
	}

	@Test
	public void char_0_1() {
		check(0, 1);
		assertEquals("columnName1 0000001",
				g.generateAutoValue(DataType.CHAR, "tableName1", "columnName1"));
	}

	@Test
	public void char_0_1234567() {
		check(0, 1234567);
		assertEquals("columnName1 1234567",
				g.generateAutoValue(DataType.CHAR, "tableName1", "columnName1"));
	}

	@Test
	public void varchar_0_1234567() {
		check(0, 1234567);
		assertEquals("columnName1 1234567", g.generateAutoValue(
				DataType.VARCHAR, "tableName1", "columnName1"));
	}

	@Test
	public void clob_0_1234567() {
		check(0, 1234567);
		assertEquals("columnName1 1234567",
				g.generateAutoValue(DataType.CLOB, "tableName1", "columnName1"));
	}

	@Test
	public void blob_0_1234567() {
		check(0, 1234567);
		assertEquals("columnName1 1234567",
				g.generateAutoValue(DataType.BLOB, "tableName1", "columnName1"));
	}

	@Test
	public void varbinary_0_1234567() {
		check(0, 1234567);
		assertEquals("columnName1 1234567", g.generateAutoValue(
				DataType.VARBINARY, "tableName1", "columnName1"));
	}

	@Test
	public void date_0_0() {
		check(0, 0);
		assertEquals("1900-01-01",
				g.generateAutoValue(DataType.DATE, "tableName1", "columnName1"));
	}

	@Test
	public void date_0_1() {
		check(0, 1);
		assertEquals("1900-01-02",
				g.generateAutoValue(DataType.DATE, "tableName1", "columnName1"));
	}

	@Test
	public void date_0_67891() {
		check(0, 67891);
		assertEquals("2085-11-17",
				g.generateAutoValue(DataType.DATE, "tableName1", "columnName1"));
	}

	@Test
	public void date_0_72998() {
		check(0, 72998);
		assertEquals("2099-11-11",
				g.generateAutoValue(DataType.DATE, "tableName1", "columnName1"));
	}

	@Test
	public void date_0_72999() {
		check(0, 72999);
		assertEquals("2099-11-12",
				g.generateAutoValue(DataType.DATE, "tableName1", "columnName1"));
	}

	@Test
	public void date_0_73000() {
		check(0, 73000);
		assertEquals("1900-01-01",
				g.generateAutoValue(DataType.DATE, "tableName1", "columnName1"));
	}

	@Test
	public void date_0_73001() {
		check(0, 73001);
		assertEquals("1900-01-02",
				g.generateAutoValue(DataType.DATE, "tableName1", "columnName1"));
	}

	@Test
	public void date_0_9999999() {
		check(0, 9999999);
		assertEquals("2097-02-15",
				g.generateAutoValue(DataType.DATE, "tableName1", "columnName1"));
	}

	@Test
	public void time_0_0() {
		check(0, 0);
		assertEquals("00:00:00",
				g.generateAutoValue(DataType.TIME, "tableName1", "columnName1"));
	}

	@Test
	public void time_0_1() {
		check(0, 1);
		assertEquals("00:00:01",
				g.generateAutoValue(DataType.TIME, "tableName1", "columnName1"));
	}

	@Test
	public void time_0_86399() {
		check(0, 86399);
		assertEquals("23:59:59",
				g.generateAutoValue(DataType.TIME, "tableName1", "columnName1"));
	}

	@Test
	public void time_0_86400() {
		check(0, 86400);
		assertEquals("00:00:00",
				g.generateAutoValue(DataType.TIME, "tableName1", "columnName1"));
	}

	@Test
	public void time_0_86401() {
		check(0, 86401);
		assertEquals("00:00:01",
				g.generateAutoValue(DataType.TIME, "tableName1", "columnName1"));
	}

	@Test
	public void timestamp_0_0() {
		check(0, 0);
		assertEquals("1900-01-01 00:00:00.0", g.generateAutoValue(
				DataType.TIMESTAMP, "tableName1", "columnName1"));
	}

	@Test
	public void timestamp_0_1() {
		check(0, 1);
		assertEquals("1900-01-02 00:00:01.001", g.generateAutoValue(
				DataType.TIMESTAMP, "tableName1", "columnName1"));
	}

	@Test
	public void timestamp_0_2() {
		check(0, 2);
		assertEquals("1900-01-03 00:00:02.002", g.generateAutoValue(
				DataType.TIMESTAMP, "tableName1", "columnName1"));
	}

	@Test
	public void timestamp_0_10() {
		check(0, 10);
		assertEquals("1900-01-11 00:00:10.01", g.generateAutoValue(
				DataType.TIMESTAMP, "tableName1", "columnName1"));
	}

	@Test
	public void timestamp_0_100() {
		check(0, 100);
		assertEquals("1900-04-11 00:01:40.1", g.generateAutoValue(
				DataType.TIMESTAMP, "tableName1", "columnName1"));
	}

	@Test
	public void timestamp_0_999() {
		check(0, 999);
		assertEquals("1902-09-27 00:16:39.999", g.generateAutoValue(
				DataType.TIMESTAMP, "tableName1", "columnName1"));
	}

	@Test
	public void timestamp_0_1000() {
		check(0, 1000);
		assertEquals("1902-09-28 00:16:40.0", g.generateAutoValue(
				DataType.TIMESTAMP, "tableName1", "columnName1"));
	}

	@Test
	public void timestamp_0_72999() {
		check(0, 72999);
		assertEquals("2099-11-12 20:16:39.999", g.generateAutoValue(
				DataType.TIMESTAMP, "tableName1", "columnName1"));
	}

	@Test
	public void timestamp_0_73000() {
		check(0, 73000);
		assertEquals("1900-01-01 20:16:40.0", g.generateAutoValue(
				DataType.TIMESTAMP, "tableName1", "columnName1"));
	}

	@Test
	public void timestamp_0_86399() {
		check(0, 86399);
		assertEquals("1936-09-08 23:59:59.399", g.generateAutoValue(
				DataType.TIMESTAMP, "tableName1", "columnName1"));
	}

	@Test
	public void timestamp_0_86400() {
		check(0, 86400);
		assertEquals("1936-09-09 00:00:00.4", g.generateAutoValue(
				DataType.TIMESTAMP, "tableName1", "columnName1"));
	}

	@Test
	public void double_0_0() {
		check(0, 0);
		assertEquals("0.0", g.generateAutoValue(DataType.DOUBLE, "tableName1",
				"columnName1"));
	}

	@Test
	public void double_0_1() {
		check(0, 1);
		assertEquals("0.01", g.generateAutoValue(DataType.DOUBLE, "tableName1",
				"columnName1"));
	}

	@Test
	public void double_0_99() {
		check(0, 99);
		assertEquals("0.99", g.generateAutoValue(DataType.DOUBLE, "tableName1",
				"columnName1"));
	}

	@Test
	public void double_0_100() {
		check(0, 100);
		assertEquals("1.0", g.generateAutoValue(DataType.DOUBLE, "tableName1",
				"columnName1"));
	}

	@Test
	public void double_0_101() {
		check(0, 101);
		assertEquals("1.01", g.generateAutoValue(DataType.DOUBLE, "tableName1",
				"columnName1"));
	}

	@Test
	public void double_0_9999999() {
		check(0, 9999999);
		assertEquals("99999.99", g.generateAutoValue(DataType.DOUBLE,
				"tableName1", "columnName1"));
	}

	@Test
	public void boolean_0_0() {
		check(0, 0);
		assertEquals("false", g.generateAutoValue(DataType.BOOLEAN,
				"tableName1", "columnName1"));
	}

	@Test
	public void boolean_0_1() {
		check(0, 1);
		assertEquals("true", g.generateAutoValue(DataType.BOOLEAN,
				"tableName1", "columnName1"));
	}

	@Test
	public void boolean_0_2() {
		check(0, 2);
		assertEquals("false", g.generateAutoValue(DataType.BOOLEAN,
				"tableName1", "columnName1"));
	}

	@Test
	public void boolean_0_3() {
		check(0, 3);
		assertEquals("true", g.generateAutoValue(DataType.BOOLEAN,
				"tableName1", "columnName1"));
	}

	@Test
	public void boolean_0_9999998() {
		check(0, 9999998);
		assertEquals("false", g.generateAutoValue(DataType.BOOLEAN,
				"tableName1", "columnName1"));
	}

	@Test
	public void boolean_0_9999999() {
		check(0, 9999999);
		assertEquals("true", g.generateAutoValue(DataType.BOOLEAN,
				"tableName1", "columnName1"));
	}

	@Test
	public void bigint_0_0() {
		check(0, 0);
		assertEquals("0", g.generateAutoValue(DataType.BIGINT, "tableName1",
				"columnName1"));
	}

	@Test
	public void bigint_0_1() {
		check(0, 1);
		assertEquals("1", g.generateAutoValue(DataType.BIGINT, "tableName1",
				"columnName1"));
	}

	@Test
	public void bigint_0_9999999() {
		check(0, 9999999);
		assertEquals("9999999", g.generateAutoValue(DataType.BIGINT,
				"tableName1", "columnName1"));
	}

	@Test
	public void decimal_0_0() {
		check(0, 0);
		assertEquals("0", g.generateAutoValue(DataType.DECIMAL, "tableName1",
				"columnName1"));
	}

	@Test
	public void decimal_0_1() {
		check(0, 1);
		assertEquals("1", g.generateAutoValue(DataType.DECIMAL, "tableName1",
				"columnName1"));
	}

	@Test
	public void decimal_0_9999999() {
		check(0, 9999999);
		assertEquals("9999999", g.generateAutoValue(DataType.DECIMAL,
				"tableName1", "columnName1"));
	}
}
