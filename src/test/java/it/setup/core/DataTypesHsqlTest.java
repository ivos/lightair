package it.setup.core;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(LightAir.class)
@Setup("DataTypesTest.xml")
public class DataTypesHsqlTest extends DataTypesTestBase {

	static {
		db = connect("jdbc:hsqldb:target/data/test", "sa", "");
		replaceConfig("hsql");
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
