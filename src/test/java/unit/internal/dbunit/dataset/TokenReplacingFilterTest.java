package unit.internal.dbunit.dataset;

import static org.junit.Assert.*;
import net.sf.lightair.internal.dbunit.dataset.TokenReplacingFilter;

import org.junit.Test;

public class TokenReplacingFilterTest {

	@Test
	public void ok() {
		TokenReplacingFilter f = new TokenReplacingFilter();
		assertEquals("Generic value", "value1", f.replaceTokens("value1"));
		assertEquals("@null", null, f.replaceTokens("@null"));
	}

}
