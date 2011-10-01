package unit.support.dbunit;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import net.sf.lightair.exception.DataSetNotFoundException;
import net.sf.lightair.support.dbunit.DataSetLoader;
import net.sf.lightair.support.dbunit.DbUnitWrapper;
import net.sf.seaf.test.jmock.JMockSupport;

import org.dbunit.dataset.IDataSet;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

public class DataSetLoaderTest extends JMockSupport {

	DataSetLoader l = new DataSetLoader();
	DbUnitWrapper dbUnit;
	IDataSet dataSet;
	private Method method;

	public void aMethod() {
	}

	@Before
	public void before() throws Exception {
		dbUnit = mock(DbUnitWrapper.class);
		l.setDbUnit(dbUnit);
		dataSet = mock(IDataSet.class);
		method = DataSetLoaderTest.class.getDeclaredMethod("aMethod",
				(Class<?>[]) null);
	}

	@Test
	public void foundByMethod() throws Exception {
		check(new Expectations() {
			{
				one(dbUnit).loadDataSetIfExists(DataSetLoaderTest.class,
						"DataSetLoaderTest.aMethod-verify.xml");
				will(returnValue(dataSet));
			}
		});

		l.loadDataSet(method);
	}

	@Test
	public void foundByClass() throws Exception {
		check(new Expectations() {
			{
				one(dbUnit).loadDataSetIfExists(DataSetLoaderTest.class,
						"DataSetLoaderTest.aMethod-verify.xml");
				will(returnValue(null));

				one(dbUnit).loadDataSetIfExists(DataSetLoaderTest.class,
						"DataSetLoaderTest-verify.xml");
				will(returnValue(dataSet));
			}
		});

		l.loadDataSet(method);
	}

	@Test
	public void notFound() throws Exception {
		check(new Expectations() {
			{
				one(dbUnit).loadDataSetIfExists(DataSetLoaderTest.class,
						"DataSetLoaderTest.aMethod-verify.xml");
				will(returnValue(null));

				one(dbUnit).loadDataSetIfExists(DataSetLoaderTest.class,
						"DataSetLoaderTest-verify.xml");
				will(returnValue(null));
			}
		});

		try {
			l.loadDataSet(method);
			fail("Should throw");
		} catch (DataSetNotFoundException e) {
			assertEquals("Data set not found [DataSetLoaderTest-verify.xml, "
					+ "DataSetLoaderTest.aMethod-verify.xml].", e.getMessage());
		}
	}

}
