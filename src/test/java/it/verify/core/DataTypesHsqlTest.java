package it.verify.core;

import it.common.DataTypesTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.support.ConfigSupport;

@RunWith(LightAir.class)
@Verify
public class DataTypesHsqlTest extends DataTypesTestBase {

	static {
		connect("jdbc:hsqldb:mem:test", "sa", "");
		ConfigSupport.replaceConfig("hsql");
	}

	@BeforeClass
	public static void beforeClass() {
		createTable();
	}

	@Test
	public void test() {
		db.execute("delete from data_types");
		db.update("insert into data_types values (0, 'efghijklmnopqrs', "
				+ "'abcdefghijklmnopqrstuvxyz', 12345678, "
				+ "'2999-12-31', '23:59:58', '2998-11-30 22:57:56.789', "
				+ "8765.4321, true, 9223372036854770000, "
				+ "12345678901234.56, 'text1', '1234567890abcde2', 'fedcba0987654322')");
		db.update("insert into data_types values (1, '', '', 0, "
				+ "'2000-01-02', '00:00:00', '2000-01-02 03:04:05.678', "
				+ "0, false, 0, 0, '', '', '')");
		db.update("insert into data_types values (2, null, null, null, "
				+ "null, null, null, "
				+ "null, null, null, null, null, null, null)");
	}

}
