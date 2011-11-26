package net.sf.lightair.internal.junit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import net.sf.lightair.internal.unitils.UnitilsWrapper;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * Abstract base class for test rules.
 * 
 * @param <T>
 *            Type of annotation that configures the test rule
 */
public abstract class AbstractTestRule<T extends Annotation> implements
		TestRule {

	private final T annotation;
	private final Method testMethod;

	/**
	 * Constructor.
	 * 
	 * @param frameworkMethod
	 *            JUnit framework method on which the test rule is being applied
	 * @param annotationType
	 *            Type of annotation that configures the test rule
	 */
	public AbstractTestRule(FrameworkMethod frameworkMethod,
			Class<T> annotationType) {
		this.testMethod = frameworkMethod.getMethod();
		T methodAnnotation = testMethod.getAnnotation(annotationType);
		annotation = (null != methodAnnotation) ? methodAnnotation : testMethod
				.getDeclaringClass().getAnnotation(annotationType);
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
	 * @throws Throwable
	 */
	protected void before() throws Throwable {
	}

	/**
	 * Called after wrapped statement.
	 * 
	 * @throws Throwable
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

	// beans and their setters:

	protected UnitilsWrapper unitilsWrapper;

	/**
	 * Set Unitils wrapper.
	 * 
	 * @param unitilsWrapper
	 *            Unitils wrapper
	 */
	public void setUnitilsWrapper(UnitilsWrapper unitilsWrapper) {
		this.unitilsWrapper = unitilsWrapper;
	}

}
