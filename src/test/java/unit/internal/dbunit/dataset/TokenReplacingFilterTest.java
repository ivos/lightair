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
		assertNull("Null", f.replaceTokens(null));
		assertEquals("Non String", new Integer(123), f.replaceTokens(123));
		assertEquals("Generic value", "value1", f.replaceTokens("value1"));

		assertEquals("@null", null, f.replaceTokens("@null"));

		assertEquals("@date", new DateMidnight(2009, 8, 28),
				new DateTime(f.replaceTokens("@date")));
		assertEquals("@time", new DateTime(1970, 1, 1, 19, 49, 59),
				new DateTime(f.replaceTokens("@time")));
		assertEquals("@timestamp", new DateTime(2009, 8, 28, 19, 49, 59, 987),
				new DateTime(f.replaceTokens("@timestamp")));

		assertEquals("@date + duration", new DateMidnight(2009, 8, 29),
				new DateTime(f.replaceTokens("@date+P1D")));
		assertEquals("@date - duration", new DateMidnight(2009, 7, 28),
				new DateTime(f.replaceTokens("@date-P1M")));
		assertEquals("@date and not duration", "@date~P1M",
				f.replaceTokens("@date~P1M"));

		assertEquals("@time + duration", new DateTime(1970, 1, 1, 20, 49, 59),
				new DateTime(f.replaceTokens("@time+PT1H")));
		assertEquals("@time - duration", new DateTime(1970, 1, 1, 19, 48, 59),
				new DateTime(f.replaceTokens("@time-PT1M")));
		assertEquals("@time and not duration", "@time~PT1M",
				f.replaceTokens("@time~PT1M"));

		assertEquals("@timestamp + duration", new DateTime(2010, 8, 28, 19, 50,
				0, 987), new DateTime(f.replaceTokens("@timestamp+P1YT1S")));
		assertEquals("@timestamp - duration", new DateTime(2009, 7, 28, 19, 48,
				59, 987), new DateTime(f.replaceTokens("@timestamp-P1MT1M")));
		assertEquals("@timestamp and not duration", "@timestamp~P1MT1M",
				f.replaceTokens("@timestamp~P1MT1M"));
	}

}
