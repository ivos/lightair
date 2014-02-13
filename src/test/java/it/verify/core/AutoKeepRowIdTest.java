package it.verify.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(LightAir.class)
@Verify
public class AutoKeepRowIdTest extends CommonTestBase {

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table a (id int primary key, "
				+ "startsAuto varchar(20), startsManual varchar(20))");
		db.execute("create table b (id int primary key, "
				+ "startsAuto varchar(20), startsManual varchar(20))");
	}

	@AfterClass
	public static void afterClass() {
		db.execute("drop table a");
		db.execute("drop table b");
	}

	@Test
	public void test() {
		fillTable("a", "421");
		fillTable("b", "241");
	}

	private void fillTable(String table, String prefix) {
		db.execute("delete from " + table);
		db.update("insert into " + table
				+ " (id,startsAuto,startsManual) values (1,'startsauto "
				+ prefix + "7500','sm1')");
		db.update("insert into "
				+ table
				+ " (id,startsAuto,startsManual) values (2,'sa2','startsmanual "
				+ prefix + "9501')");
		db.update("insert into " + table
				+ " (id,startsAuto,startsManual) values (3,'sa3','sm3')");
		db.update("insert into " + table
				+ " (id,startsAuto,startsManual) values (4,'startsauto "
				+ prefix + "7503','startsmanual " + prefix + "9503')");
	}

}
