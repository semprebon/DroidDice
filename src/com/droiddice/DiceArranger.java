/* Copyright (C) 2009 Andrew Semprebon */
package com.droiddice;

import android.util.Log;

public class DiceArranger {

	int mColumns;
	int mRows;
	int mDieSize;
	int mWidth;
	int mHeight;
	int mNumDice;
	int mXOffset;
	int mYOffset;
	
	private static final String TAG = "DiceArranger";

	public DiceArranger(int numDice, int width, int height) {
		width = (width == 0) ? 320 : width;
		height = (height == 0) ? 80 : height;
		mWidth = width;
		mHeight = height;
		mNumDice = numDice;

		mDieSize = chooseDieSize(width, height, numDice);
		mColumns = width / mDieSize;
		mRows = divUp(numDice, mColumns);
		mXOffset = (width - mColumns * mDieSize) / 2;
		if (numDice < mColumns) {
			mXOffset = (width - numDice * mDieSize) / 2;
		} else {
			mXOffset = (width - mColumns * mDieSize) / 2;
		}
		mYOffset = (height - mRows * mDieSize) / 2;
	}

	private int chooseDieSize(int width, int height, int num) {
		for (int size : DieView.DIE_SIZES) {
			int maxNum = (height / size) * (width / size);
			if (num <= maxNum) return size;
		}
		return DieView.MIN_DIE_SIZE;
	}
	
	/**
	 * Return the result of dividing n by d, rounded up
	 */
	private int divUp(int n, int d) {
		return (n + d - 1) / d;
	}
	
	public int getColumns() {
		return mColumns;
	}
	
	public int getRows() {
		return mRows;
	}
	
	public int getDieSize() {
		return mDieSize;
	}
	
	public int getX(int pos) {
		int column = pos % mColumns;
		return mXOffset + mDieSize * column;
	}

	public int getY(int pos) {
		int row = pos / mColumns;
		return mYOffset + mDieSize * row;
	}
}
