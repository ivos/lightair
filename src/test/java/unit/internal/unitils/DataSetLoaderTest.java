package unit.internal.unitils;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;

import net.sf.lightair.exception.IllegalDataSetContentException;
import net.sf.lightair.internal.unitils.DataSetFactory;
import net.sf.lightair.internal.unitils.DataSetLoader;
import net.sf.lightair.internal.util.DataSetResolver;
import net.sf.seaf.test.jmock.JMockSupport;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.util.MultiSchemaDataSet;

public class DataSetLoaderTest extends JMockSupport {

	DataSetLoader l;
	Method testMethod;
	DataSetResolver dataSetResolver;
	URL resource1, resource2, resource3;
	DataSetFactory dataSetFactory;
	IllegalDataSetContentException cause;
	MultiSchemaDataSet msds;

	public void aMethod() {
	}

	@Before
	public void before() throws Exception {
		l = new DataSetLoader();
		dataSetResolver = mock(DataSetResolver.class);
		l.setDataSetResolver(dataSetResolver);
		dataSetFactory = mock(DataSetFactory.class);
		l.setDataSetFactory(dataSetFactory);
		testMethod = DataSetLoaderTest.class.getDeclaredMethod("aMethod",
				(Class<?>[]) null);

		resource1 = new File("fileName1").toURI().toURL();
		resource2 = new File("fileName2").toURI().toURL();
		resource3 = new File("fileName3").toURI().toURL();
		cause = new IllegalDataSetContentException(null, "Message.");
		msds = mock(MultiSchemaDataSet.class);
	}

	@Test
	public void load_FileNamesPassed() {
		check(new Expectations() {
			{
				one(dataSetResolver).resolve(testMethod, "fileName1");
				will(returnValue(resource1));

				one(dataSetResolver).resolve(testMethod, "fileName2");
				will(returnValue(resource2));

				one(dataSetResolver).resolve(testMethod, "fileName3");
				will(returnValue(resource3));

				one(dataSetFactory).createDataSet("profile1", resource1,
						resource2, resource3);
				will(returnValue(msds));
			}
		});

		MultiSchemaDataSet actual = l.load("profile1", testMethod, "suffix",
				"fileName1", "fileName2", "fileName3");

		assertSame(msds, actual);
	}

	@Test
	public void load_MethodDataSet() {
		check(new Expectations() {
			{
				one(dataSetResolver).resolveIfExists(testMethod,
						"DataSetLoaderTest.aMethodsuffix.xml");
				will(returnValue(resource1));

				one(dataSetFactory).createDataSet("profile1", resource1);
				will(returnValue(msds));
			}
		});

		MultiSchemaDataSet actual = l.load("profile1", testMethod, "suffix");

		assertSame(msds, actual);
	}

	@Test
	public void load_ClassDataSet() {
		check(new Expectations() {
			{
				one(dataSetResolver).resolveIfExists(testMethod,
						"DataSetLoaderTest.aMethodsuffix.xml");
				will(returnValue(null));

				one(dataSetResolver).resolveIfExists(testMethod,
						"aMethodsuffix.xml");
				will(returnValue(null));

				one(dataSetResolver).resolve(testMethod,
						"DataSetLoaderTestsuffix.xml");
				will(returnValue(resource1));

				one(dataSetFactory).createDataSet("profile1", resource1);
				will(returnValue(msds));
			}
		});

		MultiSchemaDataSet actual = l.load("profile1", testMethod, "suffix");

		assertSame(msds, actual);
	}

	@Test
	public void load_FileNamesPassed_IllegalDataSetContent() {
		check(new Expectations() {
			{
				one(dataSetResolver).resolve(testMethod, "fileName1");
				will(returnValue(resource1));

				one(dataSetResolver).resolve(testMethod, "fileName2");
				will(returnValue(resource2));

				one(dataSetResolver).resolve(testMethod, "fileName3");
				will(returnValue(resource3));

				one(dataSetFactory).createDataSet("profile1", resource1,
						resource2, resource3);
				will(throwException(cause));
			}
		});

		performIllegalContent("fileName1, fileName2, fileName3", "fileName1",
				"fileName2", "fileName3");
	}

	@Test
	public void load_MethodDataSet_IllegalDataSetContent() {
		check(new Expectations() {
			{
				one(dataSetResolver).resolveIfExists(testMethod,
						"DataSetLoaderTest.aMethodsuffix.xml");
				will(returnValue(resource1));

				one(dataSetFactory).createDataSet("profile1", resource1);
				will(throwException(cause));
			}
		});

		performIllegalContent("DataSetLoaderTest.aMethodsuffix.xml");
	}

	@Test
	public void load_ClassDataSet_IllegalDataSetContent() {
		check(new Expectations() {
			{
				one(dataSetResolver).resolveIfExists(testMethod,
						"DataSetLoaderTest.aMethodsuffix.xml");
				will(returnValue(null));

				one(dataSetResolver).resolveIfExists(testMethod,
						"aMethodsuffix.xml");
				will(returnValue(null));

				one(dataSetResolver).resolve(testMethod,
						"DataSetLoaderTestsuffix.xml");
				will(returnValue(resource1));

				one(dataSetFactory).createDataSet("profile1", resource1);
				will(throwException(cause));
			}
		});

		performIllegalContent("DataSetLoaderTestsuffix.xml");
	}

	private void performIllegalContent(String messageFiles, String... fileNames) {
		try {
			l.load("profile1", testMethod, "suffix", fileNames);
			fail("Should throw");
		} catch (IllegalDataSetContentException e) {
			assertEquals("Message", "Cannot load content of data set ["
					+ messageFiles + "].", e.getMessage());
			assertSame("Cause", cause, e.getCause());
		}
	}

}
