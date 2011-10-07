package net.sf.lightair.exception;

import java.util.Arrays;

public class IllegalDataSetContentException extends RuntimeException {

	public IllegalDataSetContentException(Throwable cause, String... fileNames) {
		super("Cannot load content of data set " + Arrays.asList(fileNames)
				+ ".", cause);
	}

	private static final long serialVersionUID = 1L;

}
