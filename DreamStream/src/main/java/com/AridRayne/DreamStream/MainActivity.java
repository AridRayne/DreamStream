package com.AridRayne.DreamStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.AridRayne.DreamStream.DreamStream.ImageTarget;
import com.squareup.picasso.Picasso.LoadedFrom;

public class MainActivity extends Activity {

	ImageView iv;
	DreamStream dreamStream;
	AppTarget target;
//	JazzyViewPager viewPager;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iv = new ImageView(this);
        dreamStream = DreamStream.getInstance();
        dreamStream.initialize(this);
        setContentView(R.layout.viewpager_layout);
        target = new AppTarget();
        dreamStream.setTarget(target);
        dreamStream.start();
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
	public boolean onTouchEvent(MotionEvent event) {
    	return dreamStream.touchEvent(event) || super.onTouchEvent(event);
	}

	public class AppTarget extends ImageTarget {

		@Override
		public void onBitmapLoaded(Bitmap bitmap, LoadedFrom from) {
			super.onBitmapLoaded(bitmap, from);
			iv.setImageBitmap(bitmap);
		}
    }
    
}
