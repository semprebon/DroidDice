package com.droiddice;

import java.util.Arrays; 
import com.droiddice.DiceSet;
import junit.framework.TestCase;

public class DiceSetTest extends TestCase {

	public void testConstructorCanCreateDiceSetFromTextString() {
		DiceSet d = new DiceSet("2d4+d6");
		assertEquals(3, d.getCount());
		assertEquals(4, d.sizeOf(0));
		assertEquals(4, d.sizeOf(1)); 
		assertEquals(6, d.sizeOf(2));
	}
	
	public void testConstructorCanCreateDiceSetFromEmptyTextString() {
		DiceSet d = new DiceSet("");
		assertEquals(0, d.getCount());
	}
	
	public void testConstructorShouldCreateBonus() {
		DiceSet d = new DiceSet("d6+10");
		assertEquals(1, d.sizeOf(1));
		assertEquals(10, d.valueOf(1));
		assertEquals(AdjustmentDie.class, d.asList().get(1).getClass());
	}
	
	public void testConstructorShouldCreatePenalty() {
		DiceSet d = new DiceSet("d6-10");
		assertEquals(1, d.sizeOf(1));
		assertEquals(-10, d.valueOf(1));
		assertEquals(AdjustmentDie.class, d.asList().get(1).getClass());
	}

	public void testConstructorShouldCreatePenaltyOnly() {
		DiceSet d = new DiceSet("-10");
		assertEquals(-10, d.valueOf(0));
		assertEquals(AdjustmentDie.class, d.asList().get(0).getClass());
	}

	public void testRollOfD6ShouldReturnResultsBetween1And6() {
		DiceSet d = new DiceSet("d6");
		checkDiceRollsForRangeAndDistribution(d, 1, 6);
	}

	public void testRollOfD8ShouldReturnResultsBetween1And8() {
		DiceSet d = new DiceSet("d8");
		checkDiceRollsForRangeAndDistribution(d, 1, 8);
	}
	
	private void checkDiceRollsForRangeAndDistribution(DiceSet d, int min, int max) {
		int range = max + 1 - min;
		int[] rolls = new int[range];
		Arrays.fill(rolls, 0);
		for (int i = 0; i < 1000; ++i) {
			d.roll();
			assertTrue("total " + d.getResult() + " must be between " + min + " and " + max, 
					min <= d.getResult() && d.getResult() <= max);
			rolls[d.getResult() - min]++;
		}
		for (int i = 0; i < range; ++i) {
			assertTrue(rolls[i] > 0);
		}
	}
	
	public void testCountReturnsNumberOfDice() {
		DiceSet d = new DiceSet("d3+d4+d5");
		assertEquals(3, d.getCount());
	}
	
	public void testSizeShouldReturnSizeOfSpecifiedDie() {
		DiceSet d = new DiceSet("d3+d4+d5");
		assertEquals(3, d.sizeOf(0));
		assertEquals(4, d.sizeOf(1));
		assertEquals(5, d.sizeOf(2));
	}

	public void testToStringShouldBeD6ForSingeSixSided() {
		assertEquals("d6", (new DiceSet("d6")).toString());
	}

	public void testToStringShouldBe3D6ForThreeSixSideds() {
		assertEquals("3d6", (new DiceSet("3d6")).toString());
	}

	public void testToStringShouldBeEmptyForNoDice() {
		assertEquals("", (new DiceSet("")).toString());
	}

	public void testToStringShouldHandleBonus() {
		assertEquals("d6+4", (new DiceSet("d6+4")).toString());
	}

	public void testToStringShouldHandlePenalty() {
		assertEquals("d6-4", (new DiceSet("d6-4")).toString());
	}

	public void testNameShouldDefaultToString() {
		assertEquals("2d4+d6", (new DiceSet("2d4+d6")).getName());
	}
	
	public void testCountOfShouldGetNumberOfDiceOfGivenType() {
		assertEquals(2, (new DiceSet("2d4+d6")).countOf("d4"));
		assertEquals(0, (new DiceSet("2d4+d6")).countOf("d8"));
	}
	
	public void testAddShouldResultInOneMoreDice() {
		DiceSet diceSet = new DiceSet("1d6");
		diceSet.add("d6");
		assertEquals(2, diceSet.countOf("d6"));
	}
	
	public void testAddAdjustmentWhenSetHasAdjustmentShouldCombineAdjustments() {
		DiceSet diceSet = new DiceSet("1d6+2");
		diceSet.add("3");
		assertEquals(2, diceSet.getCount());
		assertEquals(5, diceSet.valueOf(1));
	}
}
