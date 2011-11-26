package net.sf.lightair.exception;

/**
 * Thrown when closing connection to database.
 */
public class CloseDatabaseConnectionException extends AbstractException {

	/**
	 * Constructor.
	 * 
	 * @param cause
	 *            Cause exception
	 */
	public CloseDatabaseConnectionException(Throwable cause) {
		super("Cannot close connection to database.", cause);
	}

	private static final long serialVersionUID = 1L;

}
