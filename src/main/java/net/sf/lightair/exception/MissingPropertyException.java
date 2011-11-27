package net.sf.lightair.exception;

/**
 * Thrown when a property is not defined in properties file.
 */
public class MissingPropertyException extends AbstractException {

	/**
	 * Constructor.
	 * 
	 * @param propertyName
	 *            Name of property
	 */
	public MissingPropertyException(String propertyName) {
		super("Property '" + propertyName
				+ "' not found in the properties file.");
	}

	private static final long serialVersionUID = 1L;

}
