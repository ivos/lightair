package net.sf.lightair.exception;

import net.sf.lightair.support.properties.PropertiesProvider;

public class PropertiesNotFoundException extends RuntimeException {

	public PropertiesNotFoundException(Throwable cause) {
		super("Light air properties file '"
				+ PropertiesProvider.PROPERTIES_FILE_NAME + "' not found.",
				cause);
	}

	private static final long serialVersionUID = 1L;

}
