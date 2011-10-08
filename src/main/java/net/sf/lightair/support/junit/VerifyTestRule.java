package net.sf.lightair.support.junit;

import java.sql.SQLException;

import net.sf.lightair.annotation.Verify;

import org.dbunit.DatabaseUnitException;
import org.junit.runners.model.FrameworkMethod;

public class VerifyTestRule extends AbstractTestRule<Verify> {

	public VerifyTestRule(FrameworkMethod frameworkMethod) {
		super(frameworkMethod, Verify.class);
	}

	@Override
	protected void after() throws ClassNotFoundException, SQLException,
			DatabaseUnitException {
		if (null != getAnnotation()) {
			unitilsWrapper.verify(getTestMethod(), getAnnotation().value());
		}
	}

}
