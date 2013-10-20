package net.sf.lightair.internal.dbunit.dataset;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import net.sf.lightair.internal.util.DurationParser;

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
 * <li>@date[+-][ISO 8601 duration] with current date midnight moved by the
 * duration</li>
 * <li>@time[+-][ISO 8601 duration] with current time on 1.1.1970 moved by the
 * duration</li>
 * <li>@timestamp[+-][ISO 8601 duration] with current timestamp moved by the
 * duration</li>
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
		if (shouldReplace(value)) {
			Object replaced = replaceToken((String) value);
			log.debug("Replaced token [{}] with value [{}].", value, replaced);
			return replaced;
		}
		return value;
	}

	private boolean shouldReplace(Object value) {
		if (!(value instanceof String)) {
			return false;
		}
		return tokens.contains(value) || isDuration((String) value, "@date")
				|| isDuration((String) value, "@time")
				|| isDuration((String) value, "@timestamp");
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
		if (isDuration(value, "@date")) {
			final DateTime from = new DateMidnight().toDateTime();
			DateTime moved = durationParser.move(from, value.substring(5));
			return new Timestamp(moved.getMillis());
		}
		if (isDuration(value, "@time")) {
			final DateTime from = new DateTime().withDate(1970, 1, 1)
					.withMillisOfSecond(0);
			DateTime moved = durationParser.move(from, value.substring(5));
			return new Timestamp(moved.getMillis());
		}
		if (isDuration(value, "@timestamp")) {
			final DateTime from = new DateTime();
			DateTime moved = durationParser.move(from, value.substring(10));
			return new Timestamp(moved.getMillis());
		}
		throw new RuntimeException("Unknown token " + value);
	}

	private boolean isDuration(String value, String temporalType) {
		return value.startsWith(temporalType + "+")
				|| value.startsWith(temporalType + "-");
	}

	// beans and their setters

	private DurationParser durationParser = new DurationParser();

	public void setDurationParser(DurationParser durationParser) {
		this.durationParser = durationParser;
	}

}
