package it.setup.core;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(LightAir.class)
@Setup("DataTypesTest.xml")
public class DataTypesH2Test extends DataTypesTestBase {

	static {
		db = connect("jdbc:h2:mem:test", "sa", "");
	}

	@BeforeClass
	public static void beforeClass() {
		createTable(db);
	}

	@Test
	public void test() {
		perform();
	}

}
