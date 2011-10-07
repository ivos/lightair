package net.sf.lightair.support.junit;

import java.lang.reflect.Method;
import java.sql.SQLException;

import net.sf.lightair.annotation.Setup;
import net.sf.lightair.support.unitils.UnitilsWrapper;

import org.dbunit.DatabaseUnitException;
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
			DatabaseUnitException {
		if (null != setup) {
			unitilsWrapper.setup(testMethod, setup.value());
		}
	}

	private UnitilsWrapper unitilsWrapper;

	public void setUnitilsWrapper(UnitilsWrapper unitilsWrapper) {
		this.unitilsWrapper = unitilsWrapper;
	}

}
