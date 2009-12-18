/* Copyright (C) 2009 Andrew Semprebon */
package com.droiddice;

import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class DiceSetSelectionGrid extends GridView {

	private Drawable mOutFocusBackground;
	private DiceSetDbAdapter mDbAdapter;

	public static final String TAG = "DiceSetSelectionGrid";
	
    public DiceSetSelectionGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

    /**
     * Set up various UI components
     */
	private void initialize(Context context) {
		mOutFocusBackground = new InsetDrawable(
			context.getResources().getDrawable(R.drawable.felt), 
			context.getResources().getDimensionPixelSize(R.dimen.focus_border_thickness));
        setSelector(context.getResources().getDrawable(R.drawable.felt_selector));
    }
	
	public ShapeDrawable createRoundRect(int radius, int thickness, int colorId) {
		float[] outerR = new float[8];
		Arrays.fill(outerR, radius);
        RectF   inset = new RectF(thickness, thickness, thickness, thickness);
        float[] innerR = new float[8];
        Arrays.fill(innerR, radius - thickness);
        ShapeDrawable rect = new ShapeDrawable(new RoundRectShape(outerR, inset, innerR));
		Paint paint = rect.getPaint();
		paint.setColor(getContext().getResources().getColor(colorId));
		return rect;
	}
	
	/**
	 * Fill in the dice set selection from the database of dice sets 
	 */
    public void fillDiceSetSelection() {
        // Get all of the rows from the database and create the item list
    	Activity activity = ((Activity) getContext());
        Cursor cursor = mDbAdapter.fetchAllDiceSets();
        activity.startManagingCursor(cursor);
        Log.d(TAG, "Found " + cursor.getCount() + " dice sets");
        
        // Now create a simple cursor adapter and set it to display
        DiceSetAdapter adapter = (DiceSetAdapter) getAdapter();
    	if (adapter == null) {
            setAdapter(new DiceSetAdapter(activity, R.layout.dice_set_selection_item, cursor));
    	} else {
    		Cursor oldCursor = adapter.getCursor(); 
    		adapter.changeCursor(cursor);
    		activity.stopManagingCursor(oldCursor);
    		oldCursor.close();
    	}
    }

    public DiceSetDbAdapter getDbAdapter() {
		return mDbAdapter;
	}

	public void setDbAdapter(DiceSetDbAdapter dbAdapter) {
		mDbAdapter = dbAdapter;
	}

	public DiceSet getDiceSetAt(int position) {
		return (DiceSet) ((DiceSetAdapter) getAdapter()).getDiceSet(position);
	}
	
	public int getPositionOfRow(long rowId) {
		DiceSetAdapter adapter = (DiceSetAdapter) getAdapter();
		for (int i = 0; i < adapter.getCount(); ++i) {
			if (rowId == adapter.getItemId(i)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
     * This adapter is for handling the dice set selection grid
     */
	private class DiceSetAdapter extends ResourceCursorAdapter {
		
		public DiceSetAdapter(Context context, int layout, Cursor c) {
			super(context, layout, c);
		}

		@Override
		public void bindView(View view, Context ctx, Cursor cursor) {
        	DiceSet diceSet = DiceSetDbAdapter.buildDiceSetFromCursor(cursor);

        	// set up sub-views
        	TextView  name = (TextView) view.findViewById(R.id.die_set_selection_item_name);
        	name.setText(diceSet.getName());
        	DiceSetView dice = (DiceSetView) view.findViewById(R.id.dice_set_selection_item_dice);
			dice.setDiceSet(diceSet);
			dice.setDisplay(DieView.DISPLAY_BLANK);
			
			// set tag with info needed by on-click handler
			view.setTag(diceSet);
			view.setBackgroundDrawable(mOutFocusBackground);
		}
		
		public DiceSet getDiceSet(int position) {
        	return DiceSetDbAdapter.buildDiceSetFromCursor((Cursor) getItem(position));
		}
	}

}
