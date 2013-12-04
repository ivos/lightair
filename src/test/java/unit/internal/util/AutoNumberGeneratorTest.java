package unit.internal.util;

import static org.junit.Assert.*;
import net.sf.lightair.internal.util.AutoNumberGenerator;
import net.sf.lightair.internal.util.HashGenerator;
import net.sf.seaf.test.jmock.JMockSupport;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

public class AutoNumberGeneratorTest extends JMockSupport {

	AutoNumberGenerator g;
	HashGenerator hashGenerator;

	@Before
	public void setup() {
		g = new AutoNumberGenerator();
		hashGenerator = mock(HashGenerator.class);
		g.setHashGenerator(hashGenerator);
	}

	private void check(final int tableHash, final int columnHash) {
		check(new Expectations() {
			{
				one(hashGenerator).generateHash("tableName1", 3);
				will(returnValue(tableHash));

				one(hashGenerator).generateHash("columnName1", 2);
				will(returnValue(columnHash));
			}
		});
	}

	@Test
	public void generateAutoNumber_0_0_0() {
		check(0, 0);
		assertEquals(0, g.generateAutoNumber("tableName1", "columnName1", 0));
	}

	@Test
	public void generateAutoNumber_1_1_1() {
		check(1, 1);
		assertEquals(10101,
				g.generateAutoNumber("tableName1", "columnName1", 1));
	}

	@Test
	public void generateAutoNumber_999_99_99() {
		check(999, 99);
		assertEquals(9999999,
				g.generateAutoNumber("tableName1", "columnName1", 99));
	}

	@Test
	public void generateAutoNumber_123_45_67() {
		check(123, 45);
		assertEquals(1234567,
				g.generateAutoNumber("tableName1", "columnName1", 67));
	}

}
