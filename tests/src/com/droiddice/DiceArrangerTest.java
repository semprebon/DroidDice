package com.droiddice;

import com.droiddice.DiceArranger;
import com.droiddice.DieView;

import junit.framework.TestCase;

public class DiceArrangerTest extends TestCase {

	public void testDieSizeShouldBeLargeIfSpaceBigEnough() {
		assertEquals(DieView.SIZE_LARGE, (new DiceArranger(1, 70, 70)).getDieSize());
	}

	public void testDieSizeShouldBeMediumIfSpaceTooSmallForLarge() {
		assertEquals(DieView.SIZE_MEDIUM, (new DiceArranger(1, 50, 70)).getDieSize());
	}

	public void testDieSizeShouldBeSmallIfSpaceTooSmallForMedium() {
		assertEquals(DieView.SIZE_SMALL, (new DiceArranger(1, 19, 70)).getDieSize());
	}

	public void testShouldSuggestSize20DiceIfSpaceNotBigEnough() {
		assertEquals(DieView.SIZE_SMALL, (new DiceArranger(1, 19, 70)).getDieSize());
	}
}
