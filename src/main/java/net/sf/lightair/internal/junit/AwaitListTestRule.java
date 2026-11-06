package net.sf.lightair.internal.junit;

import net.sf.lightair.annotation.Await;

import java.lang.reflect.Method;

/**
 * Test rule helper to verify the database with multiple <code>@Await</code>
 * annotations after test method execution with asynchronous functionality.
 */
public class AwaitListTestRule extends AbstractTestRule<Await.List> {

	/**
	 * Constructor.
	 *
	 * @param testMethod Method on which the test rule is being applied
	 */
	public AwaitListTestRule(Method testMethod) {
		super(testMethod, Await.List.class, Await.class);
	}

	/**
	 * If the method is annotated with @{@link Await}, verify the database asynchronously.
	 */
	@Override
	public void after() {
		if (null != getAnnotation()) {
			Await[] verifies = getAnnotation().value();
			for (Await await : verifies) {
				awaitExecutor.execute(await, getTestMethod());
			}
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
