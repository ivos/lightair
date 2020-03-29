package it.setup.core;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.support.ConfigSupport;

import java.math.BigDecimal;

@RunWith(LightAir.class)
@Setup("DataTypesTest.xml")
public class DataTypesH2Test extends DataTypesSetupTestBase {

	static {
		connect("jdbc:h2:mem:test", "sa", "");
		ConfigSupport.restoreConfig();
	}

	@BeforeClass
	public static void beforeClass() {
		createTable();
	}

	@Test
	public void test() {
		perform();
	}

	@Override
	protected void verify() {
		// full
		verifyRow(0, "efghijklmnopqrs", "abcdefghijklmnopqrstuvxyz",
				12345678, new DateMidnight(2999, 12, 31),
				new LocalTime(23, 59, 58),
				new DateTime(2998, 11, 30, 22, 57, 56, 789),
				8765.4321, true, 9223372036854770000L,
				new BigDecimal("12345678901234.56"),
				"text1", "EjRWeJCrzeI=", "/ty6CYdlQyI=");
		// empty
		verifyRow(1, "", "", 0,
				new DateMidnight(2000, 1, 2),
				new LocalTime(0, 0, 0),
				new DateTime(2000, 1, 2, 3, 4, 5, 678),
				0., false, 0L,
				new BigDecimal("0.00"), "", "", "");
		// null
		verifyRow(2, null, null, null, null, null,
				null, null, null, null,
				null, null, null, null);
		// auto
		verifyRow(3, "char_type 1384656904", "varchar_type 1384684104",
				1384653604, DateMidnight.parse("1976-12-12"), LocalTime.parse("08:26:44"),
				DateTime.parse("1900-01-05T04:53:24.004"), 13846684.04, false,
				1384644904L, new BigDecimal("13846469.04"), "clob_type 1384603204",
				"YmxvYl90eXBlIDEzODQ2NzU0MDQ=", "ODQ2NTIzMDQ=");
	}
}
