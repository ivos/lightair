package net.sf.lightair.internal.junit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Abstract base class for test rules.
 *
 * @param <T> Type of annotation that configures the test rule
 */
public abstract class AbstractTestRule<T extends Annotation> {

	private final T annotation;
	private final Method testMethod;

	/**
	 * Constructor.
	 *
	 * @param testMethod                Java reflection method on which the test rule is being applied
	 * @param annotationType            Type of annotation that configures the test rule
	 * @param alternativeAnnotationType Alternative annotation type
	 * @param <A>                       Generic support param
	 */
	public <A extends Annotation> AbstractTestRule(
		Method testMethod, Class<T> annotationType, Class<A> alternativeAnnotationType
	) {
		this.testMethod = testMethod;
		T methodAnnotation = testMethod.getAnnotation(annotationType);
		final boolean hasAlternativeAnnotation =
			null != alternativeAnnotationType
				&& null != testMethod.getAnnotation(alternativeAnnotationType);
		if (hasAlternativeAnnotation) {
			annotation = null;
		} else if (null != methodAnnotation) {
			annotation = methodAnnotation;
		} else {
			annotation = testMethod.getDeclaringClass().getAnnotation(annotationType);
		}
	}

	/**
	 * Executes before the test method.
	 */
	public void before() {
	}

	/**
	 * Executes after the test method.
	 */
	public void after() {
	}

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
