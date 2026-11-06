package test.support;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.fail;

public class ExceptionVerifyingExtension implements InvocationInterceptor {

	@Override
	public void interceptTestMethod(
		@NonNull Invocation<Void> invocation,
		ReflectiveInvocationContext<Method> invocationContext,
		@NonNull ExtensionContext extensionContext
	) {
		Method testMethod = invocationContext.getExecutable();
		Object testInstance = extensionContext.getRequiredTestInstance();
		ExceptionVerifyingTestRule testRule = new ExceptionVerifyingTestRule(testMethod, testInstance);
		try {
			invocation.proceed();
		} catch (Throwable error) {
			testRule.verifyException(error);
			return; // Exception successfully verified
		}
		fail("Should throw");
	}
}
