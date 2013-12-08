package unit.internal.unitils;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashSet;

import net.sf.lightair.exception.CloseDatabaseConnectionException;
import net.sf.lightair.exception.DatabaseAccessException;
import net.sf.lightair.internal.dbunit.DbUnitWrapper;
import net.sf.lightair.internal.factory.Factory;
import net.sf.lightair.internal.unitils.DataSetLoader;
import net.sf.lightair.internal.unitils.UnitilsWrapper;
import net.sf.lightair.internal.unitils.compare.DataSetAssert;
import net.sf.seaf.test.jmock.JMockSupport;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.util.MultiSchemaDataSet;

public class UnitilsWrapper_VerifyTest extends JMockSupport {

	UnitilsWrapper w;
	Method testMethod;
	String[] fileNames = { "fn1", "fn2", "fn3" };
	DataSetLoader dataSetLoader;
	MultiSchemaDataSet multiSchemaDataSet;
	IDataSet dsE1, dsE2, dsE3, dsA1, dsA2, dsA3;
	DbUnitWrapper dbUnitWrapper;
	IDatabaseConnection c1, c2, c3;
	Factory factory;
	DataSetAssert dataSetAssert;

	@Before
	public void before() throws SecurityException, NoSuchMethodException {
		w = new UnitilsWrapper();
		testMethod = Object.class
				.getDeclaredMethod("toString", new Class<?>[0]);
		dataSetLoader = mock(DataSetLoader.class);
		w.setDataSetLoader(dataSetLoader);
		multiSchemaDataSet = mock(MultiSchemaDataSet.class);
		dsE1 = mock(IDataSet.class, "dsE1");
		dsE2 = mock(IDataSet.class, "dsE2");
		dsE3 = mock(IDataSet.class, "dsE3");
		dsA1 = mock(IDataSet.class, "dsA1");
		dsA2 = mock(IDataSet.class, "dsA2");
		dsA3 = mock(IDataSet.class, "dsA3");
		dbUnitWrapper = mock(DbUnitWrapper.class);
		w.setDbUnitWrapper(dbUnitWrapper);
		c1 = mock(IDatabaseConnection.class, "c1");
		c2 = mock(IDatabaseConnection.class, "c2");
		c3 = mock(IDatabaseConnection.class, "c3");
		factory = mock(Factory.class);
		w.setFactory(factory);
		dataSetAssert = mock(DataSetAssert.class);
		w.setDataSetAssert(dataSetAssert);
	}

	@Test
	public void ok() throws SQLException, ClassNotFoundException {
		checkCommons();
		checkExecuteForSchema("schema1", dsE1, dsA1, c1);
		checkExecuteForSchema("schema2", dsE2, dsA2, c2);
		checkExecuteForSchema("schema3", dsE3, dsA3, c3);

		w.verify(testMethod, "profile1", fileNames);
	}

	private void checkCommons() {
		check(new Expectations() {
			{
				one(dataSetLoader).load("profile1", testMethod, "-verify",
						fileNames);
				will(returnValue(multiSchemaDataSet));

				one(multiSchemaDataSet).getSchemaNames();
				will(returnValue(new LinkedHashSet<String>(Arrays.asList(
						"schema1", "schema2", "schema3"))));
			}
		});
	}

	private void checkExecuteForSchema(final String schemaName,
			final IDataSet dsE, final IDataSet dsA, final IDatabaseConnection c)
			throws SQLException {
		check(new Expectations() {
			{
				one(multiSchemaDataSet).getDataSetForSchema(schemaName);
				will(returnValue(dsE));

				one(dbUnitWrapper).getConnection("profile1", schemaName);
				will(returnValue(c));

				one(c).createDataSet();
				will(returnValue(dsA));

				one(dataSetAssert).assertEqualDbUnitDataSets(schemaName, dsE,
						dsA);

				one(c).close();
			}
		});
	}

	@Test
	public void fail_SQLException_InCreateDataSet() throws SQLException {
		final SQLException cause = new SQLException();
		checkCommons();
		check(new Expectations() {
			{
				one(multiSchemaDataSet).getDataSetForSchema("schema1");
				will(returnValue(dsE1));

				one(dbUnitWrapper).getConnection("profile1", "schema1");
				will(returnValue(c1));

				one(c1).createDataSet();
				will(throwException(cause));

				one(c1).close();
			}
		});

		try {
			w.verify(testMethod, "profile1", fileNames);
			fail("Should throw");
		} catch (DatabaseAccessException e) {
			assertEquals("Message", "Error accessing database.", e.getMessage());
			assertSame("Cause", cause, e.getCause());
		}
	}

	@Test
	public void fail_SQLException_InClose() throws SQLException {
		final SQLException cause = new SQLException();
		checkCommons();
		check(new Expectations() {
			{
				one(multiSchemaDataSet).getDataSetForSchema("schema1");
				will(returnValue(dsE1));

				one(dbUnitWrapper).getConnection("profile1", "schema1");
				will(returnValue(c1));

				one(c1).createDataSet();
				will(returnValue(dsA1));

				one(dataSetAssert).assertEqualDbUnitDataSets("schema1", dsE1,
						dsA1);

				one(c1).close();
				will(throwException(cause));
			}
		});

		try {
			w.verify(testMethod, "profile1", fileNames);
			fail("Should throw");
		} catch (CloseDatabaseConnectionException e) {
			assertEquals("Message", "Cannot close connection to database.",
					e.getMessage());
			assertSame("Cause", cause, e.getCause());
		}
	}
}
