package it.setup.core;

import static org.junit.Assert.*;
import it.common.DataTypesTestBase;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;

public abstract class DataTypesSetupTestBase extends DataTypesTestBase {

	public void perform() {
		assertEquals("Count", new Integer(4), db.queryForObject(
				"select count(*) from data_types", Integer.class));
		values = db.queryForList("select * from data_types");
		verify();
	}

	protected abstract void verify();

	protected void verifyRow(int id, String char_type, String varchar_type,
			Integer integer_type, DateMidnight date_type, LocalTime time_type,
			DateTime timestamp_type, Double double_type, Boolean boolean_type,
			Long bigint_type, BigDecimal decimal_type, String clob_type,
			String blob_type, String binary_type) {
		assertEquals("id " + id, id, values.get(id).get("id"));
		assertEquals("char_type " + id, char_type,
				values.get(id).get("char_type"));
		assertEquals("varchar_type " + id, varchar_type,
				values.get(id).get("varchar_type"));
		assertEquals("integer_type " + id, integer_type,
				values.get(id).get("integer_type"));
		if (null == date_type) {
			assertNull("date_type " + id, values.get(id).get("date_type"));
		} else {
			assertEquals("date_type " + id, date_type.toDate(), values.get(id)
					.get("date_type"));
		}
		if (null == time_type) {
			assertNull("time_type " + id, values.get(id).get("time_type"));
		} else {
			assertEquals(
					"time_type " + id,
					time_type,
					LocalTime.fromDateFields((Date) values.get(id).get(
							"time_type")));
		}
		if (null == timestamp_type) {
			assertNull("timestamp_type " + id,
					values.get(id).get("timestamp_type"));
		} else {
			assertEquals("timestamp_type " + id, timestamp_type.toDate(),
					values.get(id).get("timestamp_type"));
		}
		assertEquals("double_type type " + id, double_type,
				values.get(id).get("double_type"));
		assertEquals("boolean_type type " + id, boolean_type, values.get(id)
				.get("boolean_type"));
		assertEquals("bigint_type type " + id, bigint_type,
				values.get(id).get("bigint_type"));
		assertEquals("decimal_type type " + id, decimal_type, values.get(id)
				.get("decimal_type"));
		assertEquals("clob_type type " + id, clob_type,
				values.get(id).get("clob_type"));
		assertEquals("blob_type type " + id, blob_type,
				convertBytesToString(values.get(id).get("blob_type")));
		assertEquals("binary_type type " + id, binary_type,
				convertBytesToString(values.get(id).get("binary_type")));
	}

	protected String convertBytesToString(Object bytes) {
		final byte[] encodedBytes = Base64.encodeBase64(((byte[]) bytes));
		if (null == encodedBytes) {
			return null;
		}
		return new String(encodedBytes);
	}

}
