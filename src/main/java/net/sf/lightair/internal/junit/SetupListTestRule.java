package net.sf.lightair.internal.junit;

import net.sf.lightair.annotation.Setup;

import org.junit.runners.model.FrameworkMethod;

/**
 * JUnit test rule to setup database before test method execution.
 */
public class SetupListTestRule extends AbstractTestRule<Setup.List> {

	/**
	 * Constructor.
	 * 
	 * @param frameworkMethod
	 *            JUnit framework method on which the test rule is being applied
	 */
	public SetupListTestRule(FrameworkMethod frameworkMethod) {
		super(frameworkMethod, Setup.List.class);
	}

	/**
	 * If the method is annotated with @{@link Setup}, set up the database.
	 */
	@Override
	protected void before() {
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
	 * Set setup executor.
	 * 
	 * @param setupExecutor
	 */
	public void setSetupExecutor(SetupExecutor setupExecutor) {
		this.setupExecutor = setupExecutor;
	}

}
