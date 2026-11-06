package it.setup.annotation;

import net.sf.lightair.LightAir;
import net.sf.lightair.annotation.Setup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verify that the database {@link Setup} is performed AFTER all the
 * user-defined {@link BeforeEach} methods are executed.
 */
@ExtendWith(LightAir.class)
@Setup
public class SetupCalledAfterCustomBeforeMethodsTest extends SetupTestBase {

	@BeforeAll
	public static void setup() {
		db.execute("delete from person");
		db.execute("insert into person values ('beforeSetup')");
	}

	@BeforeEach
	public void customBeforeMethod() {
		assertEquals("beforeSetup",
			db.queryForObject("select name from person", String.class));
	}

	@Test
	public void test() {
	}
}
