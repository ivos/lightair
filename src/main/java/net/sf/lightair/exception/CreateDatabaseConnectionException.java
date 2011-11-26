package net.sf.lightair.exception;

/**
 * Thrown when creating connection to database.
 */
public class CreateDatabaseConnectionException extends AbstractException {

	/**
	 * Constructor.
	 * 
	 * @param cause
	 *            Cause exception
	 */
	public CreateDatabaseConnectionException(Throwable cause) {
		super("Cannot connect to database.", cause);
	}

	private static final long serialVersionUID = 1L;

}
