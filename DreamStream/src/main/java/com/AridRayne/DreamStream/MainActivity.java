package com.AridRayne.DreamStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.AridRayne.DreamStream.DreamStream.ImageTarget;
import com.squareup.picasso.Picasso.LoadedFrom;

public class MainActivity extends Activity {

	ImageView iv;
	DreamStream dreamStream;
	AppTarget target;
	
    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
        iv = new ImageView(this);
        dreamStream = DreamStream.getInstance();
        dreamStream.initialize(this);
        setContentView(dreamStream.getViewPager());
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
    
	public class AppTarget extends ImageTarget {

		@Override
		public void onBitmapLoaded(Bitmap bitmap, LoadedFrom from) {
			super.onBitmapLoaded(bitmap, from);
			iv.setImageBitmap(bitmap);
		}
    }
    
}
