package net.sf.lightair.internal.util;

/**
 * Collects data during processing of a data set.
 */
public class DataSetProcessingData {

	private Boolean tokenAnyPresent;

	public boolean isTokenAnyPresent() {
		return Boolean.TRUE.equals(tokenAnyPresent);
	}

	public void setTokenAnyPresent() {
		this.tokenAnyPresent = true;
	}

}
