package net.sf.lightair.exception;

import org.dbunit.dataset.datatype.DataType;

/**
 * Thrown when <code>@auto</code> generates a duplicate value to guarantee
 * uniqueness of all generated <code>@auto</code> values.
 */
public class DuplicateAutoValueException extends AbstractException {

	/**
	 * Constructor.
	 * 
	 * @param tableName
	 * @param columnName
	 * @param dataType
	 * @param value
	 */
	public DuplicateAutoValueException(String tableName, String columnName,
			DataType dataType, String value) {
		super(
				"Duplicate @auto value generated for "
						+ tableName
						+ "."
						+ columnName
						+ " as type "
						+ dataType
						+ ". Value ["
						+ value
						+ "] has already been generated before. "
						+ "Please do not use @auto for this value to guarantee uniqueness.");
	}

	private static final long serialVersionUID = 1L;

}
