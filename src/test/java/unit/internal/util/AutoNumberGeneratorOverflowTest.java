package unit.internal.util;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import net.sf.lightair.exception.AutoValueColumnOverflowException;
import net.sf.lightair.exception.AutoValueTableOverflowException;
import net.sf.lightair.internal.util.AutoNumberGenerator;
import net.sf.lightair.internal.util.HashGenerator;

import org.junit.Before;
import org.junit.Test;

public class AutoNumberGeneratorOverflowTest {

	AutoNumberGenerator g;
	HashGenerator hashGenerator;
	Set<Integer> values;

	@Before
	public void setup() {
		g = new AutoNumberGenerator();
		hashGenerator = new HashGenerator();
		g.setHashGenerator(hashGenerator);
		values = new HashSet<Integer>();
	}

	@Test
	public void columnOverflow() {
		for (int i = 0; i < 100; i++) {
			final int autoNumber = g.generateAutoNumber("tableName1",
					"columnName" + i, 0);
			assertFalse(values.contains(autoNumber));
			values.add(autoNumber);
		}
		try {
			g.generateAutoNumber("tableName1", "columnName100", 0);
			fail("Should throw");
		} catch (AutoValueColumnOverflowException e) {
			assertEquals(
					"Number of columns with @auto value exceeds limit on table tableName1.",
					e.getMessage());
		}
	}

	@Test
	public void tableOverflow() {
		for (int i = 0; i < 1000; i++) {
			final int autoNumber = g.generateAutoNumber("tableName" + i,
					"columnName1", 0);
			assertFalse(values.contains(autoNumber));
			values.add(autoNumber);
		}
		try {
			g.generateAutoNumber("tableName1000", "columnName1", 0);
			fail("Should throw");
		} catch (AutoValueTableOverflowException e) {
			assertEquals(
					"Number of tables with @auto value exceeds limit with table tableName1000.",
					e.getMessage());
		}
	}

}
