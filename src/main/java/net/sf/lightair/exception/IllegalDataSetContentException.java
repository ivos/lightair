package net.sf.lightair.exception;

import java.util.Arrays;

/**
 * Thrown when dataset cannot be parsed.
 */
public class IllegalDataSetContentException extends AbstractException {

	/**
	 * Constructor.
	 * 
	 * @param cause
	 *            Cause exception
	 * @param fileNames
	 *            Names of dataset files
	 */
	public IllegalDataSetContentException(Throwable cause, String... fileNames) {
		super("Cannot load content of data set " + Arrays.toString(fileNames)
				+ ".", cause);
	}

	private static final long serialVersionUID = 1L;

}
