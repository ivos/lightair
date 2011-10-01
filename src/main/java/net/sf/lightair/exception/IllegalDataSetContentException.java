package net.sf.lightair.exception;

public class IllegalDataSetContentException extends RuntimeException {

	public IllegalDataSetContentException(String fileName, Throwable cause) {
		super("Cannot load content of data set '" + fileName + "'.", cause);
	}

	private static final long serialVersionUID = 1L;

}
