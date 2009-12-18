package com.droiddice;

import android.content.Context;
import android.test.AndroidTestCase;
import android.view.FocusFinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class RollAreaViewTest extends AndroidTestCase {

    private RollAreaView mRollAreaView;

	@Override
    protected void setUp() throws Exception {
        super.setUp();

        // inflate the layout
        final Context context = getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        View rollActivity = inflater.inflate(R.layout.roll_activity, null);

        // manually measure it, and lay it out
        rollActivity.measure(320, 480);
        rollActivity.layout(0, 0, 320, 480);

        mRollAreaView = (RollAreaView) rollActivity.findViewById(R.id.roll_activity_roll_area);
	}

    public void testPreconditions() {
        assertNotNull(mRollAreaView);
    }

}
