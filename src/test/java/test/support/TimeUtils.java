package test.support;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeUtils {

	public static LocalDate toLocalDate(Object sqlDate) {
		return ((Date) sqlDate).toLocalDate();
	}

	public static LocalTime toLocalTime(Object sqlTime) {
		return ((Time) sqlTime).toLocalTime();
	}

	public static LocalDateTime toLocalDateTime(Object sqlTimestamp) {
		return ((Timestamp) sqlTimestamp).toLocalDateTime();
	}
}
