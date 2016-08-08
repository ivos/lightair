package net.sf.lightair;

import net.sf.lightair.annotation.Setup;
import net.sf.lightair.annotation.Verify;
import net.sf.lightair.internal.util.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.internal.ConstructorOrMethod;

import java.lang.reflect.Method;

/**
 * Light air TestNG listener.
 * <p>
 * To enable Light air on a TestNG test, annotate it as follows:
 *
 * <pre>
 * &#064;Listeners(LightAirNGListener.class)
 * public class MyTest {
 * }
 * </pre>
 *
 * Then use annotations @{@link Setup}, @{@link Verify} to define actions Light
 * air should take on the test.
 */
public class LightAirNGListener implements IInvokedMethodListener, ITestListener {

	static {
		Api.initialize(Api.getPropertiesFileName());
		Runtime.getRuntime().addShutdownHook(new Thread(Api::shutdown));
	}

	private final Logger log = LoggerFactory.getLogger(LightAirNGListener.class);

	/**
	 * Method executed before every test method handling db setup and base url for JWebUnit
	 */
	public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {
		// light-air is for test methods only
		if (!iInvokedMethod.isTestMethod()) {
			return;
		}
		ConstructorOrMethod constructorOrMethod = iInvokedMethod.getTestMethod().getConstructorOrMethod();
		Method method = constructorOrMethod.getMethod();
		if (method != null) {
			Setup[] setups = getActiveSetupAnnotation(method);
			if (null != setups) {
				for (Setup set : setups) {
					Factory.getInstance().getSetupExecutor().execute(set, method);
				}
			}
		}
	}

	/**
	 * Method executed after every test method handling db verification
	 */
	public void afterInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {
		// light-air is for test methods only
		if (!iInvokedMethod.isTestMethod()) {
			return;
		}
		ConstructorOrMethod constructorOrMethod = iInvokedMethod.getTestMethod().getConstructorOrMethod();
		Method method = constructorOrMethod.getMethod();
		if (method != null) {
			Verify[] verifies = getActiveVerifyAnnotation(method);
			if (null != verifies) {
				for (Verify ver : verifies) {
					Factory.getInstance().getVerifyExecutor().execute(ver, method);
				}
			}
		}
	}

	/**
	 * Get Setup for executing before given method. If no @Setup or @Setup.List is present on the method, class annotations are used.
	 */
	private Setup[] getActiveSetupAnnotation(
			Method method) {
		Setup methodSetupAnnotation = method.getAnnotation(Setup.class);
		Setup.List methodListAnnotation = method.getAnnotation(Setup.List.class);
		Setup[] setups = null;
		if (null != methodSetupAnnotation) {
			setups = new Setup[]{methodSetupAnnotation};
		} else if (null != methodListAnnotation) {
			setups = methodListAnnotation.value();
		}
		if (null == setups) {
			Setup classSetupAnnotation = method.getDeclaringClass().getAnnotation(Setup.class);
			Setup.List classListAnnotation = method.getDeclaringClass().getAnnotation(Setup.List.class);
			if (null != classSetupAnnotation) {
				setups = new Setup[]{classSetupAnnotation};
			} else if (null != classListAnnotation) {
				setups = classListAnnotation.value();
			}
		}
		return setups;
	}

	/**
	 * Get Setup for executing before given method. If no @Verify or @Verify.List is present on the method, class annotations are used.
	 */
	private Verify[] getActiveVerifyAnnotation(
			Method method) {
		Verify methodVerifyAnnotation = method.getAnnotation(Verify.class);
		Verify.List methodListAnnotation = method.getAnnotation(Verify.List.class);
		Verify[] verifies = null;
		if (null != methodVerifyAnnotation) {
			verifies = new Verify[]{methodVerifyAnnotation};
		} else if (null != methodListAnnotation) {
			verifies = methodListAnnotation.value();
		}
		if (null == verifies) {
			Verify classVerifyAnnotation = method.getDeclaringClass().getAnnotation(Verify.class);
			Verify.List classListAnnotation = method.getDeclaringClass().getAnnotation(Verify.List.class);
			if (null != classVerifyAnnotation) {
				verifies = new Verify[]{classVerifyAnnotation};
			} else if (null != classListAnnotation) {
				verifies = classListAnnotation.value();
			}
		}
		return verifies;
	}

	// ITestListener's methods
	public void onFinish(ITestContext iTestContext) {
	}

	public void onTestStart(ITestResult iTestResult) {
	}

	public void onTestSuccess(ITestResult iTestResult) {
	}

	public void onTestFailure(ITestResult iTestResult) {
	}

	public void onTestSkipped(ITestResult iTestResult) {
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
	}

	public void onStart(ITestContext iTestContext) {
	}
}
