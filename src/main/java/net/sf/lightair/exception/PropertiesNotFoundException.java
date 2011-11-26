package net.sf.lightair.exception;

import net.sf.lightair.internal.properties.PropertiesProvider;

/**
 * Thrown when Light air properties file was not found.
 */
public class PropertiesNotFoundException extends AbstractException {

	/**
	 * Constructor.
	 * 
	 * @param cause
	 *            Cause exception
	 */
	public PropertiesNotFoundException(Throwable cause) {
		super("Light air properties file '"
				+ PropertiesProvider.PROPERTIES_FILE_NAME + "' not found.",
				cause);
	}

	private static final long serialVersionUID = 1L;

}
