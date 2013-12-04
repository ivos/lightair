package unit.internal.util;

import static org.junit.Assert.*;

import java.sql.Date;
import java.sql.Time;

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
		assertEquals(0, ((Integer) g.generateAutoValue(DataType.INTEGER,
				"tableName1", "columnName1")).intValue());
	}

	@Test
	public void integer_0_1() {
		check(0, 1);
		assertEquals(1, ((Integer) g.generateAutoValue(DataType.INTEGER,
				"tableName1", "columnName1")).intValue());
	}

	@Test
	public void integer_0_9999999() {
		check(0, 9999999);
		assertEquals(9999999, ((Integer) g.generateAutoValue(DataType.INTEGER,
				"tableName1", "columnName1")).intValue());
	}

	@Test
	public void integer_0_1234567() {
		check(0, 1234567);
		assertEquals(1234567, ((Integer) g.generateAutoValue(DataType.INTEGER,
				"tableName1", "columnName1")).intValue());
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
	public void date_0_0() {
		check(0, 0);
		final Object actual = g.generateAutoValue(DataType.DATE, "tableName1",
				"columnName1");
		assertEquals(Date.class, actual.getClass());
		assertEquals("1900-01-01", actual.toString());
	}

	@Test
	public void date_0_1() {
		check(0, 1);
		final Object actual = g.generateAutoValue(DataType.DATE, "tableName1",
				"columnName1");
		assertEquals(Date.class, actual.getClass());
		assertEquals("1900-01-02", actual.toString());
	}

	@Test
	public void date_0_67891() {
		check(0, 67891);
		final Object actual = g.generateAutoValue(DataType.DATE, "tableName1",
				"columnName1");
		assertEquals(Date.class, actual.getClass());
		assertEquals("2085-11-17", actual.toString());
	}

	@Test
	public void date_0_72998() {
		check(0, 72998);
		final Object actual = g.generateAutoValue(DataType.DATE, "tableName1",
				"columnName1");
		assertEquals(Date.class, actual.getClass());
		assertEquals("2099-11-11", actual.toString());
	}

	@Test
	public void date_0_72999() {
		check(0, 72999);
		final Object actual = g.generateAutoValue(DataType.DATE, "tableName1",
				"columnName1");
		assertEquals(Date.class, actual.getClass());
		assertEquals("2099-11-12", actual.toString());
	}

	@Test
	public void date_0_73000() {
		check(0, 73000);
		final Object actual = g.generateAutoValue(DataType.DATE, "tableName1",
				"columnName1");
		assertEquals(Date.class, actual.getClass());
		assertEquals("1900-01-01", actual.toString());
	}

	@Test
	public void date_0_73001() {
		check(0, 73001);
		final Object actual = g.generateAutoValue(DataType.DATE, "tableName1",
				"columnName1");
		assertEquals(Date.class, actual.getClass());
		assertEquals("1900-01-02", actual.toString());
	}

	@Test
	public void date_0_9999999() {
		check(0, 9999999);
		final Object actual = g.generateAutoValue(DataType.DATE, "tableName1",
				"columnName1");
		assertEquals(Date.class, actual.getClass());
		assertEquals("2097-02-15", actual.toString());
	}

	@Test
	public void time_0_0() {
		check(0, 0);
		final Object actual = g.generateAutoValue(DataType.TIME, "tableName1",
				"columnName1");
		assertEquals(Time.class, actual.getClass());
		assertEquals("00:00:00", actual.toString());
	}

	@Test
	public void time_0_1() {
		check(0, 1);
		final Object actual = g.generateAutoValue(DataType.TIME, "tableName1",
				"columnName1");
		assertEquals(Time.class, actual.getClass());
		assertEquals("00:00:00", actual.toString());
	}

}
