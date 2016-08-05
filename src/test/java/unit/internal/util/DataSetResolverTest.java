package unit.internal.util;

import net.sf.lightair.internal.util.DataSetResolver;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class DataSetResolverTest {

	DataSetResolver r = new DataSetResolver();
	Method testMethod;
	String fileName = "file.txt";

	public void aMethod() {
	}

	@Before
	public void before() throws Exception {
		testMethod = DataSetResolverTest.class.getDeclaredMethod("aMethod", (Class<?>[]) null);
	}

	@Test
	public void resolveIfExists_Exists() {
		assertEquals(fileName, new File(r.resolveIfExists(testMethod, fileName).getPath()).getName());
	}

	@Test
	public void resolveIfExists_NotExists() {
		assertNull(r.resolveIfExists(testMethod, "nonexistent"));
	}

	@Test
	public void resolve_Exists() {
		assertEquals(fileName, new File(r.resolve(testMethod, fileName).getPath()).getName());
	}

	@Test
	public void resolve_NotExists() {
		try {
			r.resolve(testMethod, "nonexistent");
			fail("Should throw");
		} catch (RuntimeException e) {
			assertEquals("Data set not found [nonexistent].", e.getMessage());
		}
	}
}
