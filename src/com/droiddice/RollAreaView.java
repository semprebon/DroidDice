/* Copyright (C) 2009-2011 Andrew Semprebon */
package com.droiddice;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RollAreaView extends LinearLayout implements SensorEventListener {

	private Animation mRollAnimation;
	private DiceSetView mCurrentDiceSetView;
	private DiceSet mDiceSet;
	private TextView mResultText;
	private TextView mNameText;
	private float[] mLastAcceleration;
	
	private static final float SHAKE_THRESHOLD = 2.0F;

	public static final String TAG = "RollAreaView";
	
	public RollAreaView(Context context, AttributeSet attrs) {
		super(context, attrs);
        mLastAcceleration  = new float[] { 0.0F, 0.0F, 0.0F };
	}

	public void setUp(Activity activity, DiceSetSelectionGrid mDiceSetSelection) {
    	mRollAnimation = AnimationUtils.loadAnimation(activity, R.anim.roll);
        setFocusable(true);
        setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				roll(true);
			}
        });
		mCurrentDiceSetView = (DiceSetView) activity.findViewById(R.id.roll_activity_dice_set);
    	mResultText = (TextView) activity.findViewById(R.id.roll_activity_result);
		mNameText = (TextView) activity.findViewById(R.id.roll_activity_name);

		final Drawable outFocusBackground = new InsetDrawable(
				activity.getResources().getDrawable(R.drawable.felt), 
				activity.getResources().getDimensionPixelSize(R.dimen.focus_border_thickness));
        final Drawable inFocusBackground = new LayerDrawable(new Drawable[] {
          	activity.getResources().getDrawable(R.drawable.felt_selector),
       		outFocusBackground,
       	});

    	setBackgroundDrawable(outFocusBackground);
        
        setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					v.setBackgroundDrawable(inFocusBackground);
				} else {
					v.setBackgroundDrawable(outFocusBackground);
				}
				v.invalidate();
			}
        });
		mCurrentDiceSetView = (DiceSetView) findViewById(R.id.roll_activity_dice_set);
		mCurrentDiceSetView.setDisplay(DieView.DISPLAY_VALUE);
	}

	
	public DiceSet getDiceSet() {
		return mDiceSet;
	}

	public void setDiceSet(DiceSet diceSet) {
		mDiceSet = diceSet;
	}

	/**
     * Roll the currently active dice set and update UI
     */
    public void roll(boolean animate) {
		mDiceSet.roll();
		Log.d(TAG, "rolled " + mDiceSet.getResult());
		mCurrentDiceSetView.invalidate();
		mResultText.setText(Integer.toString(mDiceSet.getResult()));
		if (animate) {
			mCurrentDiceSetView.startAnimation(mRollAnimation);
		}
	}

	public void setUpRollAreaForDiceSet(DiceSet diceSet) {
		// update roll area
		mDiceSet = diceSet;
		setDiceSet(diceSet);
    	mCurrentDiceSetView.setDiceSet(mDiceSet);
		mNameText.setText(mDiceSet.getName());
		roll(true);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
        	int overThreshold = 0;
        	float maxDelta = 0.0F;
        	for (int i = 0; i < mLastAcceleration.length; ++i) {
       			float delta = Math.abs(event.values[i] - mLastAcceleration[i]);
       			mLastAcceleration[i] = event.values[i];
       			if (delta > SHAKE_THRESHOLD) {
       				++overThreshold;
       			}
       			maxDelta = Math.max(maxDelta, delta);
            }
        	if (overThreshold > 1) {
        		onShaken(maxDelta);
        	}
        }
		// TODO Auto-generated method stub
		
	}
	
	public void onShaken(float magnatude) {
		roll(true);
	}

}
