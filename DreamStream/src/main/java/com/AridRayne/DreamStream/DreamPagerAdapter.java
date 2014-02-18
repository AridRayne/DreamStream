package com.AridRayne.DreamStream;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.view.View;

public class DreamPagerAdapter extends PagerAdapter {

	private ArrayList<String> uris;
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public ArrayList<String> getUris() {
		return uris;
	}

	public void setUris(ArrayList<String> uris) {
		this.uris = uris;
	}

}
