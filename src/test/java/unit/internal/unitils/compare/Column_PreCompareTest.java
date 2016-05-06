package unit.internal.unitils.compare;

import static org.junit.Assert.*;

import java.math.BigInteger;

import net.sf.lightair.internal.unitils.compare.Column;
import net.sf.lightair.internal.unitils.compare.VariableResolver;
import net.sf.seaf.test.jmock.JMockSupport;

import org.dbunit.dataset.datatype.DataType;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class Column_PreCompareTest extends JMockSupport {

	Column c;
	VariableResolver variableResolver;

	@Before
	public void before() {
		variableResolver = mock(VariableResolver.class);
	}

	@Test
	public void same() {
		c = new Column(null, null, null, 0, null, "value1");
		org.unitils.dbunit.dataset.Column other = new org.unitils.dbunit.dataset.Column(
				null, null, "value1");
		assertNull(c.preCompare(other, 0));
	}

	@Test
	public void nullOtherNotNull() {
		c = new Column(null, null, null, 0, null, null);
		org.unitils.dbunit.dataset.Column other = new org.unitils.dbunit.dataset.Column(
				null, null, "value1");
		assertNotNull(c.preCompare(other, 0));
	}

	@Test
	public void variable() {
		c = new Column(null, null, null, 0, null, "value1");
		c.setVariableResolver(variableResolver);
		org.unitils.dbunit.dataset.Column other = new org.unitils.dbunit.dataset.Column(
				null, null, null);

		check(new Expectations() {
			{
				one(variableResolver).isVariable("value1");
				will(returnValue(true));
			}
		});

		assertNotNull(c.preCompare(other, 0));
	}

	@Test
	public void castedValueEqual() {
		c = new Column(null, null, null, 0, null, "123");
		c.setVariableResolver(variableResolver);
		org.unitils.dbunit.dataset.Column other = new org.unitils.dbunit.dataset.Column(
				null, DataType.INTEGER, 123);

		check(new Expectations() {
			{
				one(variableResolver).isVariable(with(any(String.class)));
				will(returnValue(false));
			}
		});

		assertNull(c.preCompare(other, 0));
	}

	@Test
	public void castedValueNotEqual() {
		c = new Column(null, null, null, 0, null, "123");
		c.setVariableResolver(variableResolver);
		org.unitils.dbunit.dataset.Column other = new org.unitils.dbunit.dataset.Column(
				null, DataType.INTEGER, 124);

		check(new Expectations() {
			{
				one(variableResolver).isVariable(with(any(String.class)));
				will(returnValue(false));
			}
		});

		assertNotNull(c.preCompare(other, 0));
	}

  @Test
  public void castedValueEqual_HexInt() {
    c = new Column(null, null, null, 0, null, "0x6A");
    c.setVariableResolver(variableResolver);
    org.unitils.dbunit.dataset.Column other = new org.unitils.dbunit.dataset.Column(null, DataType.INTEGER, 0x6A);

    check(new Expectations() {
      {
        one(variableResolver).isVariable(with(any(String.class)));
        will(returnValue(false));
      }
    });

    assertNull(c.preCompare(other, 0));
  }

  @Test
  public void castedValueEqual_HexBigInt() {
    c = new Column(null, null, null, 0, null, "0x100000000");
    c.setVariableResolver(variableResolver);
    org.unitils.dbunit.dataset.Column other = new org.unitils.dbunit.dataset.Column(null,
        DataType.BIGINT, new BigInteger("100000000", 16));

    check(new Expectations() {
      {
        one(variableResolver).isVariable(with(any(String.class)));
        will(returnValue(false));
      }
    });

    assertNull(c.preCompare(other, 0));
  }

  @Test
  public void castedValueEqual_DecBigInt() {
    c = new Column(null, null, null, 0, null, "100000000");
    c.setVariableResolver(variableResolver);
    org.unitils.dbunit.dataset.Column other = new org.unitils.dbunit.dataset.Column(null,
        DataType.BIGINT, new BigInteger("100000000"));

    check(new Expectations() {
      {
        one(variableResolver).isVariable(with(any(String.class)));
        will(returnValue(false));
      }
    });

    assertNull(c.preCompare(other, 0));
  }

  @Test
  public void castedValueEqual_BinBigInt() {
    c = new Column(null, null, null, 0, null, "0b100000000");
    c.setVariableResolver(variableResolver);
    org.unitils.dbunit.dataset.Column other = new org.unitils.dbunit.dataset.Column(null,
        DataType.BIGINT, new BigInteger("100000000", 2));

    check(new Expectations() {
      {
        one(variableResolver).isVariable(with(any(String.class)));
        will(returnValue(false));
      }
    });

    assertNull(c.preCompare(other, 0));
  }

  @Test
  @Ignore("TODO: Implement decoding signed values")
  public void castedValueEqual_NegHexBigInt() {
    c = new Column(null, null, null, 0, null, "-0x1F0000000");
    c.setVariableResolver(variableResolver);
    org.unitils.dbunit.dataset.Column other = new org.unitils.dbunit.dataset.Column(null,
        DataType.BIGINT, new BigInteger("-1F0000000", 16));

    check(new Expectations() {
      {
        one(variableResolver).isVariable(with(any(String.class)));
        will(returnValue(false));
      }
    });

    assertNull(c.preCompare(other, 0));
  }

}
