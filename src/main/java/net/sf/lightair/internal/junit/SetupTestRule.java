package net.sf.lightair.internal.junit;

import java.sql.SQLException;

import net.sf.lightair.annotation.Setup;

import org.dbunit.DatabaseUnitException;
import org.junit.runners.model.FrameworkMethod;

/**
 * JUnit test rule to setup database before test method execution.
 */
public class SetupTestRule extends AbstractTestRule<Setup> {

	/**
	 * Constructor.
	 * 
	 * @param frameworkMethod
	 *            JUnit framework method on which the test rule is being applied
	 */
	public SetupTestRule(FrameworkMethod frameworkMethod) {
		super(frameworkMethod, Setup.class);
	}

	/**
	 * If the method is annotated with @{@link Setup}, set up the database.
	 * 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws DatabaseUnitException
	 */
	@Override
	protected void before() throws ClassNotFoundException, SQLException,
			DatabaseUnitException {
		if (null != getAnnotation()) {
			unitilsWrapper.setup(getTestMethod(), getAnnotation().value());
		}
	}

}
