package net.sf.lightair.internal.junit;

import net.sf.lightair.annotation.Setup;

import java.lang.reflect.Method;

/**
 * Test rule helper to set up the database with multiple <code>@Setup</code>
 * annotations before test method execution.
 */
public class SetupListTestRule extends AbstractTestRule<Setup.List> {

	/**
	 * Constructor.
	 *
	 * @param testMethod Method on which the test rule is being applied
	 */
	public SetupListTestRule(Method testMethod) {
		super(testMethod, Setup.List.class, Setup.class);
	}

	/**
	 * If the method is annotated with @{@link Setup}, set up the database.
	 */
	@Override
	public void before() {
		if (null != getAnnotation()) {
			Setup[] setups = getAnnotation().value();
			for (Setup setup : setups) {
				setupExecutor.execute(setup, getTestMethod());
			}
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
