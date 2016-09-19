package net.sf.lightair.internal.junit;

import net.sf.lightair.annotation.Setup;

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
		super(frameworkMethod, Setup.class, Setup.List.class);
	}

	/**
	 * If the method is annotated with @{@link Setup}, set up the database.
	 */
	@Override
	protected void before() {
		if (null != getAnnotation()) {
			setupExecutor.execute(getAnnotation(), getTestMethod());
		}
	}

	// beans and their setters:

	private SetupExecutor setupExecutor;

	/**
	 * Set setup executor.
	 * 
	 * @param setupExecutor executor
	 */
	public void setSetupExecutor(SetupExecutor setupExecutor) {
		this.setupExecutor = setupExecutor;
	}
}
