package com.droiddice;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

public class HistoryActivity extends ListActivity {
	
	private ArrayList<String> history;
	
	@Override
	public void onCreate (Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
        	history = bundle.getStringArrayList("history");
        	
        	setListAdapter(new ArrayAdapter(this, 
        			android.R.layout.simple_list_item_1, history));
        }

	}
}