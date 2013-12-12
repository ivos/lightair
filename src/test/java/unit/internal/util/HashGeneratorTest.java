package unit.internal.util;

import static org.junit.Assert.*;
import net.sf.lightair.internal.util.HashGenerator;

import org.junit.Test;

public class HashGeneratorTest {

	HashGenerator g = new HashGenerator();

	@Test
	public void generateHash_3() {
		assertEquals(196, g.generateHash("source1", 3));
		assertEquals(100, g.generateHash("source2", 3));
		assertEquals(231, g.generateHash("source3", 3)); // -231
		assertEquals(728, g.generateHash("source4", 3));
		assertEquals(240, g.generateHash("source5", 3));
		assertEquals(738, g.generateHash("source54321", 3));
		assertEquals(70, g.generateHash("source54322", 3));
		assertEquals(855, g.generateHash("tource54322", 3));
		assertEquals(421, g.generateHash("a", 3));
		assertEquals(241, g.generateHash("b", 3));
		assertEquals(464, g.generateHash("c", 3));
		assertEquals(276, g.generateHash("", 3));
	}

	@Test
	public void generateHash_2() {
		assertEquals(96, g.generateHash("source1", 2));
		assertEquals(0, g.generateHash("source2", 2));
		assertEquals(31, g.generateHash("source3", 2)); // -31
		assertEquals(28, g.generateHash("source4", 2));
		assertEquals(40, g.generateHash("source5", 2));
		assertEquals(38, g.generateHash("source54321", 2));
		assertEquals(70, g.generateHash("source54322", 2));
		assertEquals(55, g.generateHash("tource54322", 2));
		assertEquals(21, g.generateHash("a", 2));
		assertEquals(41, g.generateHash("b", 2));
		assertEquals(64, g.generateHash("c", 2));
		assertEquals(76, g.generateHash("", 2));
	}

	@Test
	public void generateHash_1() {
		try {
			g.generateHash("a", 1);
			fail("Should throw");
		} catch (IllegalArgumentException e) {
			assertEquals("Digits must be from 2 to 3, but was 1",
					e.getMessage());
		}
	}

	@Test
	public void generateHash_4() {
		try {
			g.generateHash("a", 4);
			fail("Should throw");
		} catch (IllegalArgumentException e) {
			assertEquals("Digits must be from 2 to 3, but was 4",
					e.getMessage());
		}
	}

}
