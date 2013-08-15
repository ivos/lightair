package net.sf.lightair.exception;

/**
 * Thrown when token {@code @any} is found in a setup dataset.
 */
public class TokenAnyInSetupException extends AbstractException {

	public TokenAnyInSetupException() {
		super("Token @any found in setup dataset."
				+ " This token is only allowed in verification datasets.");
	}

	private static final long serialVersionUID = 1L;

}
