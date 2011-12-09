package unit.internal.unitils.compare;

import static org.junit.Assert.*;
import net.sf.lightair.internal.unitils.compare.VariableResolver;

import org.junit.Test;

public class VariableResolverTest {

	VariableResolver r = new VariableResolver();

	@Test
	public void isVariable() {
		assertFalse("Not starts with $, not variable", r.isVariable("bla"));
		assertTrue("Starts with $, is variable", r.isVariable("$bla"));
	}

	@Test
	public void resolveValue_NotVariable() {
		assertEquals("expectedValue",
				r.resolveValue("expectedValue", "actualValue"));
	}

	@Test
	public void resolveValue_Variable() {
		assertEquals("Create variable", "actualValue0",
				r.resolveValue("$expectedValue", "actualValue0"));
		assertEquals("Use variable 1", "actualValue0",
				r.resolveValue("$expectedValue", "actualValue1"));
		assertEquals("Use variable 2", "actualValue0",
				r.resolveValue("$expectedValue", "actualValue2"));

		r.clear();
		assertEquals("Create variable with same key and new value",
				"actualValueNew0",
				r.resolveValue("$expectedValue", "actualValueNew0"));
		assertEquals("Use new variable", "actualValueNew0",
				r.resolveValue("$expectedValue", "actualValueNew1"));
	}

}
