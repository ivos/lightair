package unit.internal.dbunit.dataset;

import static org.junit.Assert.*;
import net.sf.lightair.internal.dbunit.dataset.TokenReplacingFilter;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Test;

public class TokenReplacingFilterTest {

	@Test
	public void ok() {
		DateTimeUtils.setCurrentMillisFixed(new DateTime(2009, 8, 28, 19, 49,
				59, 987).getMillis());
		TokenReplacingFilter f = new TokenReplacingFilter();
		f.init();
		assertEquals("Generic value", "value1", f.replaceTokens("value1"));
		assertEquals("@null", null, f.replaceTokens("@null"));
		assertEquals("@date", new DateMidnight(2009, 8, 28),
				new DateTime(f.replaceTokens("@date")));
		assertEquals("@time", new DateTime(1970, 1, 1, 19, 49, 59),
				new DateTime(f.replaceTokens("@time")));
		assertEquals("@timestamp", new DateTime(2009, 8, 28, 19, 49, 59, 987),
				new DateTime(f.replaceTokens("@timestamp")));
	}

}
