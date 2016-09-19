package net.sf.lightair.internal.junit;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Abstract base class for test rules.
 *
 * @param <T> Type of annotation that configures the test rule
 */
public abstract class AbstractTestRule<T extends Annotation> implements TestRule {

	private final T annotation;
	private final Method testMethod;

	/**
	 * Constructor.
	 *
	 * @param frameworkMethod           JUnit framework method on which the test rule is being applied
	 * @param annotationType            Type of annotation that configures the test rule
	 * @param alternativeAnnotationType Alternative annotation type
	 * @param <A>                       Generic support param
	 */
	public <A extends Annotation> AbstractTestRule(
			FrameworkMethod frameworkMethod, Class<T> annotationType, Class<A> alternativeAnnotationType) {
		testMethod = frameworkMethod.getMethod();
		T methodAnnotation = testMethod.getAnnotation(annotationType);
		final boolean hasAlternativeAnnotation =
				(null != alternativeAnnotationType) && (null != testMethod.getAnnotation(alternativeAnnotationType));
		if (hasAlternativeAnnotation) {
			annotation = null;
		} else if (null != methodAnnotation) {
			annotation = methodAnnotation;
		} else {
			annotation = testMethod.getDeclaringClass().getAnnotation(annotationType);
		}
	}

	// TestRule contract:

	/**
	 * JUnit test rule contract. Applies the rule to a statement, returning
	 * wrapped statement.
	 * <p>
	 * Calls {@link AbstractTestRule#before()}, then the wrapped statement, then
	 * {@link AbstractTestRule#after()}.
	 */
	public Statement apply(final Statement base, Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				before();
				base.evaluate();
				after();
			}
		};
	}

	// hook methods for descendants:

	/**
	 * Called before wrapped statement.
	 *
	 * @throws Throwable Anything
	 */
	protected void before() throws Throwable {
	}

	/**
	 * Called after wrapped statement.
	 *
	 * @throws Throwable Anything
	 */
	protected void after() throws Throwable {
	}

	// getters:

	/**
	 * Return the configuring annotation.
	 *
	 * @return Configuring annotation
	 */
	public T getAnnotation() {
		return annotation;
	}

	/**
	 * Return the test method on which the test rule is applied.
	 *
	 * @return Test method
	 */
	public Method getTestMethod() {
		return testMethod;
	}
}
