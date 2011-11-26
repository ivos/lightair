package net.sf.lightair.exception;

/**
 * Abstract base class of all exceptions.
 */
public abstract class AbstractException extends RuntimeException {

	/**
	 * Constructor.
	 * 
	 * @param message
	 *            Message
	 */
	public AbstractException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 *            Message
	 * @param cause
	 *            Cause exception
	 */
	public AbstractException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1L;

}
