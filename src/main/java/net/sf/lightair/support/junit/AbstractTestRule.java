package net.sf.lightair.support.junit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import net.sf.lightair.support.unitils.UnitilsWrapper;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public abstract class AbstractTestRule<T extends Annotation> implements
		TestRule {

	private final T annotation;
	private final Method testMethod;

	public AbstractTestRule(FrameworkMethod frameworkMethod,
			Class<T> annotationType) {
		this.testMethod = frameworkMethod.getMethod();
		T methodAnnotation = testMethod.getAnnotation(annotationType);
		annotation = (null != methodAnnotation) ? methodAnnotation : testMethod
				.getDeclaringClass().getAnnotation(annotationType);
	}

	@Override
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

	protected void before() throws Throwable {
	}

	protected void after() throws Throwable {
	}

	public T getAnnotation() {
		return annotation;
	}

	public Method getTestMethod() {
		return testMethod;
	}

	protected UnitilsWrapper unitilsWrapper;

	public void setUnitilsWrapper(UnitilsWrapper unitilsWrapper) {
		this.unitilsWrapper = unitilsWrapper;
	}

}
