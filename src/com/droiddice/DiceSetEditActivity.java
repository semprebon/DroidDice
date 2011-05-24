/* Copyright (C) 2009 Andrew Semprebon */
package com.droiddice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class DiceSetEditActivity extends Activity {

	/* Bundle keys (passed in extras) */
	public static final String ROW_ID_KEY = "ROW_ID";

	private EditText mNameText;
	DiceSetEditView mDiceSetView;
	GridView mDieSelection;

	private Long mRowId;
	private DiceSetDbAdapter mDbHelper;
	private DiceSet mDiceSet;


	private static final String TAG = "DiceSetEditActivity";

	/* Event handlers */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new DiceSetDbAdapter(this).open();
		setupUI();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mRowId = (Long) extras.get(ROW_ID_KEY);
			Log.i(TAG, "Editing dice set " + mRowId);
		} else if (savedInstanceState != null) {
			mRowId = savedInstanceState.getLong(DiceSetDbAdapter.KEY_ROWID);
		}
		restoreState();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(DiceSetDbAdapter.KEY_ROWID, mRowId);
	}

	@Override
	protected void onStop() {
		mDbHelper.close();
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		restoreState();
	}

	private void setupUI() {
		setContentView(R.layout.dice_set_edit_activity);

		mNameText = (EditText) findViewById(R.id.dice_set_edit_activity_name);
		mDiceSetView = (DiceSetEditView) findViewById(R.id.dice_set_edit_activity_dice_set);
		mDiceSetView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mDiceSet.remove(((DieView) view).getDie());
				mDiceSetView.notifyDataSetChanged();
			}
		});
		
		List<Die> dieTypes = Arrays.asList(DiceSet.DIE_TYPES);
		GridView mDieSelection = (GridView) findViewById(R.id.dice_set_edit_activity_die_selection);
		mDieSelection.setAdapter(new DieTypeAdapter(dieTypes));
		mDieSelection.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Die die = ((DieView) view).getDie();
				mDiceSet.add(die.toString());
				mDiceSetView.notifyDataSetChanged();
			}
		});
	}

	/**
	 * Save the current state of the widgets to the database as the current
	 * record
	 */
	private void saveState() {
		String name = mNameText.getText().toString();
		if (name == null || name.length() == 0) {
			name = mDiceSet.toString();
		}
		Log.d(TAG, "Saving " + mDiceSet.toString());
		mDiceSet.setName(name);
		mDbHelper.updateDiceSet(mRowId, mDiceSet);
	}

	/**
	 * Restore the state of the widgets from the database
	 */
	private void restoreState() {
		mDiceSet = mDbHelper.fetchDiceSet(mRowId);
		mDiceSetView.setDiceSet(mDiceSet);
		if (!mDiceSet.getName().equals(mDiceSet.toString())) {
			mNameText.setText(mDiceSet.getName());
		} else {
			mNameText.setText("");
		}
	}

	/**
	 * This adapter is for handling the dice set selection grid
	 */
	private class DieTypeAdapter extends ArrayAdapter<Die> {

		public DieTypeAdapter(List<Die> list) {
			super(DiceSetEditActivity.this, R.id.die_view_large, list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			DieView view = (DieView) convertView;
			if (view == null) {
				view = new DieView(DiceSetEditActivity.this);
				view.setDisplay(DieView.DISPLAY_TYPE);
				view.setPreferredSize(60);
			}
			view.setDie(getItem(position));
			return view;
		}

	}

}
