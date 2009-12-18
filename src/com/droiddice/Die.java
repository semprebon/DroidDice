package com.droiddice;

interface Die {
	int getMin();
	int getMax();
	int getValue();
	int roll();
	String toString();
	String getDisplayValue();
	boolean sameType(Die other);
}
