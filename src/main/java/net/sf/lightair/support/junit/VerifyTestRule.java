package net.sf.lightair.support.junit;

import java.lang.reflect.Method;
import java.sql.SQLException;

import net.sf.lightair.annotation.Verify;
import net.sf.lightair.support.dbunit.DataSetLoader;
import net.sf.lightair.support.dbunit.DbUnitWrapper;

import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class VerifyTestRule implements TestRule {

	private Verify verify;
	private final Method testMethod;

	public VerifyTestRule(FrameworkMethod frameworkMethod) {
		this.testMethod = frameworkMethod.getMethod();
		verify = testMethod.getAnnotation(Verify.class);
		if (null == verify) {
			verify = testMethod.getDeclaringClass().getAnnotation(Verify.class);
		}
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
			IDataSet dataSet = dataSetLoader.loadDataSet(testMethod,
					VERIFY_FILE_NAME_SUFFIX);
			IDatabaseConnection connection = dbUnitWrapper.createConnection();
			try {
				Assertion.assertEquals(dataSet, connection.createDataSet());
			} finally {
				connection.close();
			}
		}
	}

	private static final String VERIFY_FILE_NAME_SUFFIX = "-verify";

	private DbUnitWrapper dbUnitWrapper;

	public void setDbUnitWrapper(DbUnitWrapper dbUnitWrapper) {
		this.dbUnitWrapper = dbUnitWrapper;
	}

	private DataSetLoader dataSetLoader;

	public void setDataSetLoader(DataSetLoader dataSetLoader) {
		this.dataSetLoader = dataSetLoader;
	}

}
