package net.sf.lightair.exception;

public class XmlParserException extends RuntimeException {

	public XmlParserException(Throwable cause) {
		super("Cannot initialize SAX parser to read dataset XML files.", cause);
	}

	private static final long serialVersionUID = 1L;

}
