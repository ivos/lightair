package net.sf.lightair;

import java.util.Arrays;
import java.util.List;

import net.sf.lightair.annotation.Setup;
import net.sf.lightair.annotation.Verify;
import net.sf.lightair.internal.factory.Factory;

import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.statements.Fail;
import org.junit.rules.RunRules;
import org.junit.rules.TestRule;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * Light air JUnit runner.
 * <p>
 * To enable Light air on a JUnit test, annotate it as follows:
 * 
 * <pre>
 * &#064;RunWith(LightAir.class)
 * public class MyTest {
 * }
 * </pre>
 * 
 * Then use annotations @{@link Setup}, @{@link Verify} to define actions Light
 * air should take on the test.
 */
public class LightAir extends BlockJUnit4ClassRunner {

	public LightAir(Class<?> clazz) throws InitializationError {
		super(clazz);
	}

	/**
	 * Overridden to add test rules to returned statement.
	 * 
	 * @param method
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected Statement methodBlock(FrameworkMethod method) {
		Object test;
		try {
			test = new ReflectiveCallable() {
				@Override
				protected Object runReflectiveCall() throws Throwable {
					return createTest();
				}
			}.run();
		} catch (Throwable e) {
			return new Fail(e);
		}

		Statement statement = methodInvoker(method, test);

		// >>> LightAir start: add LightAir rules

		// must insert LightAir rules here to ensure that the database setup is
		// performed AFTER all the user-defined @Before methods are executed
		List<TestRule> lightAirRules = createTestRules(method);
		statement = new RunRules(statement, lightAirRules,
				describeChild(method));

		// <<< LightAir end

		statement = possiblyExpectingExceptions(method, test, statement);
		statement = withPotentialTimeout(method, test, statement);
		statement = withBefores(method, test, statement);
		statement = withAfters(method, test, statement);
		statement = withRules(method, test, statement);
		return statement;
	}

	/**
	 * Create LightAir test rules.
	 * 
	 * @param method
	 *            a test method
	 * @return rules for the test method
	 */
	protected List<TestRule> createTestRules(FrameworkMethod method) {
		return Arrays.asList(
				(TestRule) Factory.getInstance().getSetupTestRule(method),
				(TestRule) Factory.getInstance().getSetupListTestRule(method),
				(TestRule) Factory.getInstance().getVerifyTestRule(method),
				(TestRule) Factory.getInstance().getBaseUrlTestRule(method));
	}

	// copy & paste of private (!) methods from JUnit

	private Statement withRules(FrameworkMethod method, Object target,
			Statement statement) {
		List<TestRule> testRules = getTestRules(target);
		Statement result = statement;
		result = withMethodRules(method, testRules, target, result);
		result = withTestRules(method, testRules, result);

		return result;
	}

	private Statement withMethodRules(FrameworkMethod method,
			List<TestRule> testRules, Object target, Statement result) {
		for (org.junit.rules.MethodRule each : getMethodRules(target)) {
			if (!testRules.contains(each)) {
				result = each.apply(result, method, target);
			}
		}
		return result;
	}

	private List<org.junit.rules.MethodRule> getMethodRules(Object target) {
		return rules(target);
	}

	private Statement withTestRules(FrameworkMethod method,
			List<TestRule> testRules, Statement statement) {
		return testRules.isEmpty() ? statement : new RunRules(statement,
				testRules, describeChild(method));
	}

}
