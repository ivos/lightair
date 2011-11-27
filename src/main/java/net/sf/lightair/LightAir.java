package net.sf.lightair;

import java.util.Arrays;
import java.util.List;

import net.sf.lightair.annotation.Setup;
import net.sf.lightair.annotation.Verify;
import net.sf.lightair.internal.factory.Factory;

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
	@Override
	protected Statement methodBlock(FrameworkMethod method) {
		Statement statement = super.methodBlock(method);

		List<TestRule> lightAirRules = createTestRules(method);
		statement = new RunRules(statement, lightAirRules,
				describeChild(method));
		return statement;
	}

	protected List<TestRule> createTestRules(FrameworkMethod method) {
		return Arrays.asList(
				(TestRule) Factory.getInstance().getSetupTestRule(method),
				(TestRule) Factory.getInstance().getVerifyTestRule(method),
				(TestRule) Factory.getInstance().getBaseUrlTestRule(method));
	}

}
