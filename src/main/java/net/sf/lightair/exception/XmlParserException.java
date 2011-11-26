package net.sf.lightair.exception;

/**
 * Thrown when XML parser cannot initialize.
 */
public class XmlParserException extends AbstractException {

	/**
	 * Constructor.
	 * 
	 * @param cause
	 *            Cause exception
	 */
	public XmlParserException(Throwable cause) {
		super("Cannot initialize SAX parser to read dataset XML files.", cause);
	}

	private static final long serialVersionUID = 1L;

}
