package net.sf.lightair.exception;

/**
 * Thrown when accessing database.
 */
public class DatabaseAccessException extends AbstractException {

	/**
	 * Constructor.
	 * 
	 * @param cause
	 *            Cause exception
	 */
	public DatabaseAccessException(Throwable cause) {
		super("Error accessing database.", cause);
	}

	private static final long serialVersionUID = 1L;

}
