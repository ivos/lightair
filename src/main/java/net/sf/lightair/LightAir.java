package net.sf.lightair;

import net.sf.lightair.annotation.Setup;
import net.sf.lightair.annotation.Verify;
import net.sf.lightair.internal.Keywords;
import net.sf.lightair.internal.junit.util.Factory;
import org.junit.rules.RunRules;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * Light air JUnit runner.
 * <p>
 * To enable Light air on a JUnit test, annotate it as follows:
 * <p>
 * <pre>
 * &#064;RunWith(LightAir.class)
 * public class MyTest {
 * }
 * </pre>
 * <p>
 * Then use annotations @{@link Setup}, @{@link Verify} to define actions Light
 * air should take on the test.
 */
public class LightAir extends BlockJUnit4ClassRunner implements Keywords {

	static {
		Api.initialize(Api.getPropertiesFileName());
		Runtime.getRuntime().addShutdownHook(new Thread(Api::shutdown));
	}

	public LightAir(Class<?> clazz) throws InitializationError {
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
