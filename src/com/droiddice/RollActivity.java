/*package com.droiddice;

import android.app.Activity;
import android.os.Bundle;

public class RollActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
/* Copyright (C) 2009 Andrew Semprebon */
package com.droiddice;

//TODO: Implement combine (for percentile)
//TODO: Implement min/max
//TODO: Implement count over/under/equal
//TODO: Auto-increment releases
//TODO: Help (web-based)
//TODO: Dice skinning
//TODO: Custom die types
//TODO: Use better random number generator (Mersenne Twister)
//TODO: Roll different sets of dice simultaneously
//TODO: Allow dice sets to be rearranged
//TODO: Support savage dice
//TODO: Support Fudge dice
//TODO: Optionally (as set option) allow reroll of individual die
//TODO: retain set values (?)

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class RollActivity extends Activity {

	/* UI Widgets */
	private View mActiveDiceSetView;
	private DiceSetSelectionGrid mDiceSetSelection;
	private RollAreaView mRollArea;
	private DiceSetDbAdapter mDbAdapter;
	private Long mRowId;
	private int mPosition;
	private Long mContextMenuRowId;

	private SensorManager mSensorManager;
	private VersionChecker mVersionChecker; 
	
	private boolean useAccelerometer = true;
	protected boolean moveMode = false;
	private int storedPosition; // for moving diceSets
	private ArrayList<String> history; 

	/* Other activities */
	private static final int ACTIVITY_EDIT = 0;

	/* Menu Items */
	private static final int INSERT_ID = Menu.FIRST;
	private static final int SETTINGS_ID = Menu.FIRST + 1;
	private static final int HISTORY_ID = Menu.FIRST + 2;
	private static final int MULTIROLL_ID = Menu.FIRST + 3;
	private static final int EDIT_ID = Menu.FIRST + 4;
	private static final int DELETE_ID = Menu.FIRST + 5;
	private static final int MOVE_ID = Menu.FIRST + 6;
	private static final int CANCELMOVE_ID = Menu.FIRST + 7;

	private static final String TAG = "DiceRoller";
	private Animation mActivationAnimation;

	/* Event Handlers */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Starting RollActivity");
		if (savedInstanceState != null) {
			useAccelerometer = savedInstanceState.getBoolean("useAccelerometer", useAccelerometer);
			history = savedInstanceState.getStringArrayList("history");
		}
		if ( history == null ) {
			history = new ArrayList<String>();
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		mDbAdapter = new DiceSetDbAdapter(this).open();
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		setupUI();
		reloadDiceSetsAndActivate(0);
		mVersionChecker = new VersionChecker();
		mVersionChecker.checkForNewVersion(this);
	}
	
	@Override
	public void onSaveInstanceState ( Bundle instanceState ) {
		if ( instanceState != null ) {
			instanceState.putBoolean("useAccelerometer", useAccelerometer);
			instanceState.putStringArrayList("history", history);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.add_menu_text).setIcon(
				android.R.drawable.ic_menu_edit);
		menu.add(0, HISTORY_ID, 0, R.string.history_menu_text).setIcon(
				android.R.drawable.ic_menu_recent_history);
		menu.add(0, SETTINGS_ID, 0, R.string.settings_menu_text).setIcon(
				android.R.drawable.ic_menu_preferences);
		menu.add(0, MULTIROLL_ID, 0, R.string.multiroll_menu_text).setIcon(
				android.R.drawable.ic_menu_manage);
		menu.add(0, CANCELMOVE_ID, 0, R.string.cancelmove_menu_text).setIcon(
				android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu( Menu menu ) {
		super.onPrepareOptionsMenu(menu);

		if (!moveMode) {
			MenuTool.showMultipleMenuItems(menu, new int [] {
					INSERT_ID, HISTORY_ID, SETTINGS_ID, MULTIROLL_ID
			});
			MenuTool.hideMultipleMenuItems(menu, new int [] {
					CANCELMOVE_ID
			});
		} else {
			MenuTool.showMultipleMenuItems(menu, new int [] {
					CANCELMOVE_ID
			});
			MenuTool.hideMultipleMenuItems(menu, new int [] {
					INSERT_ID, HISTORY_ID, SETTINGS_ID, MULTIROLL_ID
			});
		
		}
		MenuItem mi = menu.findItem(SETTINGS_ID);
		mi.setTitle(getResources().getString(R.string.settings_menu_text) + " " + (useAccelerometer?"OFF":"ON") );
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:
			mRowId = mDbAdapter.createDiceSet(DiceSet.DEFAULT_DICE_SET);
			editDiceSet(mRowId);
			return true;
		case SETTINGS_ID:
			toggleUseAccelerometer();
			return true;
		case CANCELMOVE_ID:
			moveMode = false;
			return true;
		case HISTORY_ID:
			showHistory();
			return true;
		}

		return false;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo adapterMenuInfo = (AdapterContextMenuInfo) menuInfo;
		mContextMenuRowId = adapterMenuInfo.id;
		DiceSet diceSet = ((DiceSetSelectionGrid) view)
				.getDiceSetAt(adapterMenuInfo.position);
		menu.add(0, EDIT_ID, 0, getString(R.string.edit_menu_text) + " "
				+ diceSet.getName());
		menu.add(0, DELETE_ID, 0, getString(R.string.delete_menu_text) + " "
				+ diceSet.getName());
		super.onCreateContextMenu(menu, view, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case EDIT_ID:
			editDiceSet(mContextMenuRowId);
			return true;
		case DELETE_ID:
			deleteDiceSet(mContextMenuRowId);
			return true;
		case MOVE_ID:
			moveMode = true;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		reloadDiceSetsAndActivate(mPosition);
	}

	@Override
	protected void onDestroy() {
		mDbAdapter.close();
		super.onDestroy();
	}

	/**
	 * Set up various UI components
	 */
	private void setupUI() {
		setContentView(R.layout.roll_activity);
		setUpSelectionGrid();
		mRollArea = (RollAreaView) findViewById(R.id.roll_activity_roll_area);
		mRollArea.setUp(this, mDiceSetSelection);
	}

	private void setUpSelectionGrid() {
		mActivationAnimation = AnimationUtils.loadAnimation(this,
				R.anim.flip_to_top);
		mDiceSetSelection = (DiceSetSelectionGrid) findViewById(R.id.roll_activity_new_dice_set_selection);
		mDiceSetSelection.setFocusableInTouchMode(true);
		mDiceSetSelection.setDbAdapter(mDbAdapter);
		mDiceSetSelection.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d(TAG, "onItemClick called " + view.getTouchables().size());
				activateDiceSet(position, view);
			}
		});
		registerForContextMenu(mDiceSetSelection);
	}

	/**
	 * Go to Dice Set Edit page to create a new dice set
	 */
	private void editDiceSet(Long rowId) {
		Intent intent = new Intent(this, DiceSetEditActivity.class);
		intent.putExtra(DiceSetEditActivity.ROW_ID_KEY, rowId);
		startActivityForResult(intent, ACTIVITY_EDIT);
	}

	private void deleteDiceSet(Long rowId) {
		if (rowId != null) {
			Log.i(TAG, "Deleting dice set " + rowId);
			int position = mDiceSetSelection.getPositionOfRow(rowId);
			if (position == -1) {
				position = 0;
			}
			mDbAdapter.deleteDiceSet(rowId);
			reloadDiceSetsAndActivate(position);
		}
	}

	/**
	 * Make the specified dice set active
	 * 
	 * @param position
	 */
	protected void activateDiceSet(int position, View view) {
		if ( moveMode ) {
			moveMode = false;
			return;
		}
		
		mPosition = position;

		mRowId = mDiceSetSelection.getItemIdAtPosition(position);
		Cursor cursor = (Cursor) mDiceSetSelection.getItemAtPosition(position);
		DiceSet diceSet = (DiceSet) DiceSetDbAdapter
				.buildDiceSetFromCursor(cursor);
		Log.w(TAG, "Activated dice set " + diceSet.getName() + " (row "
				+ mRowId + ")");

		if (view != null) {
			// show activation in dice set selection
			if (mActiveDiceSetView != null) {
				toggleActive(mActiveDiceSetView, false);
			}
			mActiveDiceSetView = view;
			toggleActive(mActiveDiceSetView, true);
		}
		mRollArea.setUpRollAreaForDiceSet(diceSet);
		history.add(0, diceSet.getName() + ": " + diceSet.getDisplayResult());
	}
	
	private void moveDiceSet ( int oldPosition, int newPosition ) {
		mDbAdapter.moveDiceSet(oldPosition, newPosition);
	}

	private void showHistory () {
		
		Intent intent = new Intent(this, HistoryActivity.class);
		intent.putStringArrayListExtra("history", history);
		//intent.put
		startActivityForResult(intent, ACTIVITY_EDIT);
	}
	
	/**
	 * Let user know a particular dice set in the selection grid has been
	 * activated
	 * 
	 * @param view
	 * @param isActive
	 */
	private void toggleActive(View view, boolean isActive) {
		if (isActive) {
			view.startAnimation(mActivationAnimation);
		}
	}

	private void reloadDiceSetsAndActivate(int position) {
		mDiceSetSelection.fillDiceSetSelection();
		// activate dice set at position or first if not available
		if (mDiceSetSelection.getCount() == 0) {
			mRowId = null;
			mPosition = 0;
			mRollArea.setUpRollAreaForDiceSet(DiceSet.DEFAULT_DICE_SET);
		} else {
			if (position >= mDiceSetSelection.getCount()) {
				position = 0;
			}
			activateDiceSet(position, null);
		}
	}
	
	private void toggleUseAccelerometer () {
		if ( useAccelerometer ) {
			useAccelerometer = false;
			mSensorManager.unregisterListener(mRollArea);
		} else {
			useAccelerometer = true;
        	mSensorManager.registerListener(mRollArea,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_GAME);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if ( useAccelerometer ) {
        	mSensorManager.registerListener(mRollArea,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
		}
	}

	@Override
	protected void onStop() {
		mSensorManager.unregisterListener(mRollArea);
		super.onStop();
	}

}

