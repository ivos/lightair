package net.sf.lightair.exception;

public class UnsupportedDataType extends AbstractException {

	public UnsupportedDataType(String dataType) {
		super("Data type " + dataType + " is not supported with token @auto.");
	}

	private static final long serialVersionUID = 1L;

}
