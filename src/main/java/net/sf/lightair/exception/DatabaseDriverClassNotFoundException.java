package net.sf.lightair.exception;

import net.sf.lightair.exception.AbstractException;

/**
 * Thrown when database driver class was not found.
 */
public class DatabaseDriverClassNotFoundException extends AbstractException {

	/**
	 * Constructor.
	 * 
	 * @param driverClassName
	 *            Driver class name
	 * @param cause
	 *            Cause exception
	 */
	public DatabaseDriverClassNotFoundException(String driverClassName,
			Throwable cause) {
		super("Driver class was not found: " + driverClassName, cause);
	}

	private static final long serialVersionUID = 1L;

}
