package net.sf.lightair.exception;

/**
 * Thrown when setting up data source.
 */
public class DataSourceSetupException extends AbstractException {

	/**
	 * Constructor.
	 * 
	 * @param cause
	 *            Cause exception
	 */
	public DataSourceSetupException(Throwable cause) {
		super("Error setting up datasource.", cause);
	}

	private static final long serialVersionUID = 1L;

}
