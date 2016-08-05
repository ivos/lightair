package net.sf.lightair;

import net.sf.lightair.internal.util.Factory;
import org.junit.rules.RunRules;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Light air Spring JUnit runner.
 * <p>
 * To enable Light air on a Spring JUnit test, annotate it as follows:
 * <p>
 * <pre>
 * &#064;RunWith(LightAirSpringRunner.class)
 * public class MyTest {
 * }
 * </pre>
 * <p>
 * You can use standard spring @
 * {@link org.springframework.test.context.ContextConfiguration} to setup test
 * environment. Then use annotations @{@link net.sf.lightair.annotation.Setup}, @
 * {@link net.sf.lightair.annotation.Verify} to define actions Light air should
 * take on the test.
 * <p>
 * Requires dependency org.springframework:spring-test version 2.5 or higher in
 * order to work.
 */
public class LightAirSpringRunner extends SpringJUnit4ClassRunner {

	static {
		Api.initialize(Api.getPropertiesFileName());
		Runtime.getRuntime().addShutdownHook(new Thread(Api::shutdown));
	}

	public LightAirSpringRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
	}

	/**
	 * Overriding methodInvoker in order to place LightAir's test rules as the
	 * leading ones.
	 */
	@Override
	protected Statement methodInvoker(FrameworkMethod method, Object test) {
		Statement statement = super.methodInvoker(method, test);
		return new RunRules(statement, Factory.getInstance().getAllTestRules(method), describeChild(method));
	}
}
