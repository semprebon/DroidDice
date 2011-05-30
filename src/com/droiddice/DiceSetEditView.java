/* Copyright (C) 2009-2011 Andrew Semprebon */
package com.droiddice;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

public class DiceSetEditView extends GridView {

	private static final DiceSet DEFAULT_DICE_SET = new DiceSet("d6");
	DiceSet mDiceSet = DEFAULT_DICE_SET;
	Long mRowId;
	int mTextColor;
	float mTextSize = 16;
	boolean mShowingValue = false;
	int mDieSize;
	
	DieView mSelectDieView;

	public static final String TAG = "DiceSetEditView";

	/* Constructors */

	//TODO: probably should allow adapter to be reused 
	public DiceSetEditView(Context context) {
		super(context);
		mTextColor = context.getResources().getColor(R.color.text_dark);
		setAdapter(new DiceSetAdapter(context, mDiceSet));
	}

	public DiceSetEditView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DiceSetEditView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mTextColor = context.getResources().getColor(R.color.text_dark);
		setAdapter(new DiceSetAdapter(context, mDiceSet));
	}

	/* Properties */

	public DiceSet getDiceSet() {
		return mDiceSet;
	}

	public void setDiceSet(DiceSet diceSet) {
		mDiceSet = diceSet;
		setAdapter(new DiceSetAdapter(getContext(), mDiceSet));
		setNumColumns(columnsForCount(mDiceSet.getCount()));
	}

	public Long getRowId() {
		return mRowId;
	}

	public void setRowId(Long rowId) {
		mRowId = rowId;
	}
	
	public int getTextColor() {
		return mTextColor;
	}
	
	public void setTextColor(int color) {
		mTextColor = color;
	}
	
	public float getTextSize() {
		return mTextSize;
	}

	public void setTextSize(float textSize) {
		mTextSize = textSize;
	}

	public boolean isShowingValue() {
		return mShowingValue;
	}

	public void setShowingValue(boolean showingValue) {
		mShowingValue = showingValue;
	}
	
	private int columnsForCount(int count) {
		return (count <= 10) ? 5 : 10;
	}
	
	private int dieSizeForCount(int count) {
		return (count <= 10) ? DieView.SIZE_LARGE : DieView.SIZE_MEDIUM;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (changed && mDiceSet.getCount() != 0) {
			DiceArranger dieArranger = new DiceArranger(mDiceSet.getCount(), right - left, bottom - top);
			mDieSize = dieArranger.getDieSize();
		}
	}
	
	public void notifyDataSetChanged() {
		if (mDiceSet.getCount() != 0) {
			setNumColumns(columnsForCount(mDiceSet.getCount()));
		}
		((DiceSetAdapter) getAdapter()).notifyDataSetChanged();
	}
	
    /**
     * This adapter is for handling the dice set selection grid
     */
	private class DiceSetAdapter extends ArrayAdapter<Die> {
		
		public DiceSetAdapter(Context context, DiceSet diceSet) {
        	super(context, R.id.dice_set_edit_activity_die_selection, diceSet.asList());
        }
        
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			DieView view = (DieView) convertView;;
			if (view == null) {
				view = new DieView(parent.getContext());
				view.setDisplay(mShowingValue ? DieView.DISPLAY_VALUE : DieView.DISPLAY_BLANK);
			} 
			view.setDie(getItem(position));
			view.setPreferredSize(dieSizeForCount(getCount()));
			view.setDisplay(DieView.DISPLAY_TYPE);
			return view;
		}
	}	
}
