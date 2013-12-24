package net.sf.lightair.exception;

/**
 * Thrown when number of columns with <code>@auto</code> on a table exceeds
 * limit.
 */
public class AutoValueColumnOverflowException extends AbstractException {

	/**
	 * Constructor.
	 * 
	 * @param tableName
	 */
	public AutoValueColumnOverflowException(String tableName) {
		super("Number of columns with @auto value exceeds limit on table "
				+ tableName + ".");
	}

	private static final long serialVersionUID = 1L;

}
