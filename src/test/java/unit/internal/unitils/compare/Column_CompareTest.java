package unit.internal.unitils.compare;

import static org.junit.Assert.*;
import net.sf.lightair.internal.unitils.compare.Column;
import net.sf.lightair.internal.unitils.compare.VariableResolver;
import net.sf.seaf.test.jmock.JMockSupport;

import org.dbunit.dataset.datatype.DataType;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

public class Column_CompareTest extends JMockSupport {

	Column c;
	VariableResolver variableResolver;

	@Before
	public void before() {
		variableResolver = mock(VariableResolver.class);
	}

	@Test
	public void same() {
		c = new Column(null, null, "value1");
		org.unitils.dbunit.dataset.Column other = new org.unitils.dbunit.dataset.Column(
				null, null, "value1");
		assertNull(c.compare(other));
	}

	@Test
	public void nullOtherNotNull() {
		c = new Column(null, null, null);
		org.unitils.dbunit.dataset.Column other = new org.unitils.dbunit.dataset.Column(
				null, null, "value1");
		assertNotNull(c.compare(other));
	}

	@Test
	public void castedValueEqual() {
		c = new Column(null, null, "123");
		c.setVariableResolver(variableResolver);
		org.unitils.dbunit.dataset.Column other = new org.unitils.dbunit.dataset.Column(
				null, DataType.INTEGER, 321);

		check(new Expectations() {
			{
				one(variableResolver).resolveValue("123", 321);
				will(returnValue(321));
			}
		});

		assertNull(c.compare(other));
	}

	@Test
	public void castedValueNotEqual() {
		c = new Column(null, null, "123");
		c.setVariableResolver(variableResolver);
		org.unitils.dbunit.dataset.Column other = new org.unitils.dbunit.dataset.Column(
				null, DataType.INTEGER, 321);

		check(new Expectations() {
			{
				one(variableResolver).resolveValue("123", 321);
				will(returnValue(320));
			}
		});

		assertNotNull(c.compare(other));
	}

}
