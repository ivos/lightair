package it.setup.core;

import static org.junit.Assert.*;
import it.common.CommonTestBase;

import java.util.List;
import java.util.Map;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(LightAir.class)
@Setup
public class AutoStringCutTest extends CommonTestBase {

	List<Map<String, Object>> values;

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table cut1char (char_type char(16), varchar_type varchar(19))");
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table cut1char");
	}

	@Test
	public void cut1char() {
		values = db.queryForList("select * from cut1char");
		assertEquals("char_type", "", values.get(0).get("char_type"));
		assertEquals("varchar_type", "varchar_type",
				values.get(0).get("varchar_type"));
	}

}
