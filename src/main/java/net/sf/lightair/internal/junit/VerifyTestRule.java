package net.sf.lightair.internal.junit;

import net.sf.lightair.annotation.Verify;

import java.lang.reflect.Method;

/**
 * Test rule helper to verify the database after test method execution.
 */
public class VerifyTestRule extends AbstractTestRule<Verify> {

	/**
	 * Constructor.
	 *
	 * @param testMethod Method on which the test rule is being applied
	 */
	public VerifyTestRule(Method testMethod) {
		super(testMethod, Verify.class, Verify.List.class);
	}

	/**
	 * If the method is annotated with @{@link Verify}, verify the database.
	 */
	@Override
	public void after() {
		if (null != getAnnotation()) {
			verifyExecutor.execute(getAnnotation(), getTestMethod());
		}
	}

	// beans and their setters:

	private VerifyExecutor verifyExecutor;

	/**
	 * Set the verify executor.
	 *
	 * @param verifyExecutor executor
	 */
	public void setVerifyExecutor(VerifyExecutor verifyExecutor) {
		this.verifyExecutor = verifyExecutor;
	}
}
