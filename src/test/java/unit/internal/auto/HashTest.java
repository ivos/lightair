package unit.internal.auto;

import net.sf.lightair.internal.auto.Hash;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class HashTest {

	@Test
	public void generateHash_4() {
		assertEquals(2196, Hash.generate("source1", 4));
		assertEquals(100, Hash.generate("source2", 4));
		assertEquals(5231, Hash.generate("source3", 4));
		assertEquals(5728, Hash.generate("source4", 4));
		assertEquals(240, Hash.generate("source5", 4));
		assertEquals(1738, Hash.generate("source54321", 4));
		assertEquals(4070, Hash.generate("source54322", 4));
		assertEquals(1855, Hash.generate("tource54322", 4));
		assertEquals(421, Hash.generate("a", 4));
		assertEquals(4241, Hash.generate("b", 4));
		assertEquals(9464, Hash.generate("c", 4));
		assertEquals(6276, Hash.generate("", 4));
	}

	@Test
	public void generateHash_3() {
		assertEquals(196, Hash.generate("source1", 3));
		assertEquals(100, Hash.generate("source2", 3));
		assertEquals(231, Hash.generate("source3", 3));
		assertEquals(728, Hash.generate("source4", 3));
		assertEquals(240, Hash.generate("source5", 3));
		assertEquals(738, Hash.generate("source54321", 3));
		assertEquals(70, Hash.generate("source54322", 3));
		assertEquals(855, Hash.generate("tource54322", 3));
		assertEquals(421, Hash.generate("a", 3));
		assertEquals(241, Hash.generate("b", 3));
		assertEquals(464, Hash.generate("c", 3));
		assertEquals(276, Hash.generate("", 3));
	}

	@Test
	public void generateHash_5() {
		try {
			Hash.generate("a", 5);
			fail("Should throw");
		} catch (IllegalArgumentException e) {
			assertEquals("Maximum supported digits is 4, but got 5", e.getMessage());
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
						int hash = Hash.generate(source, 3);
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
		Map<Integer, Integer> sorted = new LinkedHashMap<>();
		counts.entrySet().stream()
				.sorted(Map.Entry.comparingByValue())
				.forEachOrdered(entry -> sorted.put(entry.getKey(), entry.getValue()));
//		System.out.println(sorted);
		ArrayList<Integer> values = new ArrayList<>(sorted.values());
		Integer min = values.get(0);
		Integer max = values.get(values.size() - 1);
		assertTrue("Max should be between 300 and 500, but was " + max, max > 300 && max < 500);
		assertTrue("Min should be between 250 and 400, but was " + min, min > 250 && min < 400);
	}
}
