package net.sf.lightair.internal.unitils.compare;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resolves variable values.
 */
public class VariableResolver {

	private final Logger log = LoggerFactory.getLogger(VariableResolver.class);

	private final Map<Object, Object> variables = new HashMap<Object, Object>();

	/**
	 * Is the expected value a variable?
	 * 
	 * @param expectedValue
	 *            Expected value
	 * @return <code>true</code> iff the expected value a variable
	 */
	public boolean isVariable(Object expectedValue) {
		return String.valueOf(expectedValue).startsWith("$");
	}

	/**
	 * Resolve possible variable in expected value.
	 * <p>
	 * If the expected value is a variable, resolve it. On first occurrence of a
	 * given variable, the variable is created with the actual value. On any
	 * subsequent occurrence, the value of the variable is resolved as it was
	 * created.
	 * 
	 * @param expectedValue
	 *            Expected value, possibly a variable name
	 * @param actualValue
	 *            Actual value used to create a variable
	 * @return Resolved expected value
	 */
	public Object resolveValue(Object expectedValue, Object actualValue) {
		if (isVariable(expectedValue)) {
			if (variables.containsKey(expectedValue)) {
				Object variableValue = variables.get(expectedValue);
				log.debug("Returning variable {} value {}.", expectedValue,
						variableValue);
				return variableValue;
			}
			log.debug("Storing variable {} value {}.", expectedValue,
					actualValue);
			variables.put(expectedValue, actualValue);
			return actualValue;
		}
		return expectedValue;
	}

	/**
	 * Clear variable definitions.
	 */
	public void clear() {
		variables.clear();
	}

}
