package net.sf.lightair.exception;

import java.util.Arrays;

/**
 * Thrown when dataset was not found.
 */
public class DataSetNotFoundException extends AbstractException {

	/**
	 * Constructor.
	 * 
	 * @param fileNames
	 *            Names of dataset files
	 */
	public DataSetNotFoundException(String... fileNames) {
		super(formatMessage(fileNames));
	}

	/**
	 * Constructor.
	 * 
	 * @param cause
	 *            Cause exception
	 * @param fileNames
	 *            Names of dataset files
	 */
	public DataSetNotFoundException(Throwable cause, String... fileNames) {
		super(formatMessage(fileNames), cause);
	}

	/**
	 * Format the exception message.
	 * 
	 * @param fileNames
	 *            Names of dataset files
	 * @return Message
	 */
	private static String formatMessage(String... fileNames) {
		return "Data set not found " + Arrays.toString(fileNames) + ".";
	}

	private static final long serialVersionUID = 1L;

}
