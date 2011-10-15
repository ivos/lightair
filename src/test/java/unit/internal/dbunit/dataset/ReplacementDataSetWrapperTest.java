package unit.internal.dbunit.dataset;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.lightair.internal.dbunit.dataset.ReplacementDataSetWrapper;
import net.sf.seaf.test.jmock.JMockSupport;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.util.MultiSchemaDataSet;

public class ReplacementDataSetWrapperTest extends JMockSupport {

	ReplacementDataSetWrapper wrapper;
	MultiSchemaDataSet msds;
	Set<String> schemaNames;
	IDataSet ds1, ds2, ds3;
	ReplacementDataSet rds1, rds2, rds3;
	Map<IDataSet, ReplacementDataSet> replacementMap = new HashMap<IDataSet, ReplacementDataSet>();

	@Before
	public void before() {
		ds1 = mock(IDataSet.class, "ds1");
		ds2 = mock(IDataSet.class, "ds2");
		ds3 = mock(IDataSet.class, "ds3");
		rds1 = mock(ReplacementDataSet.class, "rds1");
		rds2 = mock(ReplacementDataSet.class, "rds2");
		rds3 = mock(ReplacementDataSet.class, "rds3");
		replacementMap.put(ds1, rds1);
		replacementMap.put(ds2, rds2);
		replacementMap.put(ds3, rds3);
		msds = new MultiSchemaDataSet();
		msds.setDataSetForSchema("schema1", ds1);
		msds.setDataSetForSchema("schema2", ds2);
		msds.setDataSetForSchema("schema3", ds3);
		wrapper = new ReplacementDataSetWrapper() {
			@Override
			protected ReplacementDataSet createReplacementDataSet(
					IDataSet dataSet) {
				return replacementMap.get(dataSet);
			}
		};
	}

	@Test
	public void wrap() {
		check(new Expectations() {
			{
				one(rds1).addReplacementObject("@null", null);

				one(rds2).addReplacementObject("@null", null);

				one(rds3).addReplacementObject("@null", null);
			}
		});

		MultiSchemaDataSet wrapped = wrapper.wrap(msds);

		for (String schemaName : wrapped.getSchemaNames()) {
			if ("schema1".equals(schemaName)) {
				verify(wrapped, schemaName, rds1);
			} else if ("schema2".equals(schemaName)) {
				verify(wrapped, schemaName, rds2);
			} else if ("schema3".equals(schemaName)) {
				verify(wrapped, schemaName, rds3);
			} else {
				fail("Invalid schema " + schemaName);
			}
		}
		assertEquals("Count", 3, wrapped.getSchemaNames().size());
	}

	private void verify(MultiSchemaDataSet wrapped, String schemaName,
			ReplacementDataSet rds) {
		assertEquals("ReplacementDataSet", rds,
				wrapped.getDataSetForSchema(schemaName));
	}
}
