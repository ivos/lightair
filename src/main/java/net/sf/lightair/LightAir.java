package net.sf.lightair;

import net.sf.lightair.annotation.Setup;
import net.sf.lightair.annotation.Verify;
import net.sf.lightair.internal.Keywords;
import net.sf.lightair.internal.junit.AbstractTestRule;
import net.sf.lightair.internal.junit.util.Factory;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Light Air JUnit Jupiter Extension.
 * <p>
 * To enable Light Air on a JUnit test, annotate it as follows:
 * <pre>
 * &#064;ExtendWith(LightAir.class)
 * public class MyTest {
 * }
 * </pre>
 * Then use annotations @{@link Setup}, @{@link Verify} to define actions Light
 * air should take on the test.
 *
 * <p>
 * See <a href="http://lightair.sourceforge.net/write-test.html">http://lightair.sourceforge.net/write-test.html</a>
 * for more info.
 * </p>
 */
public class LightAir implements InvocationInterceptor, Keywords {

	static {
		LightAirApi.initialize(LightAirApi.getPropertiesFileName());
		Runtime.getRuntime().addShutdownHook(new Thread(LightAirApi::shutdown));
	}

	@Override
	public void interceptTestMethod(
		@NonNull Invocation<Void> invocation,
		ReflectiveInvocationContext<Method> invocationContext,
		@NonNull ExtensionContext extensionContext
	) throws Throwable {
		Method method = invocationContext.getExecutable();
		List<AbstractTestRule<?>> testRules = Factory.getInstance().getAllTestRules(method);

		try {
			testRules.forEach(AbstractTestRule::before);
		} catch (Throwable t) {
			invocation.skip(); // Mark invocation as skipped to prevent JUnit from complaining about tests not run.
			throw t;
		}
		try {
			invocation.proceed();
		} finally {
			testRules.forEach(AbstractTestRule::after);
		}
	}
}
