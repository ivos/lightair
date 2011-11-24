package unit.internal.dbunit.dataset;

import static org.junit.Assert.*;
import net.sf.lightair.internal.dbunit.dataset.MergingTable;
import net.sf.lightair.internal.dbunit.dataset.MutableTableMetaData;
import net.sf.lightair.internal.dbunit.dataset.TokenReplacingFilter;
import net.sf.seaf.test.jmock.JMockSupport;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.RowOutOfBoundsException;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

public class MergingTableTest extends JMockSupport {

	MergingTable table, otherTable;
	MutableTableMetaData metaData;
	Object v11, v12, v13, v21, v22, v23, ov11, ov12, ov13, ov14, ov21, ov22,
			ov23, ov24, ov31, ov32, ov33, ov34;
	Object v11r, v12r, v13r;
	Column c1, c2, c3, other1, other2;
	TokenReplacingFilter tokenReplacingFilter;

	@Before
	public void before() {
		v11 = mock(Object.class, "v11");
		v12 = mock(Object.class, "v12");
		v13 = mock(Object.class, "v13");
		v21 = mock(Object.class, "v21");
		v22 = mock(Object.class, "v22");
		v23 = mock(Object.class, "v23");
		c1 = mock(Column.class, "c1");
		c2 = mock(Column.class, "c2");
		c3 = mock(Column.class, "c3");
	}

	@Test
	public void test() throws RowOutOfBoundsException {
		metaData = mock(MutableTableMetaData.class);
		table = new MergingTable(metaData);
		check(new Expectations() {
			{
				exactly(2).of(metaData).getColumns();
				will(returnValue(new Column[] { c1, c2, c3 }));

				exactly(2).of(c1).getColumnName();
				will(returnValue("c1Name"));

				exactly(2).of(c2).getColumnName();
				will(returnValue("c2Name"));

				exactly(2).of(c3).getColumnName();
				will(returnValue("c3Name"));
			}
		});

		assertEquals("getTableMetaData", metaData, table.getTableMetaData());
		assertEquals("getRowCount 0", 0, table.getRowCount());
		table.addRow(v11, v12, v13);
		assertEquals("getRowCount 1", 1, table.getRowCount());
		table.addRow(v21, v22, v23);
		assertEquals("getRowCount 2", 2, table.getRowCount());

		assertEquals("getValue 11", v11, table.getValue(0, "c1Name"));
		assertEquals("getValue 13", v13, table.getValue(0, "c3Name"));
		assertEquals("getValue 23", v23, table.getValue(1, "c3Name"));
		assertEquals("getValue 23 case", v23, table.getValue(1, "C3NAME"));
	}

	@Test
	public void replacing() throws RowOutOfBoundsException {
		metaData = mock(MutableTableMetaData.class);
		table = new MergingTable(metaData);
		tokenReplacingFilter = mock(TokenReplacingFilter.class);
		table.setTokenReplacingFilter(tokenReplacingFilter);
		v11r = mock(Object.class, "v11r");
		v12r = mock(Object.class, "v12r");
		v13r = mock(Object.class, "v13r");
		check(new Expectations() {
			{
				one(metaData).getColumns();
				will(returnValue(new Column[] { c1, c2, c3 }));

				one(c1).getColumnName();
				will(returnValue("c1Name"));

				one(c2).getColumnName();
				will(returnValue("c2Name"));

				one(c3).getColumnName();
				will(returnValue("c3Name"));

				one(tokenReplacingFilter).replaceTokens(v11);
				will(returnValue(v11r));

				one(tokenReplacingFilter).replaceTokens(v12);
				will(returnValue(v12r));

				one(tokenReplacingFilter).replaceTokens(v13);
				will(returnValue(v13r));
			}
		});

		table.addRow(v11, v12, v13);

		assertSame(v11r, table.getValue(0, "c1Name"));
		assertSame(v12r, table.getValue(0, "c2Name"));
		assertSame(v13r, table.getValue(0, "c3Name"));
	}

	@Test
	public void addTableRows() throws RowOutOfBoundsException {
		metaData = new MutableTableMetaData("tableName", c1, c2, c3);
		table = new MergingTable(metaData);
		other1 = mock(Column.class, "other1");
		other2 = mock(Column.class, "other2");
		otherTable = new MergingTable(new MutableTableMetaData("otherTable",
				c2, other1, c3, other2));
		ov11 = mock(Object.class, "ov11");
		ov12 = mock(Object.class, "ov12");
		ov13 = mock(Object.class, "ov13");
		ov14 = mock(Object.class, "ov14");
		ov21 = mock(Object.class, "ov21");
		ov22 = mock(Object.class, "ov22");
		ov23 = mock(Object.class, "ov23");
		ov24 = mock(Object.class, "ov24");
		ov31 = mock(Object.class, "ov31");
		ov32 = mock(Object.class, "ov32");
		ov33 = mock(Object.class, "ov33");
		ov34 = mock(Object.class, "ov34");
		check(new Expectations() {
			{
				allowing(c1).getColumnName();
				will(returnValue("c1Name"));

				allowing(c2).getColumnName();
				will(returnValue("c2Name"));

				allowing(c3).getColumnName();
				will(returnValue("c3Name"));

				allowing(other1).getColumnName();
				will(returnValue("other1Name"));

				allowing(other2).getColumnName();
				will(returnValue("other2Name"));
			}
		});
		table.addRow(v11, v12, v13);
		table.addRow(v21, v22, v23);
		otherTable.addRow(ov11, ov12, ov13, ov14);
		otherTable.addRow(ov21, ov22, ov23, ov24);
		otherTable.addRow(ov31, ov32, ov33, ov34);

		table.addTableRows(otherTable);

		assertEquals("Table name remains", "tableName", table
				.getTableMetaData().getTableName());
		assertArrayEquals("Columns added", new Column[] { c1, c2, c3, other1,
				other2 }, table.getTableMetaData().getColumns());
		assertEquals("Added rows", 5, table.getRowCount());
		verifyMergedRow(table, 0, v11, v12, v13, null, null);
		verifyMergedRow(table, 1, v21, v22, v23, null, null);
		verifyMergedRow(table, 2, null, ov11, ov13, ov12, ov14);
		verifyMergedRow(table, 3, null, ov21, ov23, ov22, ov24);
		verifyMergedRow(table, 4, null, ov31, ov33, ov32, ov34);
	}

	private void verifyMergedRow(MergingTable table, int rowId, Object v1,
			Object v2, Object v3, Object otherv1, Object otherv2)
			throws RowOutOfBoundsException {
		assertEquals("Row value " + rowId + "1", v1,
				table.getValue(rowId, "c1Name"));
		assertEquals("Row value " + rowId + "2", v2,
				table.getValue(rowId, "c2Name"));
		assertEquals("Row value " + rowId + "3", v3,
				table.getValue(rowId, "c3Name"));
		assertEquals("Row value " + rowId + "o1", otherv1,
				table.getValue(rowId, "other1Name"));
		assertEquals("Row value " + rowId + "o2", otherv2,
				table.getValue(rowId, "other2Name"));
	}
}
