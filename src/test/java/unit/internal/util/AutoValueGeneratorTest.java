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

}
