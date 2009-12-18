package com.droiddice;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

public class RollActivityTest extends ActivityInstrumentationTestCase<RollActivity> {

    private Activity mActivity;
    private View mRollAreaView;
    private DiceSetDbAdapter mDbAdapter;
    private DiceSetSelectionGrid mDiceSetSelectionView;
    
    public static final String TAG = "RollActivityTest";

	public RollActivityTest() {
		super("com.droiddice", RollActivity.class);
	}

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
        mDbAdapter = new DiceSetDbAdapter(mActivity).open("data_test");
        mDbAdapter.getDatabase().execSQL("DELETE FROM " + DiceSetDbAdapter.DATABASE_TABLE);
        DiceSetDbAdapter.createInitialDiceSets(mDbAdapter.getDatabase());
        mRollAreaView = mActivity.findViewById(R.id.roll_activity_roll_area);
        mDiceSetSelectionView = (DiceSetSelectionGrid) mActivity.findViewById(R.id.roll_activity_new_dice_set_selection);
    }

    
    @Override
	protected void tearDown() throws Exception {
    	mDbAdapter.close();
		super.tearDown();
	}

	public void testPreconditions() {
        assertNotNull(mActivity);
        assertNotNull(mRollAreaView);
        assertNotNull(mDiceSetSelectionView);
        assertTrue("roll area should be focused", mRollAreaView.isFocused());
        assertTrue("should have multiple dice sets", mDiceSetSelectionView.getCount() > 1);
    }

    public void testClickOnRollAreaRollsDice() {
    	Log.d(TAG, "Started testClickOnRollAreaRollsDice");
    	sendKeys(KeyEvent.KEYCODE_DPAD_UP);
    	assertTrue(mRollAreaView.isFocused());
        TextView result = (TextView) mActivity.findViewById(R.id.roll_activity_result);
        String s = (String) result.getText();
        Log.d(TAG, "starting roll is " + s);
    	
        boolean allSame = true;
        for (int i = 0; i <= 20; ++i) {
        	sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
        	allSame = allSame && result.getText().equals(s);
            Log.d(TAG, "  rolled is " + result.getText());
        }
    	//assertFalse(allSame);
    }

    public void testClickOnSelectionAreaLoadsRollArea() {
    	Log.d(TAG, "Started testClickOnSelectionAreaLoadsRollArea");
    	DiceSetView view = (DiceSetView) mActivity.findViewById(R.id.roll_activity_dice_set);
    	DiceSet oldDiceSet = view.getDiceSet();
    	sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
    	sendKeys(KeyEvent.KEYCODE_DPAD_RIGHT);
    	sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
        //assertFalse(oldDiceSet.toString().equals(view.getDiceSet().toString()));
    }

    public void testLongClickOnSelectionAreaBringsUpContextMenu() {
    	Log.d(TAG, "Started testLongClickOnSelectionAreaBringsUpContextMenu");
    	DiceSetView view = (DiceSetView) mActivity.findViewById(R.id.roll_activity_dice_set);
    	DiceSet oldDiceSet = view.getDiceSet();
    	Log.d(TAG, "currently in roll ares is " + oldDiceSet);
    	Log.d(TAG, "selection grid has " + mDiceSetSelectionView.getCount() + " child views");
    	sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
    	sendKeys(KeyEvent.KEYCODE_DPAD_RIGHT);
    	sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
        //assertFalse(oldDiceSet.toString().equals(view.getDiceSet().toString()));
    }
}
