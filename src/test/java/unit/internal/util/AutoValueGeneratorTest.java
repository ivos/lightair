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
						"columnname1", rowIndex);
				will(returnValue(autoNumber));
			}
		});
	}

	@Test
	public void integer_0() {
		check(0, 0);
		assertEquals("0", g.generateAutoValue(DataType.INTEGER, "tableName1",
				"columnname1", 0, null));
	}

	@Test
	public void integer_1() {
		check(0, 1);
		assertEquals("1", g.generateAutoValue(DataType.INTEGER, "tableName1",
				"columnname1", 0, null));
	}

	@Test
	public void integer_9999999() {
		check(0, 9999999);
		assertEquals("9999999", g.generateAutoValue(DataType.INTEGER,
				"tableName1", "columnname1", 0, null));
	}

	@Test
	public void integer_1234567() {
		check(0, 1234567);
		assertEquals("1234567", g.generateAutoValue(DataType.INTEGER,
				"tableName1", "columnname1", 0, null));
	}

	@Test
	public void char_0() {
		check(0, 0);
		assertEquals("columnname1 0000000", g.generateAutoValue(DataType.CHAR,
				"tableName1", "columnname1", 100, null));
	}

	@Test
	public void char_1() {
		check(0, 1);
		assertEquals("columnname1 0000001", g.generateAutoValue(DataType.CHAR,
				"tableName1", "columnname1", 100, null));
	}

	@Test
	public void char_1234567() {
		check(0, 1234567);
		assertEquals("columnname1 1234567", g.generateAutoValue(DataType.CHAR,
				"tableName1", "columnname1", 100, null));
	}

	@Test
	public void char_Cut1Prefix() {
		check(0, 1234567);
		assertEquals("columnname11234567", g.generateAutoValue(DataType.CHAR,
				"tableName1", "columnname1", 18, null));
	}

	@Test
	public void char_Cut2Prefix() {
		check(0, 1234567);
		assertEquals("columnname1234567", g.generateAutoValue(DataType.CHAR,
				"tableName1", "columnname1", 17, null));
	}

	@Test
	public void char_Cut3Prefix() {
		check(0, 1234567);
		assertEquals("columnnam1234567", g.generateAutoValue(DataType.CHAR,
				"tableName1", "columnname1", 16, null));
	}

	@Test
	public void char_Leave1Prefix() {
		check(0, 1234567);
		assertEquals("c1234567", g.generateAutoValue(DataType.CHAR,
				"tableName1", "columnname1", 8, null));
	}

	@Test
	public void char_CutWholePrefix() {
		check(0, 1234567);
		assertEquals("1234567", g.generateAutoValue(DataType.CHAR,
				"tableName1", "columnname1", 7, null));
	}

	@Test
	public void char_Cut1Number() {
		check(0, 1234567);
		assertEquals("234567", g.generateAutoValue(DataType.CHAR, "tableName1",
				"columnname1", 6, null));
	}

	@Test
	public void char_Cut2Number() {
		check(0, 1234567);
		assertEquals("34567", g.generateAutoValue(DataType.CHAR, "tableName1",
				"columnname1", 5, null));
	}

	@Test
	public void char_Leave3Number() {
		check(0, 1234567);
		assertEquals("567", g.generateAutoValue(DataType.CHAR, "tableName1",
				"columnname1", 3, null));
	}

	@Test
	public void char_Leave2Number() {
		check(0, 1234567);
		assertEquals("67", g.generateAutoValue(DataType.CHAR, "tableName1",
				"columnname1", 2, null));
	}

	@Test
	public void char_Leave1Number() {
		check(0, 1234567);
		assertEquals("7", g.generateAutoValue(DataType.CHAR, "tableName1",
				"columnname1", 1, null));
	}

	@Test
	public void char_Leave0Number() {
		check(0, 1234567);
		assertEquals("", g.generateAutoValue(DataType.CHAR, "tableName1",
				"columnname1", 0, null));
	}

	@Test
	public void char_ToLower() {
		check(new Expectations() {
			{
				one(autoNumberGenerator).generateAutoNumber("tableName1",
						"column_name1", 0);
				will(returnValue(0));
			}
		});

		assertEquals("column_name1 0000000", g.generateAutoValue(DataType.CHAR,
				"tableName1", "COLUMN_NAME1", 100, null));
	}

	@Test
	public void varchar_1234567() {
		check(0, 1234567);
		assertEquals("columnname1 1234567", g.generateAutoValue(
				DataType.VARCHAR, "tableName1", "columnname1", 100, null));
	}

	@Test
	public void varchar_Leave1Prefix() {
		check(0, 1234567);
		assertEquals("c1234567", g.generateAutoValue(DataType.VARCHAR,
				"tableName1", "columnname1", 8, null));
	}

	@Test
	public void varchar_Leave1Number() {
		check(0, 1234567);
		assertEquals("7", g.generateAutoValue(DataType.VARCHAR, "tableName1",
				"columnname1", 1, null));
	}

	@Test
	public void clob_1234567() {
		check(0, 1234567);
		assertEquals("columnname1 1234567", g.generateAutoValue(DataType.CLOB,
				"tableName1", "columnname1", 100, null));
	}

	@Test
	public void clob_Leave1Prefix() {
		check(0, 1234567);
		assertEquals("c1234567", g.generateAutoValue(DataType.CLOB,
				"tableName1", "columnname1", 8, null));
	}

	@Test
	public void clob_Leave1Number() {
		check(0, 1234567);
		assertEquals("7", g.generateAutoValue(DataType.CLOB, "tableName1",
				"columnname1", 1, null));
	}

	@Test
	public void blob_1234567() {
		check(0, 1234567);
		assertEquals("Y29sdW1ubmFtZTEgMTIzNDU2Nw==", g.generateAutoValue(
				DataType.BLOB, "tableName1", "columnname1", 100, null));
	}

	@Test
	public void blob_Leave1Prefix() {
		check(0, 1234567);
		assertEquals("YzEyMzQ1Njc=", g.generateAutoValue(DataType.BLOB,
				"tableName1", "columnname1", 8, null));
	}

	@Test
	public void blob_Leave1Number() {
		check(0, 1234567);
		assertEquals("Nw==", g.generateAutoValue(DataType.BLOB, "tableName1",
				"columnname1", 1, null));
	}

	@Test
	public void varbinary_1234567() {
		check(0, 1234567);
		assertEquals("Y29sdW1ubmFtZTEgMTIzNDU2Nw==", g.generateAutoValue(
				DataType.VARBINARY, "tableName1", "columnname1", 100, null));
	}

	@Test
	public void varbinary_Leave1Prefix() {
		check(0, 1234567);
		assertEquals("YzEyMzQ1Njc=", g.generateAutoValue(DataType.VARBINARY,
				"tableName1", "columnname1", 8, null));
	}

	@Test
	public void varbinary_Leave1Number() {
		check(0, 1234567);
		assertEquals("Nw==", g.generateAutoValue(DataType.VARBINARY,
				"tableName1", "columnname1", 1, null));
	}

	@Test
	public void date_0() {
		check(0, 0);
		assertEquals("1900-01-01", g.generateAutoValue(DataType.DATE,
				"tableName1", "columnname1", 0, null));
	}

	@Test
	public void date_1() {
		check(0, 1);
		assertEquals("1900-01-02", g.generateAutoValue(DataType.DATE,
				"tableName1", "columnname1", 0, null));
	}

	@Test
	public void date_67891() {
		check(0, 67891);
		assertEquals("2085-11-17", g.generateAutoValue(DataType.DATE,
				"tableName1", "columnname1", 0, null));
	}

	@Test
	public void date_72998() {
		check(0, 72998);
		assertEquals("2099-11-11", g.generateAutoValue(DataType.DATE,
				"tableName1", "columnname1", 0, null));
	}

	@Test
	public void date_72999() {
		check(0, 72999);
		assertEquals("2099-11-12", g.generateAutoValue(DataType.DATE,
				"tableName1", "columnname1", 0, null));
	}

	@Test
	public void date_73000() {
		check(0, 73000);
		assertEquals("1900-01-01", g.generateAutoValue(DataType.DATE,
				"tableName1", "columnname1", 0, null));
	}

	@Test
	public void date_73001() {
		check(0, 73001);
		assertEquals("1900-01-02", g.generateAutoValue(DataType.DATE,
				"tableName1", "columnname1", 0, null));
	}

	@Test
	public void date_9999999() {
		check(0, 9999999);
		assertEquals("2097-02-15", g.generateAutoValue(DataType.DATE,
				"tableName1", "columnname1", 0, null));
	}

	@Test
	public void time_0() {
		check(0, 0);
		assertEquals("00:00:00", g.generateAutoValue(DataType.TIME,
				"tableName1", "columnname1", 0, null));
	}

	@Test
	public void time_1() {
		check(0, 1);
		assertEquals("00:00:01", g.generateAutoValue(DataType.TIME,
				"tableName1", "columnname1", 0, null));
	}

	@Test
	public void time_86399() {
		check(0, 86399);
		assertEquals("23:59:59", g.generateAutoValue(DataType.TIME,
				"tableName1", "columnname1", 0, null));
	}

	@Test
	public void time_86400() {
		check(0, 86400);
		assertEquals("00:00:00", g.generateAutoValue(DataType.TIME,
				"tableName1", "columnname1", 0, null));
	}

	@Test
	public void time_86401() {
		check(0, 86401);
		assertEquals("00:00:01", g.generateAutoValue(DataType.TIME,
				"tableName1", "columnname1", 0, null));
	}

	@Test
	public void timestamp_0() {
		check(0, 0);
		assertEquals("1900-01-01 00:00:00.000", g.generateAutoValue(
				DataType.TIMESTAMP, "tableName1", "columnname1", 0, null));
	}

	@Test
	public void timestamp_1() {
		check(0, 1);
		assertEquals("1900-01-02 00:00:01.001", g.generateAutoValue(
				DataType.TIMESTAMP, "tableName1", "columnname1", 0, null));
	}

	@Test
	public void timestamp_2() {
		check(0, 2);
		assertEquals("1900-01-03 00:00:02.002", g.generateAutoValue(
				DataType.TIMESTAMP, "tableName1", "columnname1", 0, null));
	}

	@Test
	public void timestamp_10() {
		check(0, 10);
		assertEquals("1900-01-11 00:00:10.010", g.generateAutoValue(
				DataType.TIMESTAMP, "tableName1", "columnname1", 0, null));
	}

	@Test
	public void timestamp_100() {
		check(0, 100);
		assertEquals("1900-04-11 00:01:40.100", g.generateAutoValue(
				DataType.TIMESTAMP, "tableName1", "columnname1", 0, null));
	}

	@Test
	public void timestamp_999() {
		check(0, 999);
		assertEquals("1902-09-27 00:16:39.999", g.generateAutoValue(
				DataType.TIMESTAMP, "tableName1", "columnname1", 0, null));
	}

	@Test
	public void timestamp_1000() {
		check(0, 1000);
		assertEquals("1902-09-28 00:16:40.000", g.generateAutoValue(
				DataType.TIMESTAMP, "tableName1", "columnname1", 0, null));
	}

	@Test
	public void timestamp_72999() {
		check(0, 72999);
		assertEquals("2099-11-12 20:16:39.999", g.generateAutoValue(
				DataType.TIMESTAMP, "tableName1", "columnname1", 0, null));
	}

	@Test
	public void timestamp_73000() {
		check(0, 73000);
		assertEquals("1900-01-01 20:16:40.000", g.generateAutoValue(
				DataType.TIMESTAMP, "tableName1", "columnname1", 0, null));
	}

	@Test
	public void timestamp_86399() {
		check(0, 86399);
		assertEquals("1936-09-08 23:59:59.399", g.generateAutoValue(
				DataType.TIMESTAMP, "tableName1", "columnname1", 0, null));
	}

	@Test
	public void timestamp_86400() {
		check(0, 86400);
		assertEquals("1936-09-09 00:00:00.400", g.generateAutoValue(
				DataType.TIMESTAMP, "tableName1", "columnname1", 0, null));
	}

	@Test
	public void double_0() {
		check(0, 0);
		assertEquals("0.0", g.generateAutoValue(DataType.DOUBLE, "tableName1",
				"columnname1", 0, 2));
	}

	@Test
	public void double_1() {
		check(0, 1);
		assertEquals("0.01", g.generateAutoValue(DataType.DOUBLE, "tableName1",
				"columnname1", 0, 2));
	}

	@Test
	public void double_99() {
		check(0, 99);
		assertEquals("0.99", g.generateAutoValue(DataType.DOUBLE, "tableName1",
				"columnname1", 0, 2));
	}

	@Test
	public void double_100() {
		check(0, 100);
		assertEquals("1.0", g.generateAutoValue(DataType.DOUBLE, "tableName1",
				"columnname1", 0, 2));
	}

	@Test
	public void double_101() {
		check(0, 101);
		assertEquals("1.01", g.generateAutoValue(DataType.DOUBLE, "tableName1",
				"columnname1", 0, 2));
	}

	@Test
	public void double_9999999() {
		check(0, 9999999);
		assertEquals("99999.99", g.generateAutoValue(DataType.DOUBLE,
				"tableName1", "columnname1", 0, 2));
	}

	@Test
	public void boolean_0() {
		check(0, 0);
		assertEquals("false", g.generateAutoValue(DataType.BOOLEAN,
				"tableName1", "columnname1", 0, null));
	}

	@Test
	public void boolean_1() {
		check(0, 1);
		assertEquals("true", g.generateAutoValue(DataType.BOOLEAN,
				"tableName1", "columnname1", 0, null));
	}

	@Test
	public void boolean_2() {
		check(0, 2);
		assertEquals("false", g.generateAutoValue(DataType.BOOLEAN,
				"tableName1", "columnname1", 0, null));
	}

	@Test
	public void boolean_3() {
		check(0, 3);
		assertEquals("true", g.generateAutoValue(DataType.BOOLEAN,
				"tableName1", "columnname1", 0, null));
	}

	@Test
	public void boolean_9999998() {
		check(0, 9999998);
		assertEquals("false", g.generateAutoValue(DataType.BOOLEAN,
				"tableName1", "columnname1", 0, null));
	}

	@Test
	public void boolean_9999999() {
		check(0, 9999999);
		assertEquals("true", g.generateAutoValue(DataType.BOOLEAN,
				"tableName1", "columnname1", 0, null));
	}

	@Test
	public void bigint_0() {
		check(0, 0);
		assertEquals("0", g.generateAutoValue(DataType.BIGINT, "tableName1",
				"columnname1", 0, null));
	}

	@Test
	public void bigint_1() {
		check(0, 1);
		assertEquals("1", g.generateAutoValue(DataType.BIGINT, "tableName1",
				"columnname1", 0, null));
	}

	@Test
	public void bigint_9999999() {
		check(0, 9999999);
		assertEquals("9999999", g.generateAutoValue(DataType.BIGINT,
				"tableName1", "columnname1", 0, null));
	}

	@Test
	public void decimal_0() {
		check(0, 0);
		assertEquals("0.0", g.generateAutoValue(DataType.DECIMAL, "tableName1",
				"columnname1", 0, 2));
	}

	@Test
	public void decimal_1() {
		check(0, 1);
		assertEquals("0.01", g.generateAutoValue(DataType.DECIMAL,
				"tableName1", "columnname1", 0, 2));
	}

	@Test
	public void decimal_9999999() {
		check(0, 9999999);
		assertEquals("99999.99", g.generateAutoValue(DataType.DECIMAL,
				"tableName1", "columnname1", 0, 2));
	}

	@Test
	public void decimal_9999999_5() {
		check(0, 9999999);
		assertEquals("99999.99000", g.generateAutoValue(DataType.DECIMAL,
				"tableName1", "columnname1", 0, 5));
	}

	@Test
	public void rowIncremented() {
		check(0, 0);
		check(1, 10);
		check(2, 20);
		check(3, 30);
		assertEquals("0", g.generateAutoValue(DataType.INTEGER, "tableName1",
				"columnname1", 0, null));
		assertEquals("10", g.generateAutoValue(DataType.INTEGER, "tableName1",
				"columnname1", 0, null));
		assertEquals("20", g.generateAutoValue(DataType.INTEGER, "tableName1",
				"columnname1", 0, null));
		assertEquals("30", g.generateAutoValue(DataType.INTEGER, "tableName1",
				"columnname1", 0, null));
	}

}
