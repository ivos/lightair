package net.sf.lightair.support.junit;

import java.sql.SQLException;

import net.sf.lightair.annotation.Setup;

import org.dbunit.DatabaseUnitException;
import org.junit.runners.model.FrameworkMethod;

public class SetupTestRule extends AbstractTestRule<Setup> {

	public SetupTestRule(FrameworkMethod frameworkMethod) {
		super(frameworkMethod, Setup.class);
	}

	@Override
	protected void before() throws ClassNotFoundException, SQLException,
			DatabaseUnitException {
		if (null != getAnnotation()) {
			unitilsWrapper.setup(getTestMethod(), getAnnotation().value());
		}
	}

}
