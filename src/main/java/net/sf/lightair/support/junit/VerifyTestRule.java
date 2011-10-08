package net.sf.lightair.support.junit;

import java.lang.reflect.Method;
import java.sql.SQLException;

import net.sf.lightair.annotation.Verify;
import net.sf.lightair.support.unitils.UnitilsWrapper;

import org.dbunit.DatabaseUnitException;
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
			unitilsWrapper.verify(testMethod, verify.value());
		}
	}

	private UnitilsWrapper unitilsWrapper;

	public void setUnitilsWrapper(UnitilsWrapper unitilsWrapper) {
		this.unitilsWrapper = unitilsWrapper;
	}

}
