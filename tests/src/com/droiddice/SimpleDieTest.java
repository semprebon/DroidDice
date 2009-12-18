package com.droiddice;

import java.util.HashMap;

import junit.framework.TestCase;

import com.droiddice.Die;

public class SimpleDieTest extends TestCase {
	
	public void testRollOfD6ShouldReturnResultsBetween1And6() {
		Die d = new SimpleDie(6);
		checkDiceRollsForRangeAndDistribution(d, 1, 6);
	}

	public void testRollOfD8ShouldReturnResultsBetween1And8() {
		Die d = new SimpleDie(8);
		checkDiceRollsForRangeAndDistribution(d, 1, 8);
	}
	
	private void count(HashMap<Object, Integer> map, Object obj) {
		map.put(obj, getCount(map, obj) + 1);
	}

	private Integer getCount(HashMap<Object, Integer> map, Object obj) {
		Integer count = map.get(obj);
		if (count == null) {
			count = 0;
		}
		return count;
	}
	
	
	private void checkDiceRollsForRangeAndDistribution(Die d, int min, int max) {
		HashMap<Object, Integer> rolls = new HashMap<Object, Integer>();
		for (int i = 0; i < 1000; ++i) {
			int roll = d.roll();
			assertTrue("total " + roll + " must be between " + min + " and " + max, 
					min <= roll && roll <= max);
			count(rolls, roll);
		}
		for (int i = min; i <= max; ++i) {
			assertTrue(getCount(rolls, i) > 0);
		}
	}
	

}
