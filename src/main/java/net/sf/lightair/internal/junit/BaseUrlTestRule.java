package net.sf.lightair.internal.junit;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;
import net.sf.lightair.annotation.BaseUrl;

import org.junit.runners.model.FrameworkMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit test rule to set base URL before test method execution.
 */
public class BaseUrlTestRule extends AbstractTestRule<BaseUrl> {

	private final Logger log = LoggerFactory.getLogger(BaseUrlTestRule.class);

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
	protected void before() {
		if (null != getAnnotation()) {
			String baseUrl = getAnnotation().value();
			log.info("Applying base URL [{}].", baseUrl);
			setBaseUrl(baseUrl);
		}
	}

}
