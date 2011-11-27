package net.sf.lightair.exception;

/**
 * Thrown when Light air properties file cannot be read.
 */
public class PropertiesUnreadableException extends AbstractException {

	/**
	 * Constructor.
	 * 
	 * @param fileName
	 *            Name of properties file
	 */
	public PropertiesUnreadableException(String fileName) {
		super("Light air properties file '" + fileName + "' cannot be read.");
	}

	private static final long serialVersionUID = 1L;

}
