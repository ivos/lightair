package it.setup.core;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(LightAir.class)
@Setup("DataTypesTest.xml")
public class DataTypesDerbyTest extends DataTypesTestBase {

	static {
		db = connect("jdbc:derby:memory:target/data/test;create=true", "root",
				"root");
		replaceConfig("derby");
	}

	@BeforeClass
	public static void beforeClass() {
		// Bug in Spring JdbcTemplate: CLOB does not work
		createTable__NoClob(db);
	}

	@Test
	public void test() {
		perform();
	}

}
