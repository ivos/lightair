package it.setup.core;

import it.common.DataTypesTestBase;
import org.apache.commons.codec.binary.Base64;

import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public abstract class DataTypesSetupTestBase extends DataTypesTestBase {

	protected void perform() {
		assertEquals("Count", new Integer(4), db.queryForObject("select count(*) from data_types", Integer.class));
		values = db.queryForList("select * from data_types");
		verify();
	}

	protected abstract void verify();

	protected String convertBytesToString(Object bytes) {
		final byte[] encodedBytes = Base64.encodeBase64(((byte[]) bytes));
		if (null == encodedBytes) {
			return null;
		}
		return new String(encodedBytes);
	}

	protected <EV> void verifyField(
			int id, String name, EV expectedValue,
			Function<EV, ?> expectedConverter, Function<Object, ?> actualConverter) {
		Object rawActualValue = values.get(id).get(name);
		if (null == expectedValue) {
			assertNull(name + " " + id, rawActualValue);
		} else {
			assertEquals(name + " " + id, expectedConverter.apply(expectedValue),
					actualConverter.apply(rawActualValue));
		}
	}
}
