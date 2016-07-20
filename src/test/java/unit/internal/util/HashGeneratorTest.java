package unit.internal.util;

import net.sf.lightair.internal.util.HashGenerator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

	@Test
	public void distribution() {
		Map<Integer, Integer> counts = new HashMap<>();
		for (char ch1 = 'a'; ch1 < 'z'; ch1++) {
			for (char ch2 = 'a'; ch2 < 'z'; ch2++) {
				for (char ch3 = 'a'; ch3 < 'z'; ch3++) {
					for (char ch4 = 'a'; ch4 < 'z'; ch4++) {
						String source = "" + ch1 + ch2 + ch3 + ch4;
						final int hash = g.generateHash(source, 3);
						Integer count = counts.get(hash);
						if (null == count) {
							count = 1;
						} else {
							count++;
						}
						counts.put(hash, count);
					}
				}
			}
		}
		List<Integer> distribution = new ArrayList<>(counts.values());
		Integer max = Collections.max(distribution);
		Integer min = Collections.min(distribution);
		assertTrue("Max should be < 500, but was " + max, max < 500);
		assertTrue("Min should be > 250, but was " + min, min > 250);
	}

	// @Test
	public void generateTable() {
		for (char ch1 = 'a'; ch1 < 'z'; ch1++) {
			for (char ch2 = 'a'; ch2 < 'z'; ch2++) {
				for (char ch3 = 'a'; ch3 < 'z'; ch3++) {
					for (char ch4 = 'a'; ch4 < 'z'; ch4++) {
						final String source = "" + ch1 + ch2 + ch3 + ch4;
						final int hash = g.generateHash(source, 3);
						if (hash == 998) {
							System.out.println(source);
						}
					}
				}
			}
		}
	}

	// @Test
	public void generateColumn() {
		for (char ch1 = 'a'; ch1 < 'z'; ch1++) {
			for (char ch2 = 'a'; ch2 < 'z'; ch2++) {
				final String source = "" + ch1 + ch2;
				final int hash = g.generateHash(source, 2);
				if (hash == 98) {
					System.out.println(source);
				}
			}
		}
	}

}
