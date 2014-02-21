package com.AridRayne.DreamStream;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.jfeinstein.jazzyviewpager.JazzyViewPager;
import com.jfeinstein.jazzyviewpager.OutlineContainer;
import com.squareup.picasso.Picasso;

public class DreamPagerAdapter extends PagerAdapter {

	private ArrayList<String> uris;
	private Context context;
	private JazzyViewPager jvp;

	public void setContext(Context context) {
		this.context = context;
	}

	public void setViewPager(JazzyViewPager jvp) {
		this.jvp = jvp;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (uris == null)
			return 0;
		return uris.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		if (view instanceof OutlineContainer)
			return ((OutlineContainer) view).getChildAt(0) == obj;
		else
			return view == obj;
	}

	public ArrayList<String> getUris() {
		return uris;
	}

	public void setUris(ArrayList<String> uris) {
		this.uris = uris;
		this.notifyDataSetChanged();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		//Load an instance of the view containing the PhotoView and information bars to be added to the viewpager.
		View view = View.inflate(context, R.layout.view_layout, null);
//		TextView view = new TextView(context);
//		view.setText("TEST");
		container.addView(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//		((JazzyViewPager) container).addView(view);
//		container.addView(view);
		PhotoView pv = (PhotoView) view.findViewById(R.id.PhotoView);
		pv.setOnLongClickListener(DreamStream.getInstance());
		Picasso.with(context)
			.load(uris.get(position))
			.noFade()
			.into(pv);
		jvp.setObjectForPosition(view, position);
		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((JazzyViewPager) container).removeView((View) object);
	}
	
}
