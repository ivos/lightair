package net.sf.lightair.internal.junit;

import net.sf.lightair.annotation.Setup;

import java.lang.reflect.Method;

/**
 * Test rule helper to set up the database before test method execution.
 */
public class SetupTestRule extends AbstractTestRule<Setup> {

	/**
	 * Constructor.
	 *
	 * @param testMethod Method on which the rule is being applied
	 */
	public SetupTestRule(Method testMethod) {
		super(testMethod, Setup.class, Setup.List.class);
	}

	/**
	 * If the method is annotated with @{@link Setup}, set up the database.
	 */
	@Override
	public void before() {
		if (null != getAnnotation()) {
			setupExecutor.execute(getAnnotation(), getTestMethod());
		}
	}

	// beans and their setters:

	private SetupExecutor setupExecutor;

	/**
	 * Set the setup executor.
	 *
	 * @param setupExecutor executor
	 */
	public void setSetupExecutor(SetupExecutor setupExecutor) {
		this.setupExecutor = setupExecutor;
	}
}
