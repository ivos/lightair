package net.sf.lightair.exception;

import java.util.Arrays;

public class DataSetNotFoundException extends IllegalArgumentException {

	public DataSetNotFoundException(String... fileNames) {
		super("Data set not found " + Arrays.toString(fileNames) + ".");
	}

	private static final long serialVersionUID = 1L;

}
