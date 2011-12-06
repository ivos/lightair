package net.sf.lightair.internal.dbunit.dataset;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Replaces tokens read from XML datasets.
 * <p>
 * Replaces the following tokens:
 * <ul>
 * <li>@null with <code>null</code> value</li>
 * </ul>
 */
public class TokenReplacingFilter {

	private final Logger log = LoggerFactory
			.getLogger(TokenReplacingFilter.class);

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
			Object replaced = tokens.get(value);
			log.debug("Replaced token [{}] with value [{}].", value, replaced);
			return replaced;
		}
		return value;
	}

}
