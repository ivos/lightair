package net.sf.lightair.support.junit;

import java.sql.SQLException;

import net.sf.lightair.annotation.Verify;
import net.sf.lightair.exception.DataSetNotFoundException;
import net.sf.lightair.support.dbunit.DbUnitWrapper;

import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

public class VerifyTestRule implements TestRule {

	private final TestClass testClass;
	private Verify verify;
	private final String classVerifyFileName, methodVerifyFileName;

	public VerifyTestRule(TestClass testClass, FrameworkMethod method) {
		this.testClass = testClass;
		verify = method.getAnnotation(Verify.class);
		if (null == verify) {
			verify = testClass.getJavaClass().getAnnotation(Verify.class);
		}
		classVerifyFileName = testClass.getJavaClass().getSimpleName()
				+ "-verify.xml";
		methodVerifyFileName = testClass.getJavaClass().getSimpleName() + '.'
				+ method.getName() + "-verify.xml";
	}

	@Override
	public Statement apply(final Statement base, Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				base.evaluate();
				verify();
			}
		};
	}

	protected void verify() throws ClassNotFoundException, SQLException,
			DatabaseUnitException, Exception {
		if (null != verify) {
			IDatabaseConnection connection = getDbUnit().createConnection();
			IDataSet dataSet = getDbUnit().loadDataSetIfExists(
					testClass.getJavaClass(), methodVerifyFileName);
			if (null == dataSet) {
				dataSet = getDbUnit().loadDataSetIfExists(
						testClass.getJavaClass(), classVerifyFileName);
			}
			if (null == dataSet) {
				throw new DataSetNotFoundException(classVerifyFileName,
						methodVerifyFileName);
			}
			if (null != dataSet) {
				try {
					Assertion.assertEquals(dataSet, connection.createDataSet());
				} finally {
					connection.close();
				}
			}
		}
	}

	private DbUnitWrapper dbUnit;

	public DbUnitWrapper getDbUnit() {
		if (null == dbUnit) {
			dbUnit = new DbUnitWrapper();
		}
		return dbUnit;
	}

}
