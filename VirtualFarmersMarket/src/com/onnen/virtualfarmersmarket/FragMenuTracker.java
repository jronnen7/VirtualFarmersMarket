package com.onnen.virtualfarmersmarket;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.support.v4.app.Fragment;

public class FragMenuTracker {

	LinkedList<Fragment> fragList;
	Fragment currentFrag;
	int max;
	public FragMenuTracker () {
		fragList = new LinkedList<Fragment>();
		max = 0;
	}
	public FragMenuTracker (int maxLength) {
		fragList = new LinkedList<Fragment>();
		max = maxLength;
	}
	
	public void Track(Fragment f) {
		Fragment prevFragment = currentFrag;
		if(prevFragment != f) {
			currentFrag = f;
			if(prevFragment != null) {
				fragList.add(prevFragment);
				if(max != 0 && max < fragList.size()) {
					fragList.removeFirst();
				}
			}
		}
	}
	
	public Fragment BackTrack() {
		Fragment ret = null;
		if(fragList.size() > 0) {
			ret = fragList.removeLast();
			currentFrag = ret;
		} return ret;
	}
}
