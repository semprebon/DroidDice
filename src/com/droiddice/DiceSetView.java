/* Copyright (C) 2009-2011 Andrew Semprebon */
package com.droiddice;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class DiceSetView extends LinearLayout {

	private static final DiceSet DEFAULT_DICE_SET = new DiceSet("d6");
	DiceSet mDiceSet = DEFAULT_DICE_SET;
	Long mRowId;
	int mDisplay = DieView.DISPLAY_BLANK;
	DieView mDieViewModel;
	AttributeSet mDieAttributes;
	
	DieView mSelectDieView;

	public static final String TAG = "DiceSetView";

	/* Constructors */

	public DiceSetView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDiceSet(DEFAULT_DICE_SET);
		mDieAttributes = attrs;
	}

	/* Properties */

	public DiceSet getDiceSet() {
		return mDiceSet;
	}

	public void setDiceSet(DiceSet diceSet) {
		mDiceSet = diceSet;
		removeAllViews();
		for (Die die : diceSet.asList()) {
			DieView view = new DieView(getContext());
			view.setDie(die);
			view.setDisplay(mDisplay);
			addView(view);
		}
	}

	public Long getRowId() {
		return mRowId;
	}

	public void setRowId(Long rowId) {
		mRowId = rowId;
	}
	
	public int getDisplay() {
		return mDisplay;
	}

	public void setDisplay(int display) {
		mDisplay = display;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		int width = (right - left) - (getPaddingLeft() + getPaddingRight());
		int height = (bottom - top) - (getPaddingTop() + getPaddingBottom());

		DiceArranger arranger = new DiceArranger(mDiceSet.getCount(), width, height);
		int dieSize = arranger.getDieSize();
		for (int i = 0; i < getChildCount(); ++i) {
			View view = getChildAt(i);
			int x = arranger.getX(i) + getPaddingLeft();
			int y = arranger.getY(i) + getPaddingTop();
			view.layout(x, y, x+dieSize, y+dieSize);
		}
	}

	
}
