package net.sf.lightair.internal.dbunit.dataset;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Replaces tokens read from XML datasets.
 * <p>
 * Replaces the following tokens:
 * <ul>
 * <li>@null with <code>null</code> value</li>
 * <li>@date with current date midnight</li>
 * <li>@time with current time on 1.1.1970</li>
 * <li>@timestamp with current timestamp</li>
 * </ul>
 */
public class TokenReplacingFilter {

	private final Logger log = LoggerFactory
			.getLogger(TokenReplacingFilter.class);

	private final static List<String> tokens = Arrays.asList(new String[] {
			"@null", "@date", "@time", "@timestamp" });

	/**
	 * Replace possible token in a value.
	 * 
	 * @param value
	 *            Value possibly containing a token
	 * @return Value or token replacement
	 */
	public Object replaceTokens(Object value) {
		if (tokens.contains(value)) {
			Object replaced = replaceToken((String) value);
			log.debug("Replaced token [{}] with value [{}].", value, replaced);
			return replaced;
		}
		return value;
	}

	private Object replaceToken(String value) {
		if ("@null".equals(value)) {
			return null;
		}
		if ("@date".equals(value)) {
			return new Date(new DateMidnight().getMillis());
		}
		if ("@time".equals(value)) {
			return new Time(new DateTime().withDate(1970, 1, 1)
					.withMillisOfSecond(0).getMillis());
		}
		if ("@timestamp".equals(value)) {
			return new Timestamp(new DateTime().getMillis());
		}
		throw new RuntimeException("Unknown token " + value);
	}

}
