package net.sf.lightair.internal.junit;

import net.sf.lightair.annotation.Verify;
import org.junit.runners.model.FrameworkMethod;

/**
 * JUnit test rule to verify database after test method execution.
 */
public class VerifyTestRule extends AbstractTestRule<Verify> {

	/**
	 * Constructor.
	 *
	 * @param frameworkMethod JUnit framework method on which the test rule is being applied
	 */
	public VerifyTestRule(FrameworkMethod frameworkMethod) {
		super(frameworkMethod, Verify.class, Verify.List.class);
	}

	/**
	 * If the method is annotated with @{@link Verify}, verify the database.
	 */
	@Override
	protected void after() {
		if (null != getAnnotation()) {
			verifyExecutor.execute(getAnnotation(), getTestMethod());
		}
	}

	// beans and their setters:

	private VerifyExecutor verifyExecutor;

	/**
	 * Set verify executor.
	 *
	 * @param verifyExecutor executor
	 */
	public void setVerifyExecutor(VerifyExecutor verifyExecutor) {
		this.verifyExecutor = verifyExecutor;
	}
}
