package unit.internal.auto;

import net.sf.lightair.internal.Keywords;
import net.sf.lightair.internal.auto.Auto;
import net.sf.lightair.internal.auto.Index;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class AutoTest implements Keywords {

	@Test
	public void dataTypes() {
		Map<String, String> index = new HashMap<>();
		index.put(Index.formatTableKey("p1", "t1"), "2345");
		index.put(Index.formatColumnKey("p1", "t1", BOOLEAN), "001");
		index.put(Index.formatColumnKey("p1", "t1", BYTE), "002");
		index.put(Index.formatColumnKey("p1", "t1", SHORT), "876");
		index.put(Index.formatColumnKey("p1", "t1", INTEGER), "678");
		index.put(Index.formatColumnKey("p1", "t1", LONG), "005");
		index.put(Index.formatColumnKey("p1", "t1", FLOAT), "006");
		index.put(Index.formatColumnKey("p1", "t1", DOUBLE), "007");
		index.put(Index.formatColumnKey("p1", "t1", BIGDECIMAL), "008");
		index.put(Index.formatColumnKey("p1", "t1", DATE), "009");
		index.put(Index.formatColumnKey("p1", "t1", TIME), "010");
		index.put(Index.formatColumnKey("p1", "t1", TIMESTAMP), "010");
		index.put(Index.formatColumnKey("p1", "t1", "string_column"), "765");
		index.put(Index.formatColumnKey("p1", "t1", "fixed_string_column"), "764");
		index.put(Index.formatColumnKey("p1", "t1", "nstring_column"), "012");
		index.put(Index.formatColumnKey("p1", "t1", "fixed_nstring_column"), "763");
		index.put(Index.formatColumnKey("p1", "t1", "bytes_column"), "013");
		index.put(Index.formatColumnKey("p1", "t1", "clob_column"), "014");
		index.put(Index.formatColumnKey("p1", "t1", "nclob_column"), "015");
		index.put(Index.formatColumnKey("p1", "t1", "blob_column"), "016");
		index.put(Index.formatColumnKey("p1", "t1", "uuid_column"), "101");
		assertEquals("false", Auto.generate(index, "p1", "t1", BOOLEAN, 98, BOOLEAN, 0, null));
		assertEquals("true", Auto.generate(index, "p1", "t1", BOOLEAN, 99, BOOLEAN, 0, null));
		assertEquals("98", Auto.generate(index, "p1", "t1", BYTE, 98, BYTE, 0, null));
		assertEquals("7698", Auto.generate(index, "p1", "t1", SHORT, 98, SHORT, 0, null));
		assertEquals("1234567890", Auto.generate(index, "p1", "t1", INTEGER, 90, INTEGER, 0, null));
		assertEquals("1234500598", Auto.generate(index, "p1", "t1", LONG, 98, LONG, 0, null));
		assertEquals("1.234500698E7", Auto.generate(index, "p1", "t1", FLOAT, 98, FLOAT, 0, null));
		assertEquals("1.234500798E7", Auto.generate(index, "p1", "t1", DOUBLE, 98, DOUBLE, 0, null));
		assertEquals("23450.0898", Auto.generate(index, "p1", "t1", BIGDECIMAL, 98, BIGDECIMAL, 9, 4));
		assertEquals("345008.98", Auto.generate(index, "p1", "t1", BIGDECIMAL, 98, BIGDECIMAL, 8, 2));
		assertEquals("1234500.898", Auto.generate(index, "p1", "t1", BIGDECIMAL, 98, BIGDECIMAL, 12, 3));
		assertEquals("2094-05-21", Auto.generate(index, "p1", "t1", DATE, 98, DATE, 0, null));
		assertEquals("04:58:18", Auto.generate(index, "p1", "t1", TIME, 98, TIME, 0, null));
		assertEquals("2094-08-29T04:58:18.098", Auto.generate(index, "p1", "t1", TIMESTAMP, 98, TIMESTAMP, 0, null));
		assertEquals("string_column 1234576598",
				Auto.generate(index, "p1", "t1", "string_column", 98, STRING, 100, null));
		assertEquals("string_column 1234576598",
				Auto.generate(index, "p1", "t1", "string_column", 98, STRING, 24, null));
		assertEquals("string_column1234576598",
				Auto.generate(index, "p1", "t1", "string_column", 98, STRING, 23, null));
		assertEquals("strin1234576598",
				Auto.generate(index, "p1", "t1", "string_column", 98, STRING, 15, null));
		assertEquals("s1234576598",
				Auto.generate(index, "p1", "t1", "string_column", 98, STRING, 11, null));
		assertEquals("1234576598",
				Auto.generate(index, "p1", "t1", "string_column", 98, STRING, 10, null));
		assertEquals("234576598",
				Auto.generate(index, "p1", "t1", "string_column", 98, STRING, 9, null));
		assertEquals("98",
				Auto.generate(index, "p1", "t1", "string_column", 98, STRING, 2, null));
		assertEquals("8",
				Auto.generate(index, "p1", "t1", "string_column", 98, STRING, 1, null));
		assertEquals("fixed_string_column 1234576498          ",
				Auto.generate(index, "p1", "t1", "fixed_string_column", 98, FIXED_STRING, 40, null));
		assertEquals("fixed_string_column 1234576498",
				Auto.generate(index, "p1", "t1", "fixed_string_column", 98, FIXED_STRING, 30, null));
		assertEquals("fixed_stri1234576498",
				Auto.generate(index, "p1", "t1", "fixed_string_column", 98, FIXED_STRING, 20, null));
		assertEquals("nstring_column 1234501298",
				Auto.generate(index, "p1", "t1", "nstring_column", 98, NSTRING, 100, null));
		assertEquals("fixed_nstring_column 1234576398          ",
				Auto.generate(index, "p1", "t1", "fixed_nstring_column", 98, FIXED_NSTRING, 41, null));
		assertEquals("fixed_nstring_column 1234576398",
				Auto.generate(index, "p1", "t1", "fixed_nstring_column", 98, FIXED_NSTRING, 31, null));
		assertEquals("fixed_nstri1234576398",
				Auto.generate(index, "p1", "t1", "fixed_nstring_column", 98, FIXED_NSTRING, 21, null));
		assertEquals("Ynl0ZXNfY29sdW1uIDEyMzQ1MDEzOTg=",
				Auto.generate(index, "p1", "t1", "bytes_column", 98, BYTES, 100, null));
		assertEquals("clob_column 1234501498", Auto.generate(index, "p1", "t1", "clob_column", 98, CLOB, 100, null));
		assertEquals("nclob_column 1234501598", Auto.generate(index, "p1", "t1", "nclob_column", 98, NCLOB, 100, null));
		assertEquals("YmxvYl9jb2x1bW4gMTIzNDUwMTY5OA==",
				Auto.generate(index, "p1", "t1", "blob_column", 98, BLOB, 100, null));
		assertEquals("d41d8cd9-8f00-3204-a980-0998ecf8427e",
				Auto.generate(index, "p1", "t1", "uuid_column", 98, UUID, 0, null));
	}
}
