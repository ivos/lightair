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
import net.sf.seaf.test.jmock.JMockSupport;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.util.MultiSchemaDataSet;

public class UnitilsWrapper_SetupTest extends JMockSupport {

	UnitilsWrapper w;
	Method testMethod;
	String[] fileNames = { "fn1", "fn2", "fn3" };
	DataSetLoader dataSetLoader;
	MultiSchemaDataSet multiSchemaDataSet;
	IDataSet ds1, ds2, ds3;
	DbUnitWrapper dbUnitWrapper;
	IDatabaseConnection c1, c2, c3;
	Factory factory;
	DatabaseOperation dbo;

	@Before
	public void before() throws SecurityException, NoSuchMethodException {
		w = new UnitilsWrapper();
		testMethod = Object.class
				.getDeclaredMethod("toString", new Class<?>[0]);
		dataSetLoader = mock(DataSetLoader.class);
		w.setDataSetLoader(dataSetLoader);
		multiSchemaDataSet = mock(MultiSchemaDataSet.class);
		ds1 = mock(IDataSet.class, "ds1");
		ds2 = mock(IDataSet.class, "ds2");
		ds3 = mock(IDataSet.class, "ds3");
		dbUnitWrapper = mock(DbUnitWrapper.class);
		w.setDbUnitWrapper(dbUnitWrapper);
		c1 = mock(IDatabaseConnection.class, "c1");
		c2 = mock(IDatabaseConnection.class, "c2");
		c3 = mock(IDatabaseConnection.class, "c3");
		factory = mock(Factory.class);
		w.setFactory(factory);
		dbo = mock(DatabaseOperation.class);
	}

	@Test
	public void ok() throws SQLException, DatabaseUnitException {
		checkCommons();
		checkExecuteForSchema("schema1", ds1, c1);
		checkExecuteForSchema("schema2", ds2, c2);
		checkExecuteForSchema("schema3", ds3, c3);

		w.setup(testMethod, "profile1", fileNames);
	}

	private void checkCommons() {
		check(new Expectations() {
			{
				one(dataSetLoader).load("profile1", testMethod, "", fileNames);
				will(returnValue(multiSchemaDataSet));

				one(factory).getCleanInsertDatabaseOperation();
				will(returnValue(dbo));

				one(multiSchemaDataSet).getSchemaNames();
				will(returnValue(new LinkedHashSet<String>(Arrays.asList(
						"schema1", "schema2", "schema3"))));
			}
		});
	}

	private void checkExecuteForSchema(final String schemaName,
			final IDataSet ds, final IDatabaseConnection c)
			throws DatabaseUnitException, SQLException {
		check(new Expectations() {
			{
				one(multiSchemaDataSet).getDataSetForSchema(schemaName);
				will(returnValue(ds));

				one(dbUnitWrapper).getConnection("profile1", schemaName);
				will(returnValue(c));

				one(dbo).execute(c, ds);

				one(c).close();
			}
		});
	}

	@Test
	public void fail_DatabaseUnitException() throws SQLException,
			DatabaseUnitException {
		final DatabaseUnitException cause = new DatabaseUnitException();
		checkCommons();
		check(new Expectations() {
			{
				one(multiSchemaDataSet).getDataSetForSchema("schema1");
				will(returnValue(ds1));

				one(dbUnitWrapper).getConnection("profile1", "schema1");
				will(returnValue(c1));

				one(dbo).execute(c1, ds1);
				will(throwException(cause));

				one(c1).close();
			}
		});

		try {
			w.setup(testMethod, "profile1", fileNames);
			fail("Should throw");
		} catch (DatabaseAccessException e) {
			assertEquals("Message", "Error accessing database.", e.getMessage());
			assertSame("Cause", cause, e.getCause());
		}
	}

	@Test
	public void fail_SQLException_InExecute() throws SQLException,
			DatabaseUnitException {
		final SQLException cause = new SQLException();
		checkCommons();
		check(new Expectations() {
			{
				one(multiSchemaDataSet).getDataSetForSchema("schema1");
				will(returnValue(ds1));

				one(dbUnitWrapper).getConnection("profile1", "schema1");
				will(returnValue(c1));

				one(dbo).execute(c1, ds1);
				will(throwException(cause));

				one(c1).close();
			}
		});

		try {
			w.setup(testMethod, "profile1", fileNames);
			fail("Should throw");
		} catch (DatabaseAccessException e) {
			assertEquals("Message", "Error accessing database.", e.getMessage());
			assertSame("Cause", cause, e.getCause());
		}
	}

	@Test
	public void fail_SQLException_InClose() throws SQLException,
			DatabaseUnitException {
		final SQLException cause = new SQLException();
		checkCommons();
		check(new Expectations() {
			{
				one(multiSchemaDataSet).getDataSetForSchema("schema1");
				will(returnValue(ds1));

				one(dbUnitWrapper).getConnection("profile1", "schema1");
				will(returnValue(c1));

				one(dbo).execute(c1, ds1);

				one(c1).close();
				will(throwException(cause));
			}
		});

		try {
			w.setup(testMethod, "profile1", fileNames);
			fail("Should throw");
		} catch (CloseDatabaseConnectionException e) {
			assertEquals("Message", "Cannot close connection to database.",
					e.getMessage());
			assertSame("Cause", cause, e.getCause());
		}
	}
}
