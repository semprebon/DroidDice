/* Copyright (C) 2009-2011-2011 Andrew Semprebon */
package com.droiddice.test;

import com.droiddice.RollAreaView;

import android.content.Context;
import android.test.AndroidTestCase;
import android.view.LayoutInflater;
import android.view.View;

public class RollAreaViewTest extends AndroidTestCase {

    private RollAreaView mRollAreaView;

	@Override
    protected void setUp() throws Exception {
        super.setUp();

        // inflate the layout
        final Context context = getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        View rollActivity = inflater.inflate(com.droiddice.R.layout.roll_activity, null);

        // manually measure it, and lay it out
        rollActivity.measure(320, 480);
        rollActivity.layout(0, 0, 320, 480);

        mRollAreaView = (RollAreaView) rollActivity.findViewById(com.droiddice.R.id.roll_activity_roll_area);
	}

    public void testPreconditions() {
        assertNotNull(mRollAreaView);
    }

}
