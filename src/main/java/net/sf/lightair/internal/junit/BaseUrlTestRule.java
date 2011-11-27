package net.sf.lightair.internal.junit;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;
import net.sf.lightair.annotation.BaseUrl;

import org.junit.runners.model.FrameworkMethod;

/**
 * JUnit test rule to set base URL before test method execution.
 */
public class BaseUrlTestRule extends AbstractTestRule<BaseUrl> {

	/**
	 * Constructor.
	 * 
	 * @param frameworkMethod
	 *            JUnit framework method on which the test rule is being applied
	 */
	public BaseUrlTestRule(FrameworkMethod frameworkMethod) {
		super(frameworkMethod, BaseUrl.class);
	}

	/**
	 * If the method is annotated with @{@link BaseUrl}, set the base URL.
	 */
	@Override
	protected void before() throws Throwable {
		if (null != getAnnotation()) {
			setBaseUrl(getAnnotation().value());
		}
	}

}
