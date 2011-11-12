package net.sf.lightair.internal.dbunit.dataset;

import java.util.HashMap;
import java.util.Map;

/**
 * Replaces tokens read from XML datasets.
 * <p>
 * Replaces the following tokens:
 * <ul>
 * <li>@null with <code>null</code> value</li>
 * </ul>
 */
public class TokenReplacingFilter {

	private final Map<Object, Object> tokens = new HashMap<Object, Object>();

	/**
	 * Default constructor.
	 */
	public TokenReplacingFilter() {
		init();
	}

	/**
	 * Initialize map of tokens.
	 */
	protected void init() {
		tokens.put("@null", null);
	}

	/**
	 * Replace possible token in a value.
	 * 
	 * @param value
	 *            Value possibly containing a token
	 * @return Value or token replacement
	 */
	public Object replaceTokens(Object value) {
		if (tokens.containsKey(value)) {
			return tokens.get(value);
		}
		return value;
	}

}
