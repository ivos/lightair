package net.sf.lightair.exception;

/**
 * Thrown when number of tables using <code>@auto</code> exceeds limit.
 */
public class AutoValueTableOverflowException extends AbstractException {

	/**
	 * Constructor.
	 * 
	 * @param tableName
	 */
	public AutoValueTableOverflowException(String tableName) {
		super("Number of tables with @auto value exceeds limit with table "
				+ tableName + ".");
	}

	private static final long serialVersionUID = 1L;

}
