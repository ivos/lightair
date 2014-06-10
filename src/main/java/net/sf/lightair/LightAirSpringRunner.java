package net.sf.lightair;

import net.sf.lightair.internal.factory.Factory;
import org.junit.rules.RunRules;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class LightAirSpringRunner extends SpringJUnit4ClassRunner {

    public LightAirSpringRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        Statement statement = super.methodInvoker(method, test);
        return new RunRules(statement, Factory.getInstance().getAllTestRules(method), describeChild(method));
    }

}
