package unit.internal.util;

import static org.junit.Assert.*;
import net.sf.lightair.exception.InvalidDurationFormatException;
import net.sf.lightair.internal.util.DurationParser;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

public class DurationParserTest {

	DurationParser p;
	DateTime from = new DateTime(2012, 3, 4, 5, 6, 7);

	@Before
	public void setup() {
		p = new DurationParser();
	}

	@Test
	public void plus() {
		assertEquals("All fields, single digit", plus(from, 3, 4, 5, 6, 7, 8),
				p.move(from, "+P3Y4M5DT6H7M8S").toString());
		assertEquals("All fields, multi digit",
				plus(from, 333, 444, 555, 666, 777, 888),
				p.move(from, "+P333Y444M555DT666H777M888S").toString());
		assertEquals("Years only", plus(from, 123, 0, 0, 0, 0, 0),
				p.move(from, "+P123Y").toString());
		assertEquals("Months only", plus(from, 0, 123, 0, 0, 0, 0),
				p.move(from, "+P123M").toString());
		assertEquals("Days only", plus(from, 0, 0, 123, 0, 0, 0),
				p.move(from, "+P123D").toString());
		assertEquals("Hours only", plus(from, 0, 0, 0, 123, 0, 0),
				p.move(from, "+PT123H").toString());
		assertEquals("Minutes only", plus(from, 0, 0, 0, 0, 123, 0),
				p.move(from, "+PT123M").toString());
		assertEquals("Seconds only", plus(from, 0, 0, 0, 0, 0, 123),
				p.move(from, "+PT123S").toString());
		assertEquals("Odd fields", plus(from, 3, 0, 4, 0, 5, 0),
				p.move(from, "+P3Y4DT5M").toString());
		assertEquals("Even fields", plus(from, 0, 3, 0, 4, 0, 5),
				p.move(from, "+P3MT4H5S").toString());
		assertEquals("Date fields only", plus(from, 3, 4, 5, 0, 0, 0),
				p.move(from, "+P3Y4M5D").toString());
		assertEquals("T but no time fields", plus(from, 3, 4, 5, 0, 0, 0), p
				.move(from, "+P3Y4M5DT").toString());
		assertEquals("Time fields only", plus(from, 0, 0, 0, 3, 4, 5),
				p.move(from, "+PT3H4M5S").toString());
		assertEquals("No fields", from.toString(), p.move(from, "+P")
				.toString());
	}

	private String plus(DateTime from, int years, int months, int days,
			int hours, int minutes, int seconds) {
		return from.plusYears(years).plusMonths(months).plusDays(days)
				.plusHours(hours).plusMinutes(minutes).plusSeconds(seconds)
				.toString();
	}

	@Test
	public void minus() {
		assertEquals("All fields, single digit", minus(from, 3, 4, 5, 6, 7, 8),
				p.move(from, "-P3Y4M5DT6H7M8S").toString());
		assertEquals("All fields, multi digit",
				minus(from, 333, 444, 555, 666, 777, 888),
				p.move(from, "-P333Y444M555DT666H777M888S").toString());
		assertEquals("Years only", minus(from, 123, 0, 0, 0, 0, 0),
				p.move(from, "-P123Y").toString());
		assertEquals("Months only", minus(from, 0, 123, 0, 0, 0, 0),
				p.move(from, "-P123M").toString());
		assertEquals("Days only", minus(from, 0, 0, 123, 0, 0, 0),
				p.move(from, "-P123D").toString());
		assertEquals("Hours only", minus(from, 0, 0, 0, 123, 0, 0),
				p.move(from, "-PT123H").toString());
		assertEquals("Minutes only", minus(from, 0, 0, 0, 0, 123, 0),
				p.move(from, "-PT123M").toString());
		assertEquals("Seconds only", minus(from, 0, 0, 0, 0, 0, 123),
				p.move(from, "-PT123S").toString());
		assertEquals("Odd fields", minus(from, 3, 0, 4, 0, 5, 0),
				p.move(from, "-P3Y4DT5M").toString());
		assertEquals("Even fields", minus(from, 0, 3, 0, 4, 0, 5),
				p.move(from, "-P3MT4H5S").toString());
		assertEquals("Date fields only", minus(from, 3, 4, 5, 0, 0, 0),
				p.move(from, "-P3Y4M5D").toString());
		assertEquals("T but no time fields", minus(from, 3, 4, 5, 0, 0, 0), p
				.move(from, "-P3Y4M5DT").toString());
		assertEquals("Time fields only", minus(from, 0, 0, 0, 3, 4, 5),
				p.move(from, "-PT3H4M5S").toString());
		assertEquals("No fields", from.toString(), p.move(from, "-P")
				.toString());
	}

	private String minus(DateTime from, int years, int months, int days,
			int hours, int minutes, int seconds) {
		return from.minusYears(years).minusMonths(months).minusDays(days)
				.minusHours(hours).minusMinutes(minutes).minusSeconds(seconds)
				.toString();
	}

	@Test
	public void err() {
		// "+P3Y4M5DT6H7M8S"
		verifyException("Null", null);
		verifyException("Empty", "");
		verifyException("Sign only", "+");
		verifyException("No P", "+Q3Y4M5DT6H7M8S");
		verifyException("No sign", "P3Y4M5DT6H7M8S");
		verifyException("Invalid sign", "~P3Y4M5DT6H7M8S");
		verifyException("S before T", "+P5S");
		verifyException("D after T", "+PT3D");
	}

	private void verifyException(String testCase, String duration) {
		try {
			p.move(new DateTime(2012, 3, 4, 5, 6, 7), duration);
			fail(testCase + ", should throw.");
		} catch (InvalidDurationFormatException e) {
			assertEquals(testCase, "Duration [" + duration
					+ "] does not match the required format.", e.getMessage());
		}
	}

}
