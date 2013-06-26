package net.sf.lightair.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.sourceforge.jwebunit.junit.JWebUnit;

/**
 * Sets the base URL for the test context. A trailing "/" is appended if not
 * provided.
 * <p>
 * Annotate test method with @BaseUrl to set the base URL for the test method.
 * Annotate the whole test class to have all its test methods set the base URL
 * to the same value. For example:
 * 
 * <pre>
 * &#064;Test &#064;BaseUrl("http://localhost:8080/my-app/faces")
 * public void register {
 * 	beginAt("register.xhtml");
 * }
 * </pre>
 * <p>
 * Calls {@link JWebUnit#setBaseUrl}.
 */
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
@Inherited
public @interface BaseUrl {

	/**
	 * Base URL value - A trailing "/" is appended if not provided.
	 * 
	 * @return
	 */
	String value();

}
