/* Copyright (C) 2009 Andrew Semprebon */
package com.droiddice;

public class AdjustmentDie implements Die {

	private int mValue = 0;
	
	public AdjustmentDie(int value) {
		mValue = value;
	}
	
	public int getMin() {
		return mValue;
	}
	
	public int getMax() {
		return mValue;
	}

	public int getValue() {
		return mValue;
	}
	
	public void setValue(int value) {
		mValue = value;
	}
	
	public String getDisplayValue() {
		return toString();
	}
	
	public int roll() {
		return mValue;
	}
	
	public String toString() {
		if (mValue < 0) {
			return Integer.toString(mValue);
		} else {
			return "+" + Integer.toString(mValue);
		}
	}

	public boolean sameType(Die other) {
		if (!(other instanceof AdjustmentDie)) {
			return false;
		} else {
			return true;
		}
	}

}
