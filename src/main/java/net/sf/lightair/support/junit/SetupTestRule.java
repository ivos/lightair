package net.sf.lightair.support.junit;

import java.lang.reflect.Method;
import java.sql.SQLException;

import net.sf.lightair.annotation.Setup;
import net.sf.lightair.support.dbunit.DataSetLoader;
import net.sf.lightair.support.dbunit.DbUnitWrapper;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class SetupTestRule implements TestRule {

	private Setup setup;
	private final Method testMethod;

	public SetupTestRule(FrameworkMethod frameworkMethod) {
		this.testMethod = frameworkMethod.getMethod();
		setup = testMethod.getAnnotation(Setup.class);
		if (null == setup) {
			setup = testMethod.getDeclaringClass().getAnnotation(Setup.class);
		}
	}

	@Override
	public Statement apply(final Statement base, Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				setup();
				base.evaluate();
			}
		};
	}

	protected void setup() throws ClassNotFoundException, SQLException,
			DatabaseUnitException, Exception {
		if (null != setup) {
			IDatabaseConnection connection = dbUnitWrapper.createConnection();
			IDataSet dataSet = dataSetLoader.loadDataSet(testMethod, "");
			try {
				DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
			} finally {
				connection.close();
			}
		}
	}

	private DbUnitWrapper dbUnitWrapper;

	public void setDbUnitWrapper(DbUnitWrapper dbUnitWrapper) {
		this.dbUnitWrapper = dbUnitWrapper;
	}

	private DataSetLoader dataSetLoader;

	public void setDataSetLoader(DataSetLoader dataSetLoader) {
		this.dataSetLoader = dataSetLoader;
	}

}
