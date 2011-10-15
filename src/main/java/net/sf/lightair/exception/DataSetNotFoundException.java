package net.sf.lightair.exception;

import java.util.Arrays;

public class DataSetNotFoundException extends IllegalArgumentException {

	public DataSetNotFoundException(String... fileNames) {
		super(getMessage(fileNames));
	}

	public DataSetNotFoundException(Throwable cause, String... fileNames) {
		super(getMessage(fileNames), cause);
	}

	private static String getMessage(String... fileNames) {
		return "Data set not found " + Arrays.toString(fileNames) + ".";
	}

	private static final long serialVersionUID = 1L;

}
