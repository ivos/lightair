package unit.internal.dbunit.dataset;

import static org.junit.Assert.*;
import net.sf.lightair.internal.dbunit.dataset.MutableTableMetaData;
import net.sf.seaf.test.jmock.JMockSupport;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.NoSuchColumnException;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

public class MutableTableMetaDataTest extends JMockSupport {

	MutableTableMetaData md;
	Column c1, c2, c3, cNew;

	@Before
	public void before() {
		c1 = mock(Column.class, "c1");
		c2 = mock(Column.class, "c2");
		c3 = mock(Column.class, "c3");
		cNew = mock(Column.class, "cNew");
		md = new MutableTableMetaData("tableName", c1, c2, c3);
	}

	@Test
	public void test() throws NoSuchColumnException {
		check(new Expectations() {
			{
				allowing(c1).getColumnName();
				will(returnValue("c1Name"));

				allowing(c2).getColumnName();
				will(returnValue("c2Name"));

				allowing(c3).getColumnName();
				will(returnValue("c3Name"));

				allowing(cNew).getColumnName();
				will(returnValue("cNewName"));
			}
		});

		assertEquals("getTableName", "tableName", md.getTableName());
		assertArrayEquals("getColumns", new Column[] { c1, c2, c3 },
				md.getColumns());
		assertArrayEquals("getPrimaryKeys", new Column[0], md.getPrimaryKeys());

		assertEquals("getColumnIndex 0", 0, md.getColumnIndex("c1Name"));
		assertEquals("getColumnIndex 1", 1, md.getColumnIndex("c2Name"));
		assertEquals("getColumnIndex 2", 2, md.getColumnIndex("c3Name"));
		assertEquals("getColumnIndex 2 case", 2, md.getColumnIndex("C3NAME"));
		try {
			md.getColumnIndex("NonExistent");
			fail("Should throw");
		} catch (NoSuchColumnException e) {
			assertEquals("tableName.NonExistent - column not found. "
					+ "Note that column names are NOT case sensitive.",
					e.getMessage());
		}

		assertTrue("hasColumn 0", md.hasColumn("c1Name"));
		assertTrue("hasColumn 1", md.hasColumn("c2Name"));
		assertTrue("hasColumn 2", md.hasColumn("c3Name"));
		assertTrue("hasColumn 2 case", md.hasColumn("C3NAME"));
		assertFalse("hasColumn non-existent", md.hasColumn("NonExistent"));

		assertFalse("addColumn before", md.hasColumn("cNewName"));
		md.addColumn(cNew);
		assertTrue("addColumn after", md.hasColumn("cNewName"));
	}
}
