package net.sf.lightair.internal.util;

import java.util.regex.Pattern;

import net.sf.lightair.exception.InvalidDurationFormatException;

import org.joda.time.DateTime;

/**
 * Parse duration in ISO 8601 format and move a {@link DateTime} instance by the
 * duration parsed.
 */
public class DurationParser {

	/**
	 * Parses duration in ISO 8601 format and moves the given {@link DateTime}
	 * instance by the duration parsed.
	 * <p>
	 * The duration should be specified in the following format:
	 * <code>[+-]P[n]Y[n]M[n]DT[n]H[n]M[n]S</code>, where:
	 * <ul>
	 * <li>[+-] is either one of the signs and</li>
	 * <li>[n] is a sequence of digits (0-9).</li>
	 * </ul>
	 * The sign and P duration designator are mandatory, other fields are
	 * optional. The T time designator separates date fields from time fields.
	 * 
	 * @param from
	 *            {@link DateTime} instance
	 * @param duration
	 *            ISO 8601 duration
	 * @return
	 */
	public DateTime move(DateTime from, String duration) {
		final Pattern pattern = Pattern
				.compile("[+-]P((\\d)+Y)?((\\d)+M)?((\\d)+D)?(T((\\d)+H)?((\\d)+M)?((\\d)+S)?)?");
		if (null == duration || !pattern.matcher(duration).matches()) {
			throw new InvalidDurationFormatException(duration);
		}
		DateTime result = from;
		DurationFieldParser fieldParser = new DurationFieldParser(duration);
		while (fieldParser.parseField()) {
			switch (fieldParser.field) {
			case 'Y':
				result = result.plusYears(fieldParser.value);
				break;
			case 'M':
				result = result.plusMonths(fieldParser.value);
				break;
			case 'D':
				result = result.plusDays(fieldParser.value);
				break;
			case 'h':
				result = result.plusHours(fieldParser.value);
				break;
			case 'm':
				result = result.plusMinutes(fieldParser.value);
				break;
			case 's':
				result = result.plusSeconds(fieldParser.value);
				break;
			}
		}
		return result;
	}

	private class DurationFieldParser {

		private final String duration;
		private final int sign;
		private int index;
		private int value;
		private char field;
		private boolean inTime = false;

		private DurationFieldParser(String duration) {
			this.duration = duration;
			this.sign = ('+' == duration.charAt(0)) ? 1 : -1;
			index = duration.indexOf('P');
		}

		private boolean parseField() {
			if (index > duration.length() - 2) {
				return false;
			}
			int start = ++index;
			while (Character.isDigit(duration.charAt(index))) {
				index++;
			}
			field = duration.charAt(index);
			if (inTime) {
				field = Character.toLowerCase(field);
			}
			if ('T' == field) {
				inTime = true;
				return true;
			}
			value = Integer.valueOf(duration.substring(start, index)) * sign;
			return true;
		}
	}

}
