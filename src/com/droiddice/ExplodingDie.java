/* Copyright (C) 2009-2011 Andrew Semprebon */
package com.droiddice;

public class ExplodingDie  implements Die {

	private Integer mSides;
	private Integer mValue = 1;
	
	/* Constructors */
	
	public ExplodingDie() {
		this(6);
	}
	
	public ExplodingDie(int sides) {
		mSides = sides;
	}
	
	/* Properties */
	
	public int getSides() {
		return mSides;
	}
	
	public void setSides(int sides) {
		mSides = sides;
	}

	public int getMin() {
		return 1;
	}
	
	public int getMax() {
		return getSides();
	}

	public int getValue() {
		return mValue;
	}
	
	public String getDisplayValue() {
		return mValue.toString();
	}
	
	public int roll() {
		mValue = 0;
		int currentRoll = simpleRoll();
		while (currentRoll == getSides()) {
			mValue += getSides(); 
		}
		mValue += currentRoll;
		return mValue;
	}
	
	public String toString() {
		return "x" + mSides;
	}
	
	public boolean sameType(Die other) {
		if (!(other instanceof ExplodingDie)) {
			return false;
		}
		return mSides.equals(((ExplodingDie) other).getSides()); 
	}
	
	private int simpleRoll() {
		return getMin() + (int) Math.round(Math.random() * getSides() - 0.5);
	}
}
