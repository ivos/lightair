package it.await.annotation;

import it.common.CommonTestBase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import test.support.ConfigSupport;

import java.util.Timer;
import java.util.TimerTask;

public class AwaitTestBase extends CommonTestBase {

	@BeforeClass
	@org.testng.annotations.BeforeClass
	public static void beforeClass() {
		db.execute("create table person(id int primary key, name varchar(255))");
		ConfigSupport.replaceConfig("diff1");
	}

	@AfterClass
	@org.testng.annotations.AfterClass
	public static void afterClass() {
		db.execute("drop table person");
		ConfigSupport.restoreConfig();
	}

	private void deletePersons() {
		db.execute("delete from person");
	}

	private void fillPersons(int size) {
		for (int i = 0; i < size; i++) {
			db.update("insert into person (id,name) values (" + i + ",'name"
					+ i + "')");
		}
	}

	void scheduleFillPersons(int size) {
		deletePersons();
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				fillPersons(size);
			}
		}, 150);
	}
}
