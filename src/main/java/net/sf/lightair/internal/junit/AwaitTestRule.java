package net.sf.lightair.internal.junit;

import net.sf.lightair.annotation.Await;

import java.lang.reflect.Method;

/**
 * Test rule helper to verify the database after test method execution with asynchronous functionality.
 */
public class AwaitTestRule extends AbstractTestRule<Await> {

	/**
	 * Constructor.
	 *
	 * @param testMethod Method on which the test rule is being applied
	 */
	public AwaitTestRule(Method testMethod) {
		super(testMethod, Await.class, Await.List.class);
	}

	/**
	 * If the method is annotated with @{@link Await}, verify the database asynchronously.
	 */
	@Override
	public void after() {
		if (null != getAnnotation()) {
			awaitExecutor.execute(getAnnotation(), getTestMethod());
		}
	}

	// beans and their setters:

	private AwaitExecutor awaitExecutor;

	/**
	 * Set await executor.
	 *
	 * @param awaitExecutor executor
	 */
	public void setAwaitExecutor(AwaitExecutor awaitExecutor) {
		this.awaitExecutor = awaitExecutor;
	}
}
