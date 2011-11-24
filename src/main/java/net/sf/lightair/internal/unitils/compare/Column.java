package net.sf.lightair.internal.unitils.compare;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.TypeCastException;
import org.unitils.core.UnitilsException;
import org.unitils.dbunit.dataset.comparison.ColumnDifference;

/**
 * Fork of Unitils Column to allow for customization.
 */
public class Column extends org.unitils.dbunit.dataset.Column {

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            Column name
	 * @param type
	 *            Column type
	 * @param value
	 *            Column value
	 */
	public Column(String name, DataType type, Object value) {
		super(name, type, value);
	}

	// Extracted to support variables

	/**
	 * Pre-compare this expected column value with actual column value.
	 * <p>
	 * If expected value is a variable, it is considered a non-match.
	 * 
	 * @param actualColumn
	 *            Actual column
	 * @return <code>null</code> if values match, {@link ColumnDifference}
	 *         otherwise
	 */
	public ColumnDifference preCompare(
			org.unitils.dbunit.dataset.Column actualColumn) {
		if (valuesSame(actualColumn)) {
			return null;
		}
		if (valueNullAndActualNotNull(actualColumn)) {
			return new ColumnDifference(this, actualColumn);
		}
		if (variableResolver.isVariable(getValue())) {
			return new ColumnDifference(this, actualColumn);
		}
		if (!isCastedValueEqual(getValue(), actualColumn)) {
			return new ColumnDifference(this, actualColumn);
		}
		return null;
	}

	/**
	 * Compare this expected column value with actual column value.
	 * <p>
	 * If expected value is a variable, its value is resolved.
	 * 
	 * @param actualColumn
	 *            Actual column
	 * @return <code>null</code> if values match, {@link ColumnDifference}
	 *         otherwise
	 */
	@Override
	public ColumnDifference compare(
			org.unitils.dbunit.dataset.Column actualColumn) {
		if (valuesSame(actualColumn)) {
			return null;
		}
		if (valueNullAndActualNotNull(actualColumn)) {
			return new ColumnDifference(this, actualColumn);
		}
		Object value = variableResolver.resolveValue(getValue(),
				actualColumn.getValue());
		if (!isCastedValueEqual(value, actualColumn)) {
			return new ColumnDifference(this, actualColumn);
		}
		return null;
	}

	private boolean valuesSame(org.unitils.dbunit.dataset.Column actualColumn) {
		return (getValue() == actualColumn.getValue());
	}

	private boolean valueNullAndActualNotNull(
			org.unitils.dbunit.dataset.Column actualColumn) {
		return (getValue() == null && actualColumn.getValue() != null);
	}

	private boolean isCastedValueEqual(Object value,
			org.unitils.dbunit.dataset.Column actualColumn) {
		Object castedValue = getCastedValue(value, actualColumn.getType());
		return castedValue.equals(actualColumn.getValue());
	}

	private Object getCastedValue(Object value, DataType castType) {
		try {
			return castType.typeCast(value);
		} catch (TypeCastException e) {
			throw new UnitilsException("Unable to convert \"" + value
					+ "\" to " + castType.toString() + ". Column name: "
					+ getName() + ", current type: " + getType().toString(), e);
		}
	}

	// beans and setters

	private VariableResolver variableResolver;

	/**
	 * Set variable resolver.
	 * 
	 * @param variableResolver
	 *            Variable resolver
	 */
	public void setVariableResolver(VariableResolver variableResolver) {
		this.variableResolver = variableResolver;
	}

}
