/* Copyright (C) 2009-2011 Andrew Semprebon */
package com.droiddice;

public interface Die {
	int getMin();
	int getMax();
	int getValue();
	int roll();
	String toString();
	String getDisplayValue();
	boolean sameType(Die other);
}
