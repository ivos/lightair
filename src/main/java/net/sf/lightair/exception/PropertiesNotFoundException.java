package net.sf.lightair.exception;

/**
 * Thrown when Light air properties file was not found.
 */
public class PropertiesNotFoundException extends AbstractException {

	/**
	 * Constructor.
	 * 
	 * @param fileName
	 *            Name of properties file looked up
	 */
	public PropertiesNotFoundException(String fileName) {
		super("Light air properties file '" + fileName + "' not found.");
	}

	private static final long serialVersionUID = 1L;

}
