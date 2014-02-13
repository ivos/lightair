package unit.internal.util;

import static org.junit.Assert.*;
import net.sf.lightair.exception.DuplicateAutoValueException;
import net.sf.lightair.internal.util.AutoValueGenerator;
import net.sf.lightair.internal.util.UniqueAutoValueGenerator;
import net.sf.seaf.test.jmock.JMockSupport;

import org.dbunit.dataset.datatype.DataType;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

public class UniqueAutoValueGeneratorTest extends JMockSupport {

	UniqueAutoValueGenerator g;
	AutoValueGenerator delegate;

	@Before
	public void setup() {
		g = new UniqueAutoValueGenerator();
		delegate = mock(AutoValueGenerator.class);
		g.setDelegate(delegate);
	}

	private void check(final String columnName, final DataType dataType,
			final String result) {
		check(new Expectations() {
			{
				one(delegate).generateAutoValue(dataType, "tableName1",
						columnName, 123, 456, 789);
				will(returnValue(result));
			}
		});
	}

	@Test
	public void generateAutoValue_SingleCall() {
		check("columnName1", DataType.INTEGER, "result1");

		assertEquals("result1", g.generateAutoValue(DataType.INTEGER,
				"tableName1", "columnName1", 123, 456, 789));
	}

	@Test
	public void generateAutoValue_MultipleCalls() {
		check("columnName1", DataType.INTEGER, "result1");
		check("columnName2", DataType.INTEGER, "result2");
		check("columnName3", DataType.INTEGER, "result3");

		assertEquals("result1", g.generateAutoValue(DataType.INTEGER,
				"tableName1", "columnName1", 123, 456, 789));
		assertEquals("result2", g.generateAutoValue(DataType.INTEGER,
				"tableName1", "columnName2", 123, 456, 789));
		assertEquals("result3", g.generateAutoValue(DataType.INTEGER,
				"tableName1", "columnName3", 123, 456, 789));
	}

	@Test
	public void generateAutoValue_DuplicateValue() {
		check("columnName1", DataType.INTEGER, "result1");
		check("columnName2", DataType.INTEGER, "result2");
		check("columnName3", DataType.INTEGER, "result3");
		check("columnName4", DataType.INTEGER, "result2");

		assertEquals("result1", g.generateAutoValue(DataType.INTEGER,
				"tableName1", "columnName1", 123, 456, 789));
		assertEquals("result2", g.generateAutoValue(DataType.INTEGER,
				"tableName1", "columnName2", 123, 456, 789));
		assertEquals("result3", g.generateAutoValue(DataType.INTEGER,
				"tableName1", "columnName3", 123, 456, 789));
		try {
			g.generateAutoValue(DataType.INTEGER, "tableName1", "columnName4",
					123, 456, 789);
			fail("Should throw");
		} catch (DuplicateAutoValueException e) {
			assertEquals(
					"Duplicate @auto value generated for tableName1.columnName4 as type INTEGER. "
							+ "Value [result2] has already been generated before. "
							+ "Please do not use @auto for this value to guarantee uniqueness.",
					e.getMessage());
		}
	}

	@Test
	public void generateAutoValue_DuplicateBoolean() {
		check("columnName1", DataType.BOOLEAN, "true");
		check("columnName2", DataType.BOOLEAN, "false");
		check("columnName3", DataType.BOOLEAN, "true");
		check("columnName4", DataType.BOOLEAN, "false");

		assertEquals("true", g.generateAutoValue(DataType.BOOLEAN,
				"tableName1", "columnName1", 123, 456, 789));
		assertEquals("false", g.generateAutoValue(DataType.BOOLEAN,
				"tableName1", "columnName2", 123, 456, 789));
		assertEquals("true", g.generateAutoValue(DataType.BOOLEAN,
				"tableName1", "columnName3", 123, 456, 789));
		assertEquals("false", g.generateAutoValue(DataType.BOOLEAN,
				"tableName1", "columnName4", 123, 456, 789));
	}

	@Test
	public void init_() {
		check("columnName1", DataType.INTEGER, "result1");
		check("columnName2", DataType.INTEGER, "result2");
		check("columnName3", DataType.INTEGER, "result3");
		check("columnName4", DataType.INTEGER, "result2");
		check("columnName5", DataType.INTEGER, "result1");
		check("columnName6", DataType.INTEGER, "result3");

		assertEquals("result1", g.generateAutoValue(DataType.INTEGER,
				"tableName1", "columnName1", 123, 456, 789));
		assertEquals("result2", g.generateAutoValue(DataType.INTEGER,
				"tableName1", "columnName2", 123, 456, 789));
		assertEquals("result3", g.generateAutoValue(DataType.INTEGER,
				"tableName1", "columnName3", 123, 456, 789));
		g.init();
		assertEquals("result2", g.generateAutoValue(DataType.INTEGER,
				"tableName1", "columnName4", 123, 456, 789));
		assertEquals("result1", g.generateAutoValue(DataType.INTEGER,
				"tableName1", "columnName5", 123, 456, 789));
		assertEquals("result3", g.generateAutoValue(DataType.INTEGER,
				"tableName1", "columnName6", 123, 456, 789));
	}

}
