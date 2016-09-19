package it.setup.core;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;

@RunWith(LightAir.class)
@Setup("DataTypesTest.xml")
public class DataTypesH2Test extends DataTypesSetupTestBase {

	static {
		connect("jdbc:h2:mem:test", "sa", "");
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
		verifyRow(0, "efghijklmnopqrs", "abcdefghijklmnopqrstuvxyz", 12345678,
				new DateMidnight(2999, 12, 31), new LocalTime(23, 59, 58),
				new DateTime(2998, 11, 30, 22, 57, 56, 789), 8765.4321, true,
				9223372036854770000L, new BigDecimal("12345678901234.56"),
				"text1", "EjRWeJCrzeI=", "/ty6CYdlQyI=");
		verifyRow(1, "", "", 0, new DateMidnight(2000, 1, 2), new LocalTime(0,
				0, 0), new DateTime(2000, 1, 2, 3, 4, 5, 678), 0., false, 0L,
				new BigDecimal("0.00"), "", "", "");
		verifyRow(2, null, null, null, null, null, null, null, null, null,
				null, null, null, null);
		verifyRow(3, "char_type 8466903", "varchar_type 8464103", 8463603,
				DateMidnight.parse("1903-01-09"), LocalTime.parse("23:53:23"),
				DateTime.parse("2088-12-03T23:06:43.003"), 84684.03, true,
				8464903L, new BigDecimal("84670.03"), "clob_type 8463203",
				"YmxvYl90eXBlIDg0NjU0MDM=", "Yjg0NjIzMDM=");
	}

}
