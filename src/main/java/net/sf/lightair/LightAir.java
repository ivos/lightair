package net.sf.lightair;

import java.util.Arrays;
import java.util.List;

import net.sf.lightair.support.factory.Factory;
import net.sf.lightair.support.junit.SetupTestRule;

import org.junit.rules.RunRules;
import org.junit.rules.TestRule;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class LightAir extends BlockJUnit4ClassRunner {

	public LightAir(Class<?> clazz) throws InitializationError {
		super(clazz);
	}

	@Override
	protected Statement methodBlock(FrameworkMethod method) {
		Statement statement = super.methodBlock(method);
		List<TestRule> lightAirRules = Arrays.asList(new SetupTestRule(
				getTestClass(), method), Factory.getInstance()
				.getVerifyTestRule(method));
		statement = new RunRules(statement, lightAirRules,
				describeChild(method));
		return statement;
	}

}
