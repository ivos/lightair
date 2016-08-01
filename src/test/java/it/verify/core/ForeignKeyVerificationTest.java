package it.verify.core;

import it.common.CommonTestBase;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Verify;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import test.support.ApiTestSupport;

/**
 * Table expected empty is empty in database.
 */
@RunWith(LightAir.class)
@Verify
public class ForeignKeyVerificationTest extends CommonTestBase {

	@BeforeClass
	public static void beforeClass() {
		db.execute("create table master (id int primary key, m1 varchar(50))");
		db.execute("create table detail (id int primary key, master_id int, d1 varchar(50))");
		db.execute("alter table detail add constraint fk_detail_master "
				+ "foreign key (master_id) references master (id)");
		ApiTestSupport.reInitialize();
	}

	@AfterClass
	public static void afterClass() {
		db.execute("alter table detail drop constraint fk_detail_master");
		db.execute("drop table detail");
		db.execute("drop table master");
	}

	@Test
	public void test() {
		db.execute("delete from detail");
		db.execute("delete from master");

		db.update("insert into master (id,m1) values (0,'m10')");
		db.update("insert into detail (id,master_id,d1) values (0,0,'d100')");
		db.update("insert into detail (id,master_id,d1) values (1,0,'d101')");
		db.update("insert into detail (id,master_id,d1) values (2,0,'d102')");

		db.update("insert into master (id,m1) values (1,'m11')");
		db.update("insert into detail (id,master_id,d1) values (3,1,'d113')");
		db.update("insert into detail (id,master_id,d1) values (4,1,'d114')");
		db.update("insert into detail (id,master_id,d1) values (5,1,'d115')");

		db.update("insert into master (id,m1) values (2,'m12')");
		db.update("insert into detail (id,master_id,d1) values (6,2,'d126')");
		db.update("insert into detail (id,master_id,d1) values (7,2,'d127')");
		db.update("insert into detail (id,master_id,d1) values (8,2,'d128')");
	}

}
