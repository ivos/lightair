package net.sf.lightair.exception;

public class InvalidDurationFormatException extends AbstractException {

	public InvalidDurationFormatException(String duration) {
		super("Duration [" + duration + "] does not match the required format.");
	}

	private static final long serialVersionUID = 1L;

}
