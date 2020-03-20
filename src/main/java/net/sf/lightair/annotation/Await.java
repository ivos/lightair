package net.sf.lightair.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Verifies database after an asynchronous test.
 * Verifies the database repeatedly until either the result is successful or timeout has expired.
 * <p>
 * Annotate test method with @Await to verify database after the asynchronous test method
 * executed. Annotate the whole test class to have all its test methods verify
 * database asynchronously. For example:
 * </p>
 * <pre>
 * &#064;Test &#064;Await
 * public void myTestMethod {
 * }
 * </pre>
 * <p>
 * The datasets to be verified are located in exactly the same manner as when using the @{@link Verify} annotation
 * and must conform to exactly the same format.
 * </p>
 * <p>
 * Use @{@link Await.List} to define multiple <code>@Await</code> annotations
 * on the same element.
 * </p>
 *
 * <p>
 * See <a href="http://lightair.sourceforge.net/features/await.html">http://lightair.sourceforge.net/features/await.html</a>
 * for more info.
 * </p>
 *
 * @see Verify
 * @see Await.List
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Documented
public @interface Await {

	/**
	 * Names of files with datasets.
	 * @return datasets
	 */
	String[] value() default {};

	/**
	 * Name of the profile.
	 * <p>
	 * A profile lets you connect to a different database. A profile must be
	 * defined in LightAir properties file and must have its own properties file
	 * which defines the connection to the profile database.
	 * @return profile
	 */
	String profile() default "";

	/**
	 * Defines several @{@link Await} annotations on the same element.
	 *
	 * @see Await
	 */
	@Target({TYPE, METHOD})
	@Retention(RUNTIME)
	@Documented
	@interface List {
		Await[] value();
	}
}
