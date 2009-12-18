/* Copyright (C) 2009 Andrew Semprebon */
package com.droiddice;

public class SimpleDie  implements Die {

	private Integer mSides;
	private Integer mValue = 1;
	
	/* Constructors */
	
	public SimpleDie() {
		this(6);
	}
	
	public SimpleDie(int sides) {
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
		mValue = getMin() + (int) Math.round(Math.random() * getSides() - 0.5);
		return mValue;
	}
	
	public String toString() {
		return "d" + mSides;
	}
	
	public boolean sameType(Die other) {
		if (!(other instanceof SimpleDie)) {
			return false;
		}
		return mSides.equals(((SimpleDie) other).getSides()); 
	}
}
