package unit.support.dbunit;

import static org.junit.Assert.*;
import net.sf.lightair.exception.IllegalDataSetContentException;
import net.sf.lightair.support.dbunit.DbUnitWrapper;

import org.junit.Test;

public class DbUnitWrapperTest {

	DbUnitWrapper w = new DbUnitWrapper();

	@Test
	public void createConnection() throws Exception {
		assertNotNull(w.createConnection());
	}

	@Test
	public void loadDataSetIfExists_NonExistent() throws Exception {
		assertNull("Non-existent data set returns null",
				w.loadDataSetIfExists(DbUnitWrapperTest.class, "non-existent"));
	}

	@Test
	public void loadDataSetIfExists_Correct() throws Exception {
		assertNotNull("Loads correct data set", w.loadDataSetIfExists(
				DbUnitWrapperTest.class, "data-set-correct.xml"));
	}

	@Test
	public void loadDataSetIfExists_IllegalContent() throws Exception {
		try {
			w.loadDataSetIfExists(DbUnitWrapperTest.class,
					"data-set-incorrect.xml");
			fail("Shloud throw");
		} catch (IllegalDataSetContentException e) {
			assertEquals(
					"Cannot load content of data set 'data-set-incorrect.xml'.",
					e.getMessage());
		}
	}

}
