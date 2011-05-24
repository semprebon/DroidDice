package com.droiddice;

import android.view.Menu;
import android.view.MenuItem;

public class MenuTool {
	
	
    public static void showMultipleMenuItems( Menu menu, int[] viewIds ) {
    	MenuItem v;
    	
    	for (int i : viewIds) {
    		v = menu.findItem(i);
    		if (null != v) {
    			v.setVisible(true);
    		}
    	}
    }

    public static void hideMultipleMenuItems( Menu menu, int[] viewIds ) {
    	MenuItem v;
    	
    	for (int i : viewIds) {
    		v = menu.findItem(i);
    		if (null != v) {
    			v.setVisible(false);
    		}
    	}
    }
}