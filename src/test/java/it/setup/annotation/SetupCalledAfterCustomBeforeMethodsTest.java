package it.setup.annotation;

import static org.junit.Assert.*;
import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Verify that the database {@link Setup} is performed AFTER all the
 * user-defined {@link Before} methods are executed.
 */
@RunWith(LightAir.class)
@Setup
public class SetupCalledAfterCustomBeforeMethodsTest extends SetupTestBase {

	@BeforeClass
	public static void setup() {
		db.execute("delete from person");
		db.execute("insert into person values ('beforeSetup')");
	}

	@Before
	public void customBeforeMethod() {
		assertEquals("beforeSetup",
				db.queryForObject("select name from person", String.class));
	}

	@Test
	public void test() {
	}

}
