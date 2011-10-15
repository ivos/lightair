package net.sf.lightair.internal.dbunit.dataset;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.unitils.dbunit.util.MultiSchemaDataSet;

/**
 * Wraps every dataset in a multi-schema dataset in a replacement dataset
 * replacing:
 * <ul>
 * <li>@null with null value</li>
 * </ul>
 */
public class ReplacementDataSetWrapper {

	/**
	 * Wrap datasets providing common replacements, see
	 * {@link ReplacementDataSetWrapper}.
	 * 
	 * @param multiSchemaDataSet
	 *            Multi-schema dataset
	 * @return Multi-schema dataset with replacement wrappers
	 */
	public MultiSchemaDataSet wrap(MultiSchemaDataSet multiSchemaDataSet) {
		for (String schemaName : multiSchemaDataSet.getSchemaNames()) {
			IDataSet dataSet = multiSchemaDataSet
					.getDataSetForSchema(schemaName);
			ReplacementDataSet replacementDataSet = createReplacementDataSet(dataSet);
			addReplacements(replacementDataSet);
			multiSchemaDataSet.setDataSetForSchema(schemaName,
					replacementDataSet);
		}
		return multiSchemaDataSet;
	}

	/**
	 * Add the replacements to ReplacementDataSet.
	 * 
	 * @param replacementDataSet
	 */
	private void addReplacements(ReplacementDataSet replacementDataSet) {
		replacementDataSet.addReplacementObject("@null", null);
	}

	/**
	 * Instantiate ReplacementDataSet.
	 * <p>
	 * Test enabler.
	 * 
	 * @param dataSet
	 *            Dataset to wrap
	 * @return Wrapping ReplacementDataSet
	 */
	protected ReplacementDataSet createReplacementDataSet(IDataSet dataSet) {
		return new ReplacementDataSet(dataSet);
	}

}
